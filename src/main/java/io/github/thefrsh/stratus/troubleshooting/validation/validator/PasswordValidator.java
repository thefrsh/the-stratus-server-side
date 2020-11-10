package io.github.thefrsh.stratus.troubleshooting.validation.validator;

import io.github.thefrsh.stratus.troubleshooting.validation.annotation.Password;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String>
{
    @Override
    public void initialize(Password constraintAnnotation)
    {
        // no initialization is required
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext)
    {
        return containsUpper(s) && containsDigit(s) && containsLower(s);
    }

    private boolean containsUpper(String string)
    {
        return string.chars().anyMatch(Character::isUpperCase);
    }

    private boolean containsLower(String string)
    {
        return string.chars().anyMatch(Character::isLowerCase);
    }

    private boolean containsDigit(String string)
    {
        return string.chars().anyMatch(Character::isDigit);
    }
}
