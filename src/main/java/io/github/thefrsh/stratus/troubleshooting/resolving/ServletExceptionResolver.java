package io.github.thefrsh.stratus.troubleshooting.resolving;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.thefrsh.stratus.troubleshooting.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class ServletExceptionResolver {
    private final ObjectMapper objectMapper;

    @Autowired
    public ServletExceptionResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void resolveException(HttpServletRequest request, HttpServletResponse response,
                                 ResponseStatusException e) throws IOException {
        var errors = Errors.builder()
                .timestamp(new Date())
                .status(e.getStatus())
                .message(e.getReason())
                .path(request.getServletPath())
                .build();

        response.setStatus(e.getStatus().value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getOutputStream(), errors);
    }
}
