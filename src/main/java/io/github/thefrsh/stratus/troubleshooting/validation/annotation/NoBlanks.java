package io.github.thefrsh.stratus.troubleshooting.validation.annotation;

import io.github.thefrsh.stratus.troubleshooting.validation.validator.NoBlanksValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoBlanksValidator.class)
public @interface NoBlanks {

    String message() default "There are white characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
