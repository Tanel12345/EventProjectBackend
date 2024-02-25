package ee.MinuTood.Quest.registration.system.userInterface.dtos;

import ee.MinuTood.Quest.registration.system.userInterface.customValidations.ValidEnumString;
import ee.MinuTood.Quest.registration.system.userInterface.enums.PaymentMethod;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LegalAttendeeRequestDto {

    @NotBlank(message = "Ettevõtte nimi peab olema täidetud")
    private String companyName;
    @NotNull(message = "Ettevõtte registrinumber peab olema lisatud")
    private Long registrationCode;
    @NotNull(message = "Osavõtjate arv peab olema täidetud")
    @Min(value = 1, message = "Osavõtjate arv peab olema vähemalt {value}")
    @Max(value = 25, message = "Osavõtjate arv ei tohi ületada {value}")
    private Long numberOfParticipant;
    @ValidEnumString(message = "Mitte lubatud makseviis", enumClass = PaymentMethod.class)
    private PaymentMethod paymentMethod;
    @Column(length = 5000)
    @Size(max = 5000, message = "Extra informatsiooni väli ei tohi ületada {max} tähte")
    private String additionalInfo;

}