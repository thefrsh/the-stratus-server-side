package io.github.thefrsh.stratus.troubleshooting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotThatUserException extends RuntimeException {
    public NotThatUserException(String message) {
        super(message);
    }
}
