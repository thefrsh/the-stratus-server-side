package io.github.thefrsh.stratus;

import com.ejlchina.okhttps.OkHttps;
import com.ejlchina.stomp.Stomp;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.thefrsh.stratus.model.Conversation;
import io.github.thefrsh.stratus.model.MessageState;
import io.github.thefrsh.stratus.model.User;
import io.github.thefrsh.stratus.repository.ChatMessageRepository;
import io.github.thefrsh.stratus.repository.ConversationJpaRepository;
import io.github.thefrsh.stratus.repository.UserJpaRepository;
import io.github.thefrsh.stratus.transfer.request.LoginCredentialsRequest;
import io.github.thefrsh.stratus.transfer.request.MessageRequest;
import io.github.thefrsh.stratus.transfer.response.MessageResponse;
import io.github.thefrsh.stratus.transfer.response.TokenResponse;
import io.github.thefrsh.stratus.transfer.websocket.MessageTransfer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureDataJpa
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConversationTest {

    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConversationJpaRepository conversationJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @LocalServerPort
    private Integer port;

    private User testUser;
    private User testFriend;
    private Conversation testConversation;
    private String userAuthorization;
    private String friendAuthorization;

    @BeforeEach
    public void setUp() throws Exception {

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

        var conversation = Conversation.builder()
                .id(1L)
                .participants(List.of(testUser, testFriend))
                .build();

        testConversation = conversationJpaRepository.save(conversation);

        userAuthorization = login("test");
        friendAuthorization = login("test2");
    }

    @AfterEach
    public void tearDown() {
        chatMessageRepository.deleteAll();
        conversationJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Test
    public void sendMessage_messageSentCorrect_shouldReturn201CreatedAndMessageResponse() throws Exception {

        var receivedMessages = new ArrayBlockingQueue<String>(1);

        var stomp = Stomp.over(OkHttps.webSocket(getWebsocketPath()));

        stomp.subscribe("/topic/" + testFriend.getId(), null, message -> receivedMessages.add(message.getPayload()));
        stomp.connect();

        var messageRequest = MessageRequest.builder()
                .senderId(1L)
                .content("hello")
                .build();

        var messageResponseJson = mockMvc.perform(post("/conversations/" + testConversation.getId() + "/messages")
                .header(HttpHeaders.AUTHORIZATION, userAuthorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var messageTransfer = objectMapper.readValue(receivedMessages.poll(2, TimeUnit.SECONDS), MessageTransfer.class);
        var messageResponse = objectMapper.readValue(messageResponseJson, MessageResponse.class);

        stomp.disconnect();

        chatMessageRepository.findById(messageResponse.getId())
                .peek(chatMessage -> {
                    assertEquals(testUser.getId(), chatMessage.getSender().getId());
                    assertEquals(messageRequest.getContent(), chatMessage.getContent());
                    assertEquals(MessageState.DELIVERED, chatMessage.getMessageState());
                    assertEquals(1L, chatMessage.getConversation().getId());
                })
                .onEmpty(Assertions::fail);

        assertNotNull(messageResponse.getId());
        assertNotNull(messageTransfer.getId());
        assertEquals(messageResponse.getId(), messageTransfer.getId());
        assertEquals(messageRequest.getContent(), messageResponse.getContent());
        assertEquals(messageRequest.getContent(), messageTransfer.getContent());
        assertEquals(testUser.getUsername(), messageTransfer.getSender());
    }

    private String getWebsocketPath() {
        return String.format("ws://localhost:%d/websocket", port);
    }

    private String login(String username) throws Exception {

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
