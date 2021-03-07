package io.github.thefrsh.stratus.troubleshooting;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
public class Errors
{
    private final Date timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    @Builder
    private Errors(Date timestamp, HttpStatus status, String message, String path)
    {
        this.timestamp = timestamp;
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = path;
    }
}
