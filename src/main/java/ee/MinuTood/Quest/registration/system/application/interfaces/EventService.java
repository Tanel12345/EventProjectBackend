package ee.MinuTood.Quest.registration.system.application.interfaces;

import ee.MinuTood.Quest.registration.system.domain.event.Event;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.EventRequestDto;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.EventResponseDto;

import java.util.List;

public interface EventService {

    EventResponseDto createEventWithoutAttendees(EventRequestDto eventRequestDto);
    List<EventResponseDto> getAllEvents();
}
