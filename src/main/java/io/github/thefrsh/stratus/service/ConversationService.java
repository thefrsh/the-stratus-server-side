package io.github.thefrsh.stratus.service;

import io.github.thefrsh.stratus.model.Conversation;
import io.github.thefrsh.stratus.model.User;
import io.github.thefrsh.stratus.transfer.response.MessageResponse;

import java.util.List;

public interface ConversationService {
    Conversation createConversation(List<User> participants);

    Conversation removeConversationBetween(List<User> participants, Long conversationId);

    MessageResponse sendMessage(Long id, Long senderId, String content);
}
