package io.github.thefrsh.stratus.troubleshooting.resolving;

import io.github.thefrsh.stratus.troubleshooting.Errors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.Objects;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Errors> handleMethodArgumentNotValidException(HttpServletRequest request,
                                                                        MethodArgumentNotValidException e) {

        var message = Objects.requireNonNull(e.getBindingResult()
                .getFieldError())
                .getDefaultMessage();

        var errors = Errors.builder()
                .timestamp(new Date())
                .status(HttpStatus.BAD_REQUEST)
                .message(message)
                .path(request.getServletPath())
                .build();

        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return new ResponseEntity<>(errors, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NumberFormatException.class, HttpMessageNotReadableException.class,
            ConstraintViolationException.class})
    public ResponseEntity<Errors> handleNumberFormatException(HttpServletRequest request, Exception e) {

        var errors = Errors.builder()
                .timestamp(new Date())
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .path(request.getServletPath())
                .build();

        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return new ResponseEntity<>(errors, headers, HttpStatus.BAD_REQUEST);
    }
}
