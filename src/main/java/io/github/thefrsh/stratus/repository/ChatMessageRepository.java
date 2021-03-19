package io.github.thefrsh.stratus.repository;

import io.github.thefrsh.stratus.model.ChatMessage;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

public interface ChatMessageRepository extends Repository<ChatMessage, Long> {
    ChatMessage save(ChatMessage chatMessage);

    Option<ChatMessage> findById(Long id);

    List<ChatMessage> findAllByConversationId(Long id, Pageable pageable);
}
