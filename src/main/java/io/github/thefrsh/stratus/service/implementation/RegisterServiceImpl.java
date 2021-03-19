package io.github.thefrsh.stratus.service.implementation;

import io.github.thefrsh.stratus.repository.UserJpaRepository;
import io.github.thefrsh.stratus.service.RegisterService;
import io.github.thefrsh.stratus.service.TransferConversionService;
import io.github.thefrsh.stratus.transfer.request.RegisterCredentialsRequest;
import io.github.thefrsh.stratus.transfer.response.UserResponse;
import io.github.thefrsh.stratus.troubleshooting.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class RegisterServiceImpl implements RegisterService {
    private final UserJpaRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TransferConversionService conversionService;

    @Autowired
    public RegisterServiceImpl(UserJpaRepository repository, PasswordEncoder passwordEncoder,
                               TransferConversionService conversionService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.conversionService = conversionService;
    }

    @Override
    @Transactional
    public UserResponse register(RegisterCredentialsRequest credentials) {
        repository.findByUsernameOrEmail(credentials.getUsername(), credentials.getEmail()).peek(u -> {

            var message = "";

            if (u.getUsername().equals(credentials.getUsername())) {
                message = "Username " + credentials.getUsername() + " is not available";
            }
            else if (u.getEmail().equals(credentials.getEmail())) {
                message = "Email address " + credentials.getEmail() + " is not available";
            }

            throw new UserAlreadyExistsException(message);
        });

        var user = conversionService.toUser(credentials);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        var savedUser = repository.save(user);

        return conversionService.toUserResponse(savedUser);
    }
}
