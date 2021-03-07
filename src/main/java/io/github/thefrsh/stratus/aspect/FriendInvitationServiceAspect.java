package io.github.thefrsh.stratus.aspect;

import io.github.thefrsh.stratus.troubleshooting.exception.IdConflictException;
import io.github.thefrsh.stratus.troubleshooting.exception.NotThatUserException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FriendInvitationServiceAspect
{
    @Before(value = "execution(* io.github.thefrsh.stratus.service.implementation.FriendInvitationServiceImpl" +
            ".inviteToFriends(Long, Long)) && args(userId, friendId)", argNames = "userId, friendId")
    public void checkIfIdsAreDifferent(Long userId, Long friendId)
    {
        if (userId.equals(friendId))
        {
            throw new IdConflictException("User ids are the same");
        }

        var id = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getCredentials();

        if (!id.equals(userId))
        {
            throw new NotThatUserException("Your id is " + id + " but " + userId + " was sent in request body");
        }
    }
}
