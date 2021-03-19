package io.github.thefrsh.stratus.transfer.websocket;

import io.github.thefrsh.stratus.transfer.response.UserResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FriendInvitationTransfer {
    private final TransferType type;
    private final Long id;
    private final UserResponse sender;
    private final LocalDateTime timestamp;

    @Builder
    public FriendInvitationTransfer(Long id, UserResponse sender, LocalDateTime timestamp) {
        this.type = TransferType.FRIEND_INVITATION;
        this.id = id;
        this.sender = sender;
        this.timestamp = timestamp;
    }
}
