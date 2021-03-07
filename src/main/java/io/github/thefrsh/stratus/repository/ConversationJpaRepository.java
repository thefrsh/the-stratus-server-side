package io.github.thefrsh.stratus.repository;

import io.github.thefrsh.stratus.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationJpaRepository extends JpaRepository<Conversation, Long>
{
}
