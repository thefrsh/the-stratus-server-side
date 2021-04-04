package io.github.thefrsh.stratus.controller;

import io.github.thefrsh.stratus.service.FriendInvitationService;
import io.github.thefrsh.stratus.service.UserService;
import io.github.thefrsh.stratus.transfer.request.ConversationIdRequest;
import io.github.thefrsh.stratus.transfer.request.UserIdRequest;
import io.github.thefrsh.stratus.transfer.response.ConversationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final FriendInvitationService friendInvitationService;

    @Autowired
    public UserController(UserService userService, FriendInvitationService friendInvitationService) {

        this.userService = userService;
        this.friendInvitationService = friendInvitationService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping(path = "/{userId}/friends/{friendId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ConversationResponse addFriend(@PathVariable Long userId, @PathVariable Long friendId) {

        return userService.addFriend(userId, friendId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{userId}/friends/{friendId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void removeFriend(@PathVariable Long userId, @PathVariable Long friendId,
                             @Valid @RequestBody ConversationIdRequest conversationIdRequest) {

        userService.removeFriend(userId, friendId, conversationIdRequest.getId());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/{userId}/invitations", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void inviteToFriends(@PathVariable Long userId, @Valid @RequestBody UserIdRequest friendIdTransfer) {

        friendInvitationService.inviteToFriends(friendIdTransfer.getId(), userId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{userId}/invitations/{invitationId}")
    public void declineInvitation(@PathVariable Long userId, @PathVariable Long invitationId) {

        friendInvitationService.declineInvitation(userId, invitationId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{userId}/conversations")
    public List<ConversationResponse> getConversations(@PathVariable Long userId) {

        return userService.getConversations(userId);
    }
}
