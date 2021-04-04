package io.github.thefrsh.stratus.transfer.websocket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConversationRemoveTransfer {

    private final TransferType type;

    private Long id;

    public ConversationRemoveTransfer() {

        type = TransferType.REMOVE_CONVERSATION;
    }
}
