package io.github.thefrsh.stratus.service.implementation;

import io.github.thefrsh.stratus.model.FriendInvitation;
import io.github.thefrsh.stratus.repository.FriendInvitationJpaRepository;
import io.github.thefrsh.stratus.service.FriendInvitationService;
import io.github.thefrsh.stratus.service.UserService;
import io.github.thefrsh.stratus.service.WebSocketService;
import io.github.thefrsh.stratus.troubleshooting.exception.UserAlreadyInvitedException;
import io.github.thefrsh.stratus.troubleshooting.exception.UserFriendException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FriendInvitationServiceImpl implements FriendInvitationService
{
    private final FriendInvitationJpaRepository friendInvitationJpaRepository;
    private final UserService userService;
    private final WebSocketService webSocketService;

    @Autowired
    public FriendInvitationServiceImpl(FriendInvitationJpaRepository friendInvitationJpaRepository,
                                       @Lazy UserService userService, WebSocketService webSocketService)
    {
        this.friendInvitationJpaRepository = friendInvitationJpaRepository;
        this.userService = userService;
        this.webSocketService = webSocketService;
    }

    @Override
    @Transactional
    public void inviteToFriends(Long senderId, Long receiverId)
    {
        var receiver = userService.findUser(receiverId);

        receiver.getReceivedInvitations().stream()
                .map(invitation -> invitation.getSender().getId())
                .filter(id -> id.equals(senderId))
                .findAny()
                .ifPresent(user -> {
                    throw new UserAlreadyInvitedException("You have already invited user with id " + receiverId);
                });

        var sender = userService.findUser(senderId);

        var invitation = new FriendInvitation();
        invitation.setTimestamp(LocalDateTime.now());

        var persistedInvitation = friendInvitationJpaRepository.save(invitation);

        sender.getSentInvitations().add(persistedInvitation);
        receiver.getReceivedInvitations().add(persistedInvitation);

        persistedInvitation.setSender(sender);
        persistedInvitation.setReceiver(receiver);

        webSocketService.sendInvitation(receiverId, persistedInvitation);
    }

    @Override
    @Transactional
    public void removeInvitation(Long invitationId)
    {
        friendInvitationJpaRepository.deleteById(invitationId);
    }

    @Override
    @Transactional
    public void declineInvitation(Long userId, Long invitationId)
    {
        var user = userService.findUser(userId);

        var invitation = user.getReceivedInvitations().stream()
                .filter(inv -> inv.getId().equals(invitationId))
                .findFirst()
                .orElseThrow(() -> new UserFriendException("You have not invitation with id " + invitationId));

        friendInvitationJpaRepository.delete(invitation);
        webSocketService.sendInformation(invitation.getSender().getId(), "User " + user.getUsername() + " declined " +
                "your invitation");
    }
}
