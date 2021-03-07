package io.github.thefrsh.stratus.service;

import io.github.thefrsh.stratus.model.Conversation;
import io.github.thefrsh.stratus.model.User;

import java.util.List;

public interface ConversationService
{
    Conversation createConversation(List<User> participants);

    Conversation removeConversationBetween(List<User> participants, Long conversationId);
}
