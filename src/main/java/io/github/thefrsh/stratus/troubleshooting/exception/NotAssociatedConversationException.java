package io.github.thefrsh.stratus.troubleshooting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotAssociatedConversationException extends RuntimeException {

    public NotAssociatedConversationException(String message) {

        super(message);
    }
}
