package io.github.thefrsh.stratus.repository;

import io.github.thefrsh.stratus.model.Conversation;
import io.vavr.control.Option;
import org.springframework.data.repository.Repository;

public interface ConversationJpaRepository extends Repository<Conversation, Long> {
    Option<Conversation> findById(Long id);

    Conversation save(Conversation conversation);

    void deleteById(Long id);

    boolean existsById(Long id);

    void deleteAll();
}
