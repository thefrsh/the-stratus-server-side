package io.github.thefrsh.stratus.service;

import io.github.thefrsh.stratus.model.User;
import io.github.thefrsh.stratus.transfer.response.ConversationResponse;

import java.util.List;

public interface UserService {

    ConversationResponse addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId, Long conversationId);

    User findUser(Long userId);

    List<ConversationResponse> getConversations(Long userId);
}
