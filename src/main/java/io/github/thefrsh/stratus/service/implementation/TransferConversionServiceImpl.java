package io.github.thefrsh.stratus.service.implementation;

import io.github.thefrsh.stratus.model.ChatMessage;
import io.github.thefrsh.stratus.model.Conversation;
import io.github.thefrsh.stratus.model.FriendInvitation;
import io.github.thefrsh.stratus.model.User;
import io.github.thefrsh.stratus.service.TransferConversionService;
import io.github.thefrsh.stratus.transfer.request.RegisterCredentialsRequest;
import io.github.thefrsh.stratus.transfer.response.ConversationResponse;
import io.github.thefrsh.stratus.transfer.response.MessageResponse;
import io.github.thefrsh.stratus.transfer.response.UserResponse;
import io.github.thefrsh.stratus.transfer.websocket.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class TransferConversionServiceImpl implements TransferConversionService {

    private final ModelMapper modelMapper;

    @Autowired
    public TransferConversionServiceImpl(ModelMapper modelMapper) {

        this.modelMapper = modelMapper;
    }

    @Override
    public UserResponse toUserResponse(User user) {

        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public User toUser(RegisterCredentialsRequest credentialsRequest) {

        return modelMapper.map(credentialsRequest, User.class);
    }

    @Override
    public ConversationTransfer toConversationTransfer(Conversation conversation) {

        var conversationTransfer = new ConversationTransfer();
        conversationTransfer.setId(conversation.getId());

        var usernames = conversation.getParticipants().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());

        conversationTransfer.setParticipants(usernames);

        return conversationTransfer;
    }

    @Override
    public FriendInvitationTransfer toFriendInvitationTransfer(FriendInvitation invitation) {

        var senderUserTransfer = modelMapper.map(invitation.getSender(), UserResponse.class);

        return FriendInvitationTransfer.builder()
                .id(invitation.getId())
                .sender(senderUserTransfer)
                .timestamp(invitation.getTimestamp())
                .build();
    }

    @Override
    public ConversationRemoveTransfer toConversationRemove(Conversation conversation) {

        return modelMapper.map(conversation, ConversationRemoveTransfer.class);
    }

    @Override
    public ConversationResponse toConversationResponse(Conversation conversation) {

        var conversationResponse = new ConversationResponse();
        conversationResponse.setId(conversation.getId());

        var usernames = conversation.getParticipants().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());

        conversationResponse.setParticipants(usernames);

        return conversationResponse;
    }

    @Override
    public MessageResponse toMessageResponse(ChatMessage chatMessage) {

        return modelMapper.map(chatMessage, MessageResponse.class);
    }

    @Override
    public MessageTransfer toMessageTransfer(ChatMessage chatMessage) {

        return MessageTransfer.builder()
                .id(chatMessage.getId())
                .sender(chatMessage.getSender().getUsername())
                .conversationId(chatMessage.getConversation().getId())
                .content(chatMessage.getContent())
                .sendTime(chatMessage.getSendTime())
                .messageState(chatMessage.getMessageState())
                .build();
    }

    @Override
    public MessageStateTransfer toMessageStateTransfer(ChatMessage chatMessage) {

        return modelMapper.map(chatMessage, MessageStateTransfer.class);
    }
}
