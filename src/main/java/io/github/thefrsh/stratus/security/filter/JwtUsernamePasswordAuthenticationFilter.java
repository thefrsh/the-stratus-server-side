package io.github.thefrsh.stratus.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.thefrsh.stratus.security.TokenResponse;
import io.github.thefrsh.stratus.security.UserLoginCredentials;
import io.github.thefrsh.stratus.troubleshooting.ApiError;
import io.github.thefrsh.stratus.troubleshooting.exception.RequestBodyException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{
    private final AuthenticationManager authenticationManager;
    private final String jwtSecret;
    private final ObjectMapper objectMapper;

    public JwtUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager, String jwtSecret)
    {
        this.authenticationManager = authenticationManager;
        this.jwtSecret = jwtSecret;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException
    {
        try
        {
            var userCredentials = objectMapper.readValue(request.getReader(), UserLoginCredentials.class);

            var authentication = new UsernamePasswordAuthenticationToken(
                    userCredentials.getUsername(), userCredentials.getPassword()
            );

            return authenticationManager.authenticate(authentication);
        }
        catch (IOException e)
        {
            var apiError = ApiError.builder()
                    .message("Incorrect body format, please provide username and password")
                    .status(HttpServletResponse.SC_BAD_REQUEST)
                    .build();

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            try
            {
                objectMapper.writeValue(response.getOutputStream(), apiError);
            }
            catch (IOException exception)
            {
                throw new RuntimeException(exception);
            }
        }

        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException
    {
        var jwt = Jwts.builder()
                .setSubject(authResult.getName())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(2)))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();

        var tokenResponse = new TokenResponse(jwt);

        objectMapper.writeValue(response.getOutputStream(), tokenResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException
    {
        var apiError = ApiError.builder()
                .message(failed.getMessage())
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        objectMapper.writeValue(response.getOutputStream(), apiError);
    }
}
