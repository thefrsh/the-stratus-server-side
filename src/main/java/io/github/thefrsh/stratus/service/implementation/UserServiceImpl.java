package io.github.thefrsh.stratus.service.implementation;

import io.github.thefrsh.stratus.model.FriendInvitation;
import io.github.thefrsh.stratus.model.User;
import io.github.thefrsh.stratus.repository.UserJpaRepository;
import io.github.thefrsh.stratus.service.*;
import io.github.thefrsh.stratus.transfer.response.ConversationResponse;
import io.github.thefrsh.stratus.troubleshooting.exception.UserFriendException;
import io.github.thefrsh.stratus.troubleshooting.exception.UserNotFoundException;
import io.vavr.collection.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService {
    private final WebSocketService webSocketService;
    private final ConversationService conversationService;
    private final UserJpaRepository userJpaRepository;
    private final TransferConversionService conversionService;
    private final FriendInvitationService friendInvitationService;

    @Autowired
    public UserServiceImpl(WebSocketService webSocketService, ConversationService conversationService,
                           UserJpaRepository userJpaRepository, TransferConversionService conversionService,
                           FriendInvitationService invitationService) {
        this.webSocketService = webSocketService;
        this.conversationService = conversationService;
        this.userJpaRepository = userJpaRepository;
        this.conversionService = conversionService;
        this.friendInvitationService = invitationService;
    }

    @Override
    @Transactional
    public ConversationResponse addFriend(Long userId, Long friendId) {
        var user = findUser(userId);
        var friend = findUser(friendId);

        if (areFriends(user, friendId)) {
            throw new UserFriendException("User " + friendId + " is actually friend of user with id " + userId);
        }

        return List.ofAll(user.getReceivedInvitations())
                .find(invitation -> invitation.getSender().getId().equals(friendId))
                .map(FriendInvitation::getId)
                .onEmpty(() -> {
                    throw new UserFriendException("User " + friendId + " has not invited you to friend list");
                })
                .map(invitationId -> {
                    user.getFriends().add(friend);

                    var conversation = conversationService.createConversation(Arrays.asList(user, friend));

                    friendInvitationService.removeInvitation(invitationId);

                    webSocketService.sendInformation(friendId, user.getUsername() + " accepted your friend invitation");
                    webSocketService.sendConversation(friendId, conversation);

                    return conversionService.toConversationResponse(conversation);
                })
                .get();
    }

    @Override
    @Transactional
    public void removeFriend(Long userId, Long friendId, Long conversationId) {
        var user = findUser(userId);

        if (!areFriends(user, friendId)) {
            throw new UserFriendException("User with id " + friendId + " is not a friend of user " + userId);
        }

        user.getFriends().removeIf(u -> u.getId().equals(friendId));
        user.getFriendsOf().removeIf(u -> u.getId().equals(friendId));

        var friend = findUser(friendId);

        var conversation = conversationService.removeConversationBetween(Arrays.asList(user, friend), conversationId);

        webSocketService.sendInformation(friendId, user.getUsername() + " removed you from the friends list");
        webSocketService.sendConversationRemove(friendId, conversation);
    }

    @Override
    @Transactional
    public User findUser(Long userId) {
        return userJpaRepository.findById(userId)
                .getOrElseThrow(() -> new UserNotFoundException("User with id " + userId + " does not exist"));
    }

    @Override
    @Transactional
    public java.util.List<ConversationResponse> getConversations(Long userId) {
        var user = findUser(userId);

        return List.ofAll(user.getConversations())
                .map(conversionService::toConversationResponse)
                .toJavaList();
    }

    private boolean areFriends(User user, Long friendId) {
        return List.ofAll(user.getFriends())
                .map(User::getId)
                .find(id -> id.equals(friendId))
                .isDefined();
    }
}
