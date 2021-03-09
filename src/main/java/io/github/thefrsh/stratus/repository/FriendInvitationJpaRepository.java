package io.github.thefrsh.stratus.repository;

import io.github.thefrsh.stratus.model.FriendInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

public interface FriendInvitationJpaRepository extends JpaRepository<FriendInvitation, Long>
{
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "delete from friend_invitations where id = ?1", nativeQuery = true)
    void deleteById(@NonNull Long id);
}
