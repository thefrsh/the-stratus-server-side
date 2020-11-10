package io.github.thefrsh.stratus.troubleshooting.exception;

public class UserAlreadyExistsException extends RuntimeException
{
    public UserAlreadyExistsException(String message)
    {
        super(message);
    }
}
