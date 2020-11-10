package io.github.thefrsh.stratus.service;

import io.github.thefrsh.stratus.transfer.RegisterCredentialsTransfer;
import io.github.thefrsh.stratus.transfer.UserTransfer;

public interface RegisterService
{
    UserTransfer register(RegisterCredentialsTransfer credentials);
}
