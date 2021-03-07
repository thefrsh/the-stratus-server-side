package io.github.thefrsh.stratus.repository;

import io.github.thefrsh.stratus.model.FriendInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendInvitationJpaRepository extends JpaRepository<FriendInvitation, Long>
{
}
