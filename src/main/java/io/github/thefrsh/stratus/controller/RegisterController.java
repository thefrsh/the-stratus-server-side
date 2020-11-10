package io.github.thefrsh.stratus.controller;

import io.github.thefrsh.stratus.service.RegisterService;
import io.github.thefrsh.stratus.transfer.RegisterCredentialsTransfer;
import io.github.thefrsh.stratus.transfer.UserTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegisterController
{
    private final RegisterService registerService;

    @Autowired
    public RegisterController(RegisterService registerService)
    {
        this.registerService = registerService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = {"", "/"}, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserTransfer register(@Validated(RegisterCredentialsTransfer.ValidationOrder.class)
                                 @RequestBody RegisterCredentialsTransfer credentials)
    {
        return registerService.register(credentials);
    }
}
