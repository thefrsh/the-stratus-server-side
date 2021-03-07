package io.github.thefrsh.stratus.transfer.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserIdRequest
{
    @NotNull(message = "User id property is missing")
    private Long id;
}
