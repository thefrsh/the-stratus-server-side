package io.github.thefrsh.stratus.service;

public interface FriendInvitationService {

    void inviteToFriends(Long senderId, Long friendId);

    void removeInvitation(Long invitationId);

    void declineInvitation(Long userId, Long invitationId);
}
