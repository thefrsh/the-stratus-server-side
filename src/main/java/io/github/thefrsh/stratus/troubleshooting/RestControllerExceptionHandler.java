package io.github.thefrsh.stratus.troubleshooting;

import io.github.thefrsh.stratus.troubleshooting.exception.RequestBodyException;
import io.github.thefrsh.stratus.troubleshooting.exception.UserAlreadyExistsException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class RestControllerExceptionHandler
{
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        return handleCommonError(HttpStatus.BAD_REQUEST, Objects.requireNonNull(e.getBindingResult()
                .getFieldError())
                .getDefaultMessage());
    }

    @ExceptionHandler({RequestBodyException.class})
    public ResponseEntity<ApiError> handleBadRequestsException(Exception e)
    {
        return handleCommonError(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({UserAlreadyExistsException.class})
    public ResponseEntity<ApiError> handleConflictExceptions(Exception e)
    {
        return handleCommonError(HttpStatus.CONFLICT, e.getMessage());
    }

    private ResponseEntity<ApiError> handleCommonError(HttpStatus status, String message)
    {
        var apiError = ApiError.builder()
                .message(message)
                .status(status.value())
                .build();

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(apiError, headers, status);
    }
}
