package io.github.thefrsh.stratus.service;

import io.github.thefrsh.stratus.model.Conversation;
import io.github.thefrsh.stratus.model.FriendInvitation;
import io.github.thefrsh.stratus.transfer.response.ConversationResponse;
import io.github.thefrsh.stratus.transfer.websocket.ConversationRemoveTransfer;
import io.github.thefrsh.stratus.transfer.websocket.ConversationTransfer;
import io.github.thefrsh.stratus.transfer.websocket.FriendInvitationTransfer;

public interface TransferConversionService
{
    ConversationTransfer toConversationTransfer(Conversation conversation);

    FriendInvitationTransfer toFriendInvitationTransfer(FriendInvitation invitation);

    ConversationRemoveTransfer toConversationRemove(Conversation conversation);

    ConversationResponse toConversationResponse(Conversation conversation);
}
