package ee.MinuTood.Quest.registration.system.userInterface.dtos;

import ee.MinuTood.Quest.registration.system.userInterface.customValidations.ValidEnumString;
import ee.MinuTood.Quest.registration.system.userInterface.enums.PaymentMethod;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class IndividualAttendeeRequestDto {

    @NotBlank(message = "Kohalviibija eesnimi peab olema täidetud")
    private String firstName;
    @NotBlank(message = "Kohalviibija perekonnanimi peab olema täidetud")
    private String lastName;
    @Pattern(regexp = "\\d{11}", message = "Isikukood peab olema 11 kohaline")
    private String personalCode;
    @ValidEnumString(message = "Mitte lubatud makseviis", enumClass = PaymentMethod.class)
    private PaymentMethod paymentMethod;
    @Column(length = 1500)
    @Size(max = 1500, message = "Extra informatsiooni väli ei tohi ületada {max} tähte")
    private String additionalInfo;


}
