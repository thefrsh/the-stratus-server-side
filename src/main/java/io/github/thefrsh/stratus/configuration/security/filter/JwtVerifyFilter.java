package io.github.thefrsh.stratus.configuration.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.thefrsh.stratus.troubleshooting.ApiError;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    private final String jwtSecret;
    private final UserDetailsService service;
    private final ObjectMapper objectMapper;

    public JwtVerifyFilter(String jwtSecret, UserDetailsService service)
    {
        this.jwtSecret = jwtSecret;
        this.service = service;

        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    @NonNull HttpServletResponse httpServletResponse,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException
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
            var userId = Long.parseLong(claimsJws.getBody().getId());

            service.loadUserByUsername(username);

            var authenticationToken = new UsernamePasswordAuthenticationToken(username, userId, null);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
        catch (JwtException | UsernameNotFoundException e)
        {
            var apiError = ApiError.builder()
                    .message(e.getMessage())
                    .status(HttpServletResponse.SC_UNAUTHORIZED)
                    .build();

            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(httpServletResponse.getOutputStream(), apiError);
        }
    }
}
