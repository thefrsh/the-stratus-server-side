package io.github.thefrsh.stratus.troubleshooting.validation.validator;

import io.github.thefrsh.stratus.troubleshooting.validation.annotation.NoBlanks;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NoBlanksValidator implements ConstraintValidator<NoBlanks, String> {

    @Override
    public void initialize(NoBlanks constraintAnnotation) {
        // no initialization in required
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        return s.matches("\\S+");
    }
}
