package io.github.thefrsh.stratus.service;

import io.github.thefrsh.stratus.model.ChatMessage;
import io.github.thefrsh.stratus.transfer.response.MessageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatMessageService {

    ChatMessage save(ChatMessage chatMessage);

    void updateStatus(Long conversationId, Long messageId);

    List<MessageResponse> getConversationMessages(Long conversationId, Pageable pageable);
}
