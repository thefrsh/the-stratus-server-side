package io.github.thefrsh.stratus.service;

import io.github.thefrsh.stratus.model.ChatMessage;
import io.github.thefrsh.stratus.model.Conversation;
import io.github.thefrsh.stratus.model.FriendInvitation;
import io.github.thefrsh.stratus.model.User;
import io.github.thefrsh.stratus.transfer.request.RegisterCredentialsRequest;
import io.github.thefrsh.stratus.transfer.response.ConversationResponse;
import io.github.thefrsh.stratus.transfer.response.MessageResponse;
import io.github.thefrsh.stratus.transfer.response.UserResponse;
import io.github.thefrsh.stratus.transfer.websocket.*;

public interface TransferConversionService {
    UserResponse toUserResponse(User user);

    User toUser(RegisterCredentialsRequest credentialsRequest);

    ConversationTransfer toConversationTransfer(Conversation conversation);

    FriendInvitationTransfer toFriendInvitationTransfer(FriendInvitation invitation);

    ConversationRemoveTransfer toConversationRemove(Conversation conversation);

    ConversationResponse toConversationResponse(Conversation conversation);

    MessageResponse toMessageResponse(ChatMessage chatMessage);

    MessageTransfer toMessageTransfer(ChatMessage chatMessage);

    MessageStateTransfer toMessageStateTransfer(ChatMessage chatMessage);
}
