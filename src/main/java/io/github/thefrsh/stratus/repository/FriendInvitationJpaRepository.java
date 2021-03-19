package io.github.thefrsh.stratus.repository;

import io.github.thefrsh.stratus.model.FriendInvitation;
import io.vavr.control.Option;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.lang.NonNull;

public interface FriendInvitationJpaRepository extends Repository<FriendInvitation, Long> {
    FriendInvitation save(FriendInvitation friendInvitation);

    Option<FriendInvitation> findById(Long id);

    boolean existsById(Long id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "delete from friend_invitations where id = ?1", nativeQuery = true)
    void deleteById(@NonNull Long id);
}
