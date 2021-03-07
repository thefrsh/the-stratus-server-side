package io.github.thefrsh.stratus.service.implementation;

import io.github.thefrsh.stratus.model.Conversation;
import io.github.thefrsh.stratus.model.FriendInvitation;
import io.github.thefrsh.stratus.service.TransferConversionService;
import io.github.thefrsh.stratus.service.WebSocketService;
import io.github.thefrsh.stratus.transfer.websocket.InformationTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketServiceImpl implements WebSocketService
{
    private static final String TOPIC_PATH = "/topic/";

    private final SimpMessagingTemplate messagingTemplate;
    private final TransferConversionService conversionService;

    @Autowired
    public WebSocketServiceImpl(SimpMessagingTemplate messagingTemplate, TransferConversionService conversionService)
    {
        this.messagingTemplate = messagingTemplate;
        this.conversionService = conversionService;
    }

    @Override
    public void sendInformation(Long receiverId, String content)
    {
        messagingTemplate.convertAndSend(TOPIC_PATH + receiverId, new InformationTransfer(content));
    }

    @Override
    public void sendInvitation(Long receiverId, FriendInvitation invitation)
    {
        var invitationTransfer = conversionService.toFriendInvitationTransfer(invitation);

        messagingTemplate.convertAndSend(TOPIC_PATH + receiverId, invitationTransfer);
    }

    @Override
    public void sendConversation(Long receiverId, Conversation conversation)
    {
        var conversationTransfer = conversionService.toConversationTransfer(conversation);

        messagingTemplate.convertAndSend(TOPIC_PATH + receiverId, conversationTransfer);
    }

    @Override
    public void sendConversationRemove(Long receiverId, Conversation conversation)
    {
        var conversationRemove = conversionService.toConversationRemove(conversation);

        messagingTemplate.convertAndSend(TOPIC_PATH + receiverId, conversationRemove);
    }
}
