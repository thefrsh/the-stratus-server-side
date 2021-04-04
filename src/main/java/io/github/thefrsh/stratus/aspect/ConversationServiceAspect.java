package io.github.thefrsh.stratus.aspect;

import io.github.thefrsh.stratus.troubleshooting.exception.NotThatUserException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ConversationServiceAspect {

    @Before(value = "execution(* io.github.thefrsh.stratus.service.implementation.ConversationServiceImpl.*" +
            "(*, Long, *)) && args(*, senderId, *)", argNames = "senderId")
    public void validateUserId(Long senderId) {

        var id = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getCredentials();

        if (!id.equals(senderId)) {
            throw new NotThatUserException("Your id is " + id + " but " + senderId + " was sent in request body");
        }
    }
}
