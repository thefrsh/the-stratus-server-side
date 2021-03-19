package io.github.thefrsh.stratus.configuration.security.filter;

import io.github.thefrsh.stratus.troubleshooting.resolving.ServletExceptionResolver;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtVerifyFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String EMPTY_STRING = "";

    private final UserDetailsService service;
    private final ServletExceptionResolver resolver;
    private final String jwtSecret;

    public JwtVerifyFilter(UserDetailsService service, ServletExceptionResolver resolver, String jwtSecret) {
        this.service = service;
        this.resolver = resolver;
        this.jwtSecret = jwtSecret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
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

            filterChain.doFilter(request, response);
        }
        catch (JwtException | UsernameNotFoundException e) {
            resolver.resolveException(request, response, new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    e.getMessage()));
        }
    }
}
