package io.github.thefrsh.stratus.service.implementation;

import io.github.thefrsh.stratus.model.Conversation;
import io.github.thefrsh.stratus.model.User;
import io.github.thefrsh.stratus.repository.ConversationJpaRepository;
import io.github.thefrsh.stratus.service.ConversationService;
import io.github.thefrsh.stratus.troubleshooting.exception.ConversationNotFoundException;
import io.github.thefrsh.stratus.troubleshooting.exception.NotAssociatedConversationException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService
{
    private final ConversationJpaRepository repository;

    @Autowired
    public ConversationServiceImpl(ConversationJpaRepository repository)
    {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Conversation createConversation(List<User> participants)
    {
        var conversation = new Conversation();
        conversation.setParticipants(participants);

        return repository.save(conversation);
    }

    @Override
    @Transactional
    public Conversation removeConversationBetween(List<User> participants, Long conversationId)
    {
        var conversation = repository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException("Conversation with id " + conversationId +
                        "does not exist"));

        if (!CollectionUtils.isEqualCollection(conversation.getParticipants(), participants))
        {
            throw new NotAssociatedConversationException("Provided users do not belong to conversation with id " +
                    conversationId);
        }

        repository.delete(conversation);

        return conversation;
    }
}
