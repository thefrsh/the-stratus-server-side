package io.github.thefrsh.stratus.configuration.test.bootstrap;

import io.github.thefrsh.stratus.model.User;
import io.github.thefrsh.stratus.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("test")
public class TestDataSeed implements CommandLineRunner
{
    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TestDataSeed(UserJpaRepository userJpaRepository, PasswordEncoder passwordEncoder)
    {
        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args)
    {
        var user = User.builder()
                .username("test")
                .password(passwordEncoder.encode("test"))
                .email("test@test.com")
                .build();

        userJpaRepository.save(user);
    }
}
