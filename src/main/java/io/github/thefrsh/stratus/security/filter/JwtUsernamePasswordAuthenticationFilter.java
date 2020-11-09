package io.github.thefrsh.stratus.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.thefrsh.stratus.security.TokenResponse;
import io.github.thefrsh.stratus.security.UserLoginCredentials;
import io.github.thefrsh.stratus.troubleshooting.ApiError;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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
    private final int tokenExpirationTimeInDays;

    public JwtUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager, String jwtSecret,
                                                   int tokenExpirationTimeInDays)
    {
        this.authenticationManager = authenticationManager;
        this.jwtSecret = jwtSecret;
        this.tokenExpirationTimeInDays = tokenExpirationTimeInDays;

        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException
    {
        try
        {
            var loginCredentials = objectMapper.readValue(request.getReader(), UserLoginCredentials.class);

            var authentication = new UsernamePasswordAuthenticationToken(
                    loginCredentials.getUsername(), loginCredentials.getPassword()
            );

            return authenticationManager.authenticate(authentication);
        }
        catch (IOException exception)
        {
            var apiError = ApiError.builder()
                    .message("Incorrect body format, please provide username and password")
                    .status(HttpServletResponse.SC_BAD_REQUEST)
                    .build();

            try
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(response.getOutputStream(), apiError);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
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
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(tokenExpirationTimeInDays)))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();

        objectMapper.writeValue(response.getOutputStream(), new TokenResponse(jwt));
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
