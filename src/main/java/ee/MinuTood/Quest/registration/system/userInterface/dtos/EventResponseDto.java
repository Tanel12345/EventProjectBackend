package ee.MinuTood.Quest.registration.system.userInterface.dtos;

import ee.MinuTood.Quest.registration.system.application.dtos.IndividualAttendeeResponseDto;
import ee.MinuTood.Quest.registration.system.application.dtos.LegalAttendeeResponseDto;
import ee.MinuTood.Quest.registration.system.domain.event.valueobjects.LocationAddress;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class EventResponseDto {
    private Long id;
    private String name;
    private LocalDateTime time;
    private LocationAddress locationAdress;

   private Long attendeesCount;

    // Constructors, getters, and setters
}
