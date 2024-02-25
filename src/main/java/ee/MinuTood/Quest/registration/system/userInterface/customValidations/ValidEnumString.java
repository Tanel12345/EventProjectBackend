package ee.MinuTood.Quest.registration.system.userInterface.customValidations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ValidEnumString Annotation (eraisikust osavõtja dto klassi makseviisi fieldi valideerimiseks):
 *
 * @Target({ElementType.FIELD}): Indicates that this annotation can only be used on fields.
 * @Retention(RetentionPolicy.RUNTIME): Specifies that the annotation should be retained at runtime.
 * @Constraint(validatedBy = ValidEnumStringValidator.class): Specifies the validator class that should be used to validate fields annotated with @ValidEnumString.
 * String message() default "Invalid enum value";: Provides a default error message for validation failure.
 * Class<?>[] groups() default {};: Defines groups to which this constraint belongs. (Not used in this example)
 * Class<? extends Payload>[] payload() default {};: Defines payload classes that can be used to carry additional information. (Not used in this example)
 * Class<? extends Enum<?>> enumClass();: Specifies the enum class that should be used for validation.
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumStringValidator.class)
public @interface ValidEnumString {
    String message() default "Invalid enum value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum<?>> enumClass();
}
