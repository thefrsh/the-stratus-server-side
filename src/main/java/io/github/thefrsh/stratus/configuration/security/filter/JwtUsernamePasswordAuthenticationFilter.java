package io.github.thefrsh.stratus.configuration.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.thefrsh.stratus.configuration.security.UserDetailsJpaAdapter;
import io.github.thefrsh.stratus.transfer.response.TokenResponse;
import io.github.thefrsh.stratus.transfer.request.LoginCredentialsRequest;
import io.github.thefrsh.stratus.troubleshooting.resolving.ServletExceptionResolver;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final ServletExceptionResolver resolver;
    private final String jwtSecret;
    private final int tokenExpirationTimeInDays;

    public JwtUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper, ServletExceptionResolver resolver,
                                                   String jwtSecret, int tokenExpirationTimeInDays) {

        this.objectMapper = objectMapper;
        this.resolver = resolver;
        this.jwtSecret = jwtSecret;
        this.tokenExpirationTimeInDays = tokenExpirationTimeInDays;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        try {

            var loginCredentials = objectMapper.readValue(request.getReader(), LoginCredentialsRequest.class);

            var authentication = new UsernamePasswordAuthenticationToken(
                    loginCredentials.getUsername(), loginCredentials.getPassword()
            );

            return getAuthenticationManager().authenticate(authentication);
        }
        catch (IOException exception) {

            try {
                resolver.resolveException(request, response, new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Incorrect body format, please provide username and password"));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException {

        var userDetails = (UserDetailsJpaAdapter) authResult.getPrincipal();

        var jwt = Jwts.builder()
                .setSubject(authResult.getName())
                .setId(Long.toString(userDetails.getId()))
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(tokenExpirationTimeInDays)))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();

        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), new TokenResponse(userDetails.getId(), jwt));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {

        resolver.resolveException(request, response, new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                failed.getMessage()));
    }
}
