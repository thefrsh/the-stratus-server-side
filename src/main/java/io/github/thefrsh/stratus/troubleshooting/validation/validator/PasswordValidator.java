package io.github.thefrsh.stratus.troubleshooting.validation.validator;

import io.github.thefrsh.stratus.troubleshooting.validation.annotation.Password;
import io.vavr.collection.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    @Override
    public void initialize(Password constraintAnnotation) {
        // no initialization is required
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return containsUpper(s) && containsDigit(s) && containsLower(s);
    }

    private boolean containsUpper(String string) {
        return List.ofAll(string.toCharArray()).find(Character::isUpperCase).isDefined();
    }

    private boolean containsLower(String string) {
        return List.ofAll(string.toCharArray()).find(Character::isLowerCase).isDefined();
    }

    private boolean containsDigit(String string) {
        return List.ofAll(string.toCharArray()).find(Character::isDigit).isDefined();
    }
}
