package io.github.thefrsh.stratus.service;

import io.github.thefrsh.stratus.transfer.request.RegisterCredentialsRequest;
import io.github.thefrsh.stratus.transfer.response.UserResponse;

public interface RegisterService {
    UserResponse register(RegisterCredentialsRequest credentials);
}
