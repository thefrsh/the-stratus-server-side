package io.github.thefrsh.stratus.troubleshooting;

import io.github.thefrsh.stratus.troubleshooting.exception.RequestBodyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestControllerExceptionHandler
{
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RequestBodyException.class)
    public ApiError handleRequestBodyException(Exception e)
    {
        return ApiError.builder()
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }
}
