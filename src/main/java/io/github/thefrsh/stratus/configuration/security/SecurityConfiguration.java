package io.github.thefrsh.stratus.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.thefrsh.stratus.configuration.security.filter.JwtUsernamePasswordAuthenticationFilter;
import io.github.thefrsh.stratus.configuration.security.filter.JwtVerifyFilter;
import io.github.thefrsh.stratus.troubleshooting.resolving.ServletExceptionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsJpaService userDetailsJpaService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final ServletExceptionResolver resolver;

    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;

    @Value("${spring.security.jwt.expiration-days}")
    private int tokenExpirationTimeInDays;

    @Autowired
    public SecurityConfiguration(UserDetailsJpaService userDetailsJpaService, PasswordEncoder passwordEncoder,
                                 ObjectMapper objectMapper, ServletExceptionResolver resolver) {

        this.userDetailsJpaService = userDetailsJpaService;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        this.resolver = resolver;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(jwtUsernamePasswordAuthenticationFilter())
                .addFilterAfter(jwtVerifyFilter(), JwtUsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .anyRequest()
                .authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {

        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) {

        web.ignoring().antMatchers("/register", "/h2-console/**", "/websocket/**");
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {

        var daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsJpaService);
        return daoAuthenticationProvider;
    }

    public UsernamePasswordAuthenticationFilter jwtUsernamePasswordAuthenticationFilter() throws Exception {

        var filter = new JwtUsernamePasswordAuthenticationFilter(objectMapper, resolver, jwtSecret,
                tokenExpirationTimeInDays);
        filter.setAuthenticationManager(authenticationManager());

        return filter;
    }

    public OncePerRequestFilter jwtVerifyFilter() {

        return new JwtVerifyFilter(userDetailsJpaService, resolver, jwtSecret);
    }
}
