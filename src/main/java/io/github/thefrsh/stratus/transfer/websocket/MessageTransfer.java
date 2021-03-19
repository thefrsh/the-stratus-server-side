package io.github.thefrsh.stratus.transfer.websocket;

import io.github.thefrsh.stratus.model.MessageState;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class MessageTransfer {
    TransferType type;

    Long id;
    String sender;
    Long conversationId;
    String content;
    LocalDateTime sendTime;
    MessageState messageState;

    @Builder
    public MessageTransfer(Long id, String sender, Long conversationId, String content, LocalDateTime sendTime,
                           MessageState messageState) {
        this.type = TransferType.CHAT_MESSAGE;

        this.id = id;
        this.sender = sender;
        this.conversationId = conversationId;
        this.content = content;
        this.sendTime = sendTime;
        this.messageState = messageState;
    }
}
