package io.github.thefrsh.stratus.service;

import io.github.thefrsh.stratus.model.Conversation;
import io.github.thefrsh.stratus.model.FriendInvitation;

public interface WebSocketService
{
    void sendInformation(Long receiverId, String content);

    void sendInvitation(Long receiverId, FriendInvitation invitation);

    void sendConversation(Long receiverId, Conversation conversation);

    void sendConversationRemove(Long receiverId, Conversation conversation);
}
