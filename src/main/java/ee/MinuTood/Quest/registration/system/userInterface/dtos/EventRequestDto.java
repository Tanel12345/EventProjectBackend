package ee.MinuTood.Quest.registration.system.userInterface.dtos;

import ee.MinuTood.Quest.registration.system.domain.event.valueobjects.LocationAddress;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class EventRequestDto {

    @NotBlank(message = "Event name cannot be blank")
    private String name;


    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @FutureOrPresent(message = "Event time must be in the present or future")
    private LocalDateTime time;
@Embedded
    @Valid
    private LocationAddress locationAddress;
    @Size(max = 1000, message = "Additional info cannot exceed 1000 characters")
    private String additionalInfo;

    // Constructors, getters, and setters
}
