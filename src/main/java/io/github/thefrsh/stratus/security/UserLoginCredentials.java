package io.github.thefrsh.stratus.security;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginCredentials
{
    private String username;
    private String password;
}
