package io.github.thefrsh.stratus.service.implementation;

import io.github.thefrsh.stratus.model.ChatMessage;
import io.github.thefrsh.stratus.model.Conversation;
import io.github.thefrsh.stratus.model.MessageState;
import io.github.thefrsh.stratus.model.User;
import io.github.thefrsh.stratus.repository.ConversationJpaRepository;
import io.github.thefrsh.stratus.service.ChatMessageService;
import io.github.thefrsh.stratus.service.ConversationService;
import io.github.thefrsh.stratus.service.TransferConversionService;
import io.github.thefrsh.stratus.service.WebSocketService;
import io.github.thefrsh.stratus.transfer.response.MessageResponse;
import io.github.thefrsh.stratus.troubleshooting.exception.ConversationNotFoundException;
import io.github.thefrsh.stratus.troubleshooting.exception.NotAssociatedConversationException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final ConversationJpaRepository repository;
    private final WebSocketService webSocketService;
    private final TransferConversionService conversionService;
    private final ChatMessageService chatMessageService;

    @Autowired
    public ConversationServiceImpl(ConversationJpaRepository repository, WebSocketService webSocketService,
                                   TransferConversionService conversionService, ChatMessageService chatMessageService) {

        this.repository = repository;
        this.webSocketService = webSocketService;
        this.conversionService = conversionService;
        this.chatMessageService = chatMessageService;
    }

    @Override
    @Transactional
    public Conversation createConversation(List<User> participants) {

        var conversation = new Conversation();
        conversation.setParticipants(participants);

        return repository.save(conversation);
    }

    @Override
    @Transactional
    public Conversation removeConversationBetween(List<User> participants, Long conversationId) {

        var conversation = findConversation(conversationId);

        if (!CollectionUtils.isEqualCollection(conversation.getParticipants(), participants)) {
            throw new NotAssociatedConversationException("Provided users do not belong to conversation with id " +
                    conversationId);
        }

        repository.deleteById(conversationId);

        return conversation;
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(Long conversationId, Long senderId, String content) {

        var conversation = findConversation(conversationId);

        return io.vavr.collection.List.ofAll(conversation.getParticipants())
                .find(user -> user.getId().equals(senderId))
                .onEmpty(() -> {
                    throw new NotAssociatedConversationException("User with id " + senderId + " does not belong to " +
                            "conversation with id " + conversationId);
                })
                .map(user -> {
                    var message = ChatMessage.builder()
                            .sender(user)
                            .content(content)
                            .conversation(conversation)
                            .messageState(MessageState.DELIVERED)
                            .sendTime(LocalDateTime.now())
                            .build();

                    var savedMessage = chatMessageService.save(message);

                    io.vavr.collection.List.ofAll(conversation.getParticipants())
                            .map(User::getId)
                            .filter(id -> !id.equals(senderId))
                            .forEach(id -> webSocketService.sendChatMessage(id, savedMessage));

                    return conversionService.toMessageResponse(savedMessage);
                })
                .get();
    }

    private Conversation findConversation(Long id) {

        return repository.findById(id)
                .getOrElseThrow(() -> new ConversationNotFoundException("Conversation with id " + id + " has not " +
                        "been found"));
    }
}
