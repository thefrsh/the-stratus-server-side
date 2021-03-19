package io.github.thefrsh.stratus.aspect;

import io.github.thefrsh.stratus.troubleshooting.exception.IdConflictException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserServiceAspect {
    @Before(value = "execution(* io.github.thefrsh.stratus.service.implementation.UserServiceImpl.*(Long, Long,..)) &&" +
            "args(userId, friendId,..)", argNames = "userId, friendId")
    public void checkIfIdsAreDifferent(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new IdConflictException("User ids are the same");
        }
    }
}
