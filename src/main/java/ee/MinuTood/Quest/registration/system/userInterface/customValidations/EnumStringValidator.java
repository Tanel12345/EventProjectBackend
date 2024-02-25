package ee.MinuTood.Quest.registration.system.userInterface.customValidations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ValidEnumStringValidator Class:
 *
 * Implements ConstraintValidator<ValidEnumString, Enum<?>>: This class implements the ConstraintValidator interface, where ValidEnumString is the annotation type, and Enum<?> is the type of the field that is being validated.
 * private Set<String> enumValues;: This set will store the string representations of the enum constants.
 * initialize(ValidEnumString annotation): This method is called during the initialization of the validator. It retrieves the enum class from the annotation and populates the enumValues set with the names of the enum constants.
 * isValid(Enum<?> value, ConstraintValidatorContext context): This method is called to perform the actual validation. It checks whether the provided enum value is in the set of valid enum names.
 * Usage in DTO class (IndividualAttendeeRequestDto):
 * The ValidEnumString annotation is applied to the paymentMethod field in the IndividualAttendeeRequestDto class. The enumClass attribute specifies the enum class to be used for validation (PaymentMethod in this case).
 */

public class EnumStringValidator implements ConstraintValidator<ValidEnumString, Enum<?>> {

    private Set<String> enumValues;

    @Override
    public void initialize(ValidEnumString annotation) {
        Class<? extends Enum<?>> enumClass = annotation.enumClass();
        enumValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        return value == null || enumValues.contains(value.name());
    }
}