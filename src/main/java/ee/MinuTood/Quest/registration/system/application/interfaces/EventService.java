package ee.MinuTood.Quest.registration.system.application.interfaces;

import ee.MinuTood.Quest.registration.system.domain.event.Event;
import ee.MinuTood.Quest.registration.system.domain.event.entities.IndividualAttendee;
import ee.MinuTood.Quest.registration.system.domain.event.entities.LegalAttendee;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.EventRequestDto;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.EventResponseDto;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.IndividualAttendeeRequestDto;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.LegalAttendeeRequestDto;

import java.util.List;

public interface EventService {

    EventResponseDto createEventWithoutAttendees(EventRequestDto eventRequestDto);
    List<EventResponseDto> getAllEvents();
    IndividualAttendee addIndividualAttendeeToEventId(Long eventId, IndividualAttendeeRequestDto individualAttendeeRequestDto);
    LegalAttendee addLegalAttendeeToEventId(Long eventId, LegalAttendeeRequestDto legalAttendeeRequestDto);
    EventResponseDto getEventById(Long eventId);
    void deleteEvent(Long eventId);
}
