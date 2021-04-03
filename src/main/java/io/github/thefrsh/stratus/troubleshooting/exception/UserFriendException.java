package io.github.thefrsh.stratus.troubleshooting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserFriendException extends RuntimeException {

    public UserFriendException(String message) {

        super(message);
    }
}
