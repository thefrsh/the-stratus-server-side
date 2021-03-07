package io.github.thefrsh.stratus.transfer.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginCredentialsRequest
{
    private String username;
    private String password;

    @Builder
    @JsonCreator
    @SuppressWarnings(value = "unused")
    public LoginCredentialsRequest(@JsonProperty(required = true) String username,
                                   @JsonProperty(required = true) String password)
    {
        this.username = username;
        this.password = password;
    }
}
