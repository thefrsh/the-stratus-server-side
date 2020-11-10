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
    @ExceptionHandler({RequestBodyException.class})
    public ResponseEntity<ApiError> handleBadRequestsException(Exception e)
    {
        var apiError = ApiError.builder()
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(apiError, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        var apiError = ApiError.builder()
                .message(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(apiError, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handlerUserAlreadyExistsException(Exception e)
    {
        var apiError = ApiError.builder()
                .message(e.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .build();

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(apiError, headers, HttpStatus.CONFLICT);
    }
}
