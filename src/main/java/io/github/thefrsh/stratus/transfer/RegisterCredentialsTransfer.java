package io.github.thefrsh.stratus.transfer;

import io.github.thefrsh.stratus.troubleshooting.validation.annotation.NoBlanks;
import io.github.thefrsh.stratus.troubleshooting.validation.annotation.Password;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.GroupSequence;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

@Getter
@Builder
@AllArgsConstructor
public class RegisterCredentialsTransfer
{
    @NotNull(message = "Username property is missing", groups = First.class)
    @NoBlanks(message = "Username must not contain any white character")
    @Size(min = 3, max = 20, message = "Username must be between {min} and {max} characters long")
    private final String username;

    @NotNull(message = "Password property is missing", groups = First.class)
    @NoBlanks(message = "Password must not contain any white character")
    @Size(min = 6, max = 20, message = "Password must be between {min} and {max} characters long")
    @Password(message = "Password must contain at least one upper letter, one lower and one digit")
    private final String password;

    @NotNull(message = "Email property is missing", groups = First.class)
    @Email(message = "${validatedValue} is not a valid email address")
    private final String email;

    public interface First {}

    @GroupSequence({First.class, Default.class})
    public interface ValidationOrder {}
}
