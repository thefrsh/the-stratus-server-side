package io.github.thefrsh.stratus.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginCredentialsTransfer
{
    private String username;
    private String password;
}
