package io.github.thefrsh.stratus.transfer.websocket;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConversationTransfer {
    private final TransferType type;

    private Long id;
    private List<String> participants;

    public ConversationTransfer() {
        type = TransferType.NEW_CONVERSATION;
    }
}
