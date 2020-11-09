package io.github.thefrsh.stratus.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.thefrsh.stratus.troubleshooting.ApiError;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtVerifyFilter extends OncePerRequestFilter
{
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String EMPTY_STRING = "";

    private final ObjectMapper objectMapper;
    private final String jwtSecret;

    public JwtVerifyFilter(String jwtSecret)
    {
        this.jwtSecret = jwtSecret;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException
    {
        var authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

        try
        {
            if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX))
            {
                throw new JwtException("Token is missing or it is in bad format");
            }

            var jwt = authorizationHeader.replace(BEARER_PREFIX, EMPTY_STRING);

            var claimsJws = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(jwt);

            var username = claimsJws.getBody().getSubject();

            var authenticationToken = new UsernamePasswordAuthenticationToken(username, null, null);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        catch (JwtException e)
        {
            var apiError = ApiError.builder()
                    .message(e.getMessage())
                    .status(HttpServletResponse.SC_UNAUTHORIZED)
                    .build();

            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(httpServletResponse.getOutputStream(), apiError);
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
