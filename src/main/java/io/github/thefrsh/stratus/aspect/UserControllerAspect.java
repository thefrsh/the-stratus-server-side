package io.github.thefrsh.stratus.aspect;

import io.github.thefrsh.stratus.troubleshooting.exception.NotThatUserException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserControllerAspect
{
    @Before("execution(* io.github.thefrsh.stratus.controller.UserController.*(Long,..)) && args(userId,..) &&" +
            "!execution(* io.github.thefrsh.stratus.controller.UserController.inviteToFriends(..))")
    public void validateUserId(Long userId)
    {
        var id = (Long) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getCredentials();

        if (!id.equals(userId))
        {
            throw new NotThatUserException("Your id is " + id + " but requested for resource assigned to " + userId);
        }
    }
}
