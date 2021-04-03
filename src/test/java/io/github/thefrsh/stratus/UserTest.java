package io.github.thefrsh.stratus;

import com.ejlchina.okhttps.OkHttps;
import com.ejlchina.stomp.Stomp;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.thefrsh.stratus.model.Conversation;
import io.github.thefrsh.stratus.model.FriendInvitation;
import io.github.thefrsh.stratus.model.User;
import io.github.thefrsh.stratus.repository.ConversationJpaRepository;
import io.github.thefrsh.stratus.repository.FriendInvitationJpaRepository;
import io.github.thefrsh.stratus.repository.UserJpaRepository;
import io.github.thefrsh.stratus.transfer.request.ConversationIdRequest;
import io.github.thefrsh.stratus.transfer.request.LoginCredentialsRequest;
import io.github.thefrsh.stratus.transfer.request.UserIdRequest;
import io.github.thefrsh.stratus.transfer.response.ConversationResponse;
import io.github.thefrsh.stratus.transfer.response.TokenResponse;
import io.github.thefrsh.stratus.transfer.websocket.*;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureDataJpa
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTest
{
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private FriendInvitationJpaRepository invitationJpaRepository;

    @Autowired
    private ConversationJpaRepository conversationJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private Integer port;

    private User testUser;
    private User testFriend;
    private String userAuthorization;
    private String friendAuthorization;

    @BeforeEach
    public void setUp() throws Exception
    {
        var user = User.builder()
                .id(1L)
                .username("test")
                .email("test@test.com")
                .password(passwordEncoder.encode("test"))
                .build();

        var friend = User.builder()
                .id(2L)
                .username("test2")
                .email("test2@test.com")
                .password(passwordEncoder.encode("test"))
                .build();

        testUser = userJpaRepository.save(user);
        testFriend = userJpaRepository.save(friend);

        userAuthorization = login("test");
        friendAuthorization = login("test2");
    }

    @AfterEach
    public void tearDown()
    {
        userJpaRepository.deleteAll();
    }

    @Test
    public void friendInvitationAttempt_sendInvitation_shouldReturn201Created() throws Exception
    {
        var receivedMessages = new ArrayBlockingQueue<String>(1);

        var stomp = Stomp.over(OkHttps.webSocket(getWebsocketPath()));

        stomp.subscribe("/topic/" + testUser.getId(), null, message -> receivedMessages.add(message.getPayload()));
        stomp.connect();

        var friendIdRequest = new UserIdRequest();
        friendIdRequest.setId(testFriend.getId());

        mockMvc.perform(post("/users/" + testUser.getId() + "/invitations")
                .header(HttpHeaders.AUTHORIZATION, friendAuthorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(friendIdRequest)))
                    .andExpect(status().isCreated());

        var invitation = objectMapper.readValue(receivedMessages.poll(2, TimeUnit.SECONDS),
                FriendInvitationTransfer.class);

        stomp.disconnect();

        invitationJpaRepository.findById(invitation.getId())
                .peek(inv -> {
                    assertEquals(testUser.getId(), inv.getReceiver().getId());
                    assertEquals(testFriend.getId(), inv.getSender().getId());
                })
                .onEmpty(Assertions::fail);

        assertEquals(TransferType.FRIEND_INVITATION, invitation.getType());
        assertEquals(testFriend.getId(), invitation.getSender().getId());
    }

    @Test
    public void friendInvitationAttempt_acceptInvitation_shouldReturn201CreatedWithConversationResponse()
            throws Exception
    {
        var receivedMessages = new ArrayBlockingQueue<String>(2);

        var stomp = Stomp.over(OkHttps.webSocket(getWebsocketPath()));
        stomp.subscribe("/topic/" + testFriend.getId(), null, message -> receivedMessages.add(message.getPayload()));
        stomp.connect();

        var invitation = FriendInvitation.builder()
                .id(1L)
                .timestamp(LocalDateTime.now())
                .sender(testFriend)
                .receiver(testUser)
                .build();

        var savedInvitation = invitationJpaRepository.save(invitation);

        var conversationString = mockMvc.perform(put("/users/" + testUser.getId() + "/friends/" + testFriend.getId())
            .header(HttpHeaders.AUTHORIZATION, userAuthorization))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var conversationResponse = objectMapper.readValue(conversationString, ConversationResponse.class);

        var informationTransfer = objectMapper.readValue(receivedMessages.poll(2, TimeUnit.SECONDS),
                InformationTransfer.class);

        var conversationTransfer = objectMapper.readValue(receivedMessages.poll(2, TimeUnit.SECONDS),
                ConversationTransfer.class);

        stomp.disconnect();

        var participants = List.of("test", "test2");

        assertEquals(TransferType.INFORMATION, informationTransfer.getType());
        assertEquals(TransferType.NEW_CONVERSATION, conversationTransfer.getType());
        assertEquals(conversationResponse.getId(), conversationTransfer.getId());
        assertTrue(conversationJpaRepository.existsById(conversationResponse.getId()));
        assertTrue(CollectionUtils.isEqualCollection(participants, conversationResponse.getParticipants()));
        assertTrue(CollectionUtils.isEqualCollection(participants, conversationTransfer.getParticipants()));
        assertFalse(invitationJpaRepository.existsById(savedInvitation.getId()));
    }

    @Test
    public void friendInvitationAttempt_declineInvitation_shouldReturn204NoContent() throws Exception
    {
        var receivedMessages = new ArrayBlockingQueue<String>(1);

        var stomp = Stomp.over(OkHttps.webSocket(getWebsocketPath()));
        stomp.subscribe("/topic/" + testFriend.getId(), null, message -> receivedMessages.add(message.getPayload()));
        stomp.connect();

        var invitation = FriendInvitation.builder()
                .id(1L)
                .timestamp(LocalDateTime.now())
                .sender(testFriend)
                .receiver(testUser)
                .build();

        var savedInvitation = invitationJpaRepository.save(invitation);

        mockMvc.perform(delete("/users/" + testUser.getId() + "/invitations/" + savedInvitation.getId())
            .header(HttpHeaders.AUTHORIZATION, userAuthorization))
                .andExpect(status().isNoContent());

        var informationTransfer = objectMapper.readValue(receivedMessages.poll(2, TimeUnit.SECONDS),
                InformationTransfer.class);

        stomp.disconnect();

        assertEquals(TransferType.INFORMATION, informationTransfer.getType());
        assertFalse(invitationJpaRepository.existsById(savedInvitation.getId()));
    }

    @Test
    public void friendInvitationAttempt_removeFriend_shouldReturn204NoContent() throws Exception
    {
        var receivedMessages = new ArrayBlockingQueue<String>(2);

        var stomp = Stomp.over(OkHttps.webSocket(getWebsocketPath()));
        stomp.subscribe("/topic/" + testFriend.getId(), null, message -> receivedMessages.add(message.getPayload()));
        stomp.connect();

        var conversation = Conversation.builder()
                .id(1L)
                .participants(List.of(testUser, testFriend))
                .build();

        var savedConversation = conversationJpaRepository.save(conversation);

        userJpaRepository.findById(testUser.getId()).peek(user -> {
            user.getFriends().add(testFriend);
            userJpaRepository.save(user);
        })
        .onEmpty(Assertions::fail);

        userJpaRepository.flush();

        var conversationIdRequest = new ConversationIdRequest(savedConversation.getId());

        mockMvc.perform(delete("/users/" + testUser.getId() + "/friends/" + testFriend.getId())
            .header(HttpHeaders.AUTHORIZATION, userAuthorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(conversationIdRequest)))
                .andExpect(status().isNoContent());

        var informationTransfer = objectMapper.readValue(receivedMessages.poll(2, TimeUnit.SECONDS),
                InformationTransfer.class);

        var conversationRemoveTransfer = objectMapper.readValue(receivedMessages.poll(2, TimeUnit.SECONDS),
                ConversationRemoveTransfer.class);

        assertEquals(TransferType.INFORMATION, informationTransfer.getType());
        assertEquals(savedConversation.getId(), conversationRemoveTransfer.getId());
        assertFalse(conversationJpaRepository.existsById(savedConversation.getId()));
    }

    private String getWebsocketPath()
    {
        return String.format("ws://localhost:%d/websocket", port);
    }

    private String login(String username) throws Exception
    {
        var loginCredentials = LoginCredentialsRequest.builder()
                .username(username)
                .password("test")
                .build();

        var stringResponse = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginCredentials)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var tokenResponse = objectMapper.readValue(stringResponse, TokenResponse.class);

        return BEARER_PREFIX + tokenResponse.getToken();
    }
}
