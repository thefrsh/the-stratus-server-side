package io.github.thefrsh.stratus.transfer.websocket;

import io.github.thefrsh.stratus.model.MessageState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageStateTransfer {

    private final TransferType type;

    private final Long id;
    private final MessageState messageState;

    @Builder
    public MessageStateTransfer(Long id, MessageState messageState) {

        type = TransferType.CHAT_MESSAGE_STATE;

        this.id = id;
        this.messageState = messageState;
    }
}
