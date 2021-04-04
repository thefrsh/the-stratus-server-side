package io.github.thefrsh.stratus.troubleshooting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyInvitedException extends RuntimeException {

    public UserAlreadyInvitedException(String message) {

        super(message);
    }
}
