package io.github.thefrsh.stratus.service.implementation;

import io.github.thefrsh.stratus.model.ChatMessage;
import io.github.thefrsh.stratus.model.MessageState;
import io.github.thefrsh.stratus.model.User;
import io.github.thefrsh.stratus.repository.ChatMessageRepository;
import io.github.thefrsh.stratus.service.ChatMessageService;
import io.github.thefrsh.stratus.service.TransferConversionService;
import io.github.thefrsh.stratus.service.WebSocketService;
import io.github.thefrsh.stratus.transfer.response.MessageResponse;
import io.github.thefrsh.stratus.troubleshooting.exception.BadMessageStateException;
import io.github.thefrsh.stratus.troubleshooting.exception.ChatMessageNotFoundException;
import io.github.thefrsh.stratus.troubleshooting.exception.MessageConversationException;
import io.vavr.collection.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final TransferConversionService conversionService;
    private final WebSocketService webSocketService;

    @Autowired
    public ChatMessageServiceImpl(ChatMessageRepository chatMessageRepository,
                                  TransferConversionService conversionService, WebSocketService webSocketService) {
        this.chatMessageRepository = chatMessageRepository;
        this.conversionService = conversionService;
        this.webSocketService = webSocketService;
    }

    @Override
    @Transactional
    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    @Override
    @Transactional
    public void updateStatus(Long conversationId, Long messageId) {
        var message = findChatMessage(messageId);

        List.of(message)
                .find(msg -> msg.getConversation().getId().equals(conversationId))
                .onEmpty(() -> {
                    throw new MessageConversationException("Message with id " + messageId + " does not belong to " +
                            "conversation with id " + conversationId);
                })
                .map(ChatMessage::getMessageState)
                .filter(messageState -> messageState.equals(MessageState.DELIVERED))
                .onEmpty(() -> {
                    throw new BadMessageStateException("You cannot change message state which is: " +
                            message.getMessageState());
                })
                .peek(messageState -> {
                    message.setMessageState(MessageState.READ);

                    List.ofAll(message.getConversation().getParticipants())
                            .map(User::getId)
                            .filter(id -> !message.getSender().getId().equals(id))
                            .forEach(id -> webSocketService.sendChatMessageState(id, message));
                });
    }

    @Override
    @Transactional
    public java.util.List<MessageResponse> getConversationMessages(Long conversationId, Pageable pageable) {
        return chatMessageRepository.findAllByConversationId(conversationId, pageable)
                .map(conversionService::toMessageResponse)
                .toJavaList();
    }

    private ChatMessage findChatMessage(Long id) {
        return chatMessageRepository.findById(id)
                .getOrElseThrow(() -> new ChatMessageNotFoundException("Message with id " + id + " has not been " +
                        "found"));
    }
}
