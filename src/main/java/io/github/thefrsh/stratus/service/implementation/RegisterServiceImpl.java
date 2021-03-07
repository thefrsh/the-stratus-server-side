package io.github.thefrsh.stratus.service.implementation;

import io.github.thefrsh.stratus.model.User;
import io.github.thefrsh.stratus.repository.UserJpaRepository;
import io.github.thefrsh.stratus.service.RegisterService;
import io.github.thefrsh.stratus.transfer.request.RegisterCredentialsRequest;
import io.github.thefrsh.stratus.transfer.response.UserResponse;
import io.github.thefrsh.stratus.troubleshooting.exception.UserAlreadyExistsException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class RegisterServiceImpl implements RegisterService
{
    private final UserJpaRepository repository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegisterServiceImpl(UserJpaRepository repository, ModelMapper modelMapper, PasswordEncoder passwordEncoder)
    {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse register(RegisterCredentialsRequest credentials)
    {
        repository.findByUsernameOrEmail(credentials.getUsername(), credentials.getEmail()).ifPresent(u ->
        {
            var message = "";

            if (u.getUsername().equals(credentials.getUsername()))
            {
                message = "Username " + credentials.getUsername() + " is not available";
            }
            else if (u.getEmail().equals(credentials.getEmail()))
            {
                message = "Email address " + credentials.getEmail() + " is not available";
            }

            throw new UserAlreadyExistsException(message);
        });

        var user = modelMapper.map(credentials, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        var savedUser = repository.save(user);

        return modelMapper.map(savedUser, UserResponse.class);
    }
}
