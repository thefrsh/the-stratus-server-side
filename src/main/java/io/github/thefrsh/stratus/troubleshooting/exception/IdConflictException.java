package io.github.thefrsh.stratus.troubleshooting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class IdConflictException extends RuntimeException {

    public IdConflictException(String message) {

        super(message);
    }
}
