package io.github.thefrsh.stratus.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.thefrsh.stratus.security.UserLoginCredentials;
import io.github.thefrsh.stratus.troubleshooting.exception.RequestBodyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{
    private final AuthenticationManager authenticationManager;

    public JwtUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException
    {
        try
        {
            var userCredentials = new ObjectMapper().readValue(request.getReader(), UserLoginCredentials.class);

            var authentication = new UsernamePasswordAuthenticationToken(
                    userCredentials.getUsername(), userCredentials.getPassword()
            );

            return authenticationManager.authenticate(authentication);
        }
        catch (IOException e)
        {
            throw new RequestBodyException();
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException
    {
        super.successfulAuthentication(request, response, chain, authResult);
    }
}
