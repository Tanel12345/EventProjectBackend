package ee.MinuTood.Quest.registration.system.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.MinuTood.Quest.registration.system.application.interfaces.EventService;
import ee.MinuTood.Quest.registration.system.domain.event.Event;
import ee.MinuTood.Quest.registration.system.domain.event.repositories.EventRepository;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.EventRequestDto;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.EventResponseDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Apligatsiooni Üritusteteenuse klass
 *
 * @author Tanel Sepp
 */

@Service
@AllArgsConstructor
public class EventServiceimpl implements EventService {

    private  ModelMapper modelMapper;

    private EventRepository eventRepository;



    @Transactional
    public EventResponseDto createEventWithoutAttendees(EventRequestDto eventRequestDto) {
        // Convert DTO to domain entity
        Event event = mapDtoToEntity(eventRequestDto);

        // Perform business logic and save the event
        Event savedEvent = eventRepository.save(event);

        // Map the savedEvent to EventResponseDto using ModelMapper
        EventResponseDto eventResponseDto = modelMapper.map(savedEvent, EventResponseDto.class);

        // Additional logic if needed

        return eventResponseDto;
    }

    private Event mapDtoToEntity(EventRequestDto eventRequestDto) {
        Event event = new Event();
        event.setName(eventRequestDto.getName());
        event.setTime(eventRequestDto.getTime());
        event.setLocationAddress(eventRequestDto.getLocationAddress());
        event.setAdditionalInfo(eventRequestDto.getAdditionalInfo());
        // You might need to set other properties based on your DTO and entity structure

        return event;
    }


///////////////////////////////////////////////////////////////////////////////////////////////


    @Transactional
    public List<EventResponseDto> getAllEvents() {



        // Perform business logic and save the event
        List<Event> savedEvents = eventRepository.findAll();
        List<EventResponseDto> eventResponseDtos = savedEvents.stream()
                .map(this::mapSavedEventsToEventResponceDto)
                .collect(Collectors.toList());

        return eventResponseDtos;




    }
    private EventResponseDto mapSavedEventsToEventResponceDto(Event event){
      EventResponseDto eventResponseDto = new EventResponseDto();
      eventResponseDto.setId(event.getId());
        eventResponseDto.setName(event.getName());
        eventResponseDto.setTime(event.getTime());
        eventResponseDto.setLocationAdress(event.getLocationAddress());
        Long individualCount = event.getIndividualAttendees().stream().count();
        Long legalCount = event.getLegalAttendees().stream().count();
        eventResponseDto.setAttendeesCount(individualCount + legalCount);

        return eventResponseDto;
    }

//    @Transactional(readOnly = true)
//    public EventResponseDto getEventById(Long eventId) {
//        Event event = eventRepository.findById(eventId).orElse(null);
//        return mapEntityToDto(event);
//    }
//
//    @Transactional
//    public EventResponseDto updateEvent(Long eventId, EventRequestDto updatedEventDto) {
//        Event existingEvent = eventRepository.findById(eventId).orElse(null);
//        if (existingEvent != null) {
//            Event updatedEvent = mapDtoToEntity(updatedEventDto);
//            updatedEvent.setId(eventId);
//            eventRepository.save(updatedEvent);
//            return mapEntityToDto(updatedEvent);
//        }
//        return null; // Handle appropriately if the event is not found
//    }
//
//    @Transactional
//    public void deleteEvent(Long eventId) {
//        eventRepository.deleteById(eventId);
//    }
//
//    @Transactional
//    public void addIndividualAttendeeToEvent(Long eventId, IndividualAttendeeRequestDto individualAttendeeDto) {
//        Event event = getEventById(eventId);
//        if (event != null) {
//            IndividualAttendee individualAttendee = mapDtoToEntity(individualAttendeeDto);
//            event.addIndividualAttendee(individualAttendee);
//            updateEvent(event);
//        }
//    }
//
//    @Transactional
//    public void addLegalAttendeeToEvent(Long eventId, LegalAttendeeRequestDto legalAttendeeDto) {
//        Event event = getEventById(eventId);
//        if (event != null) {
//            LegalAttendee legalAttendee = mapDtoToEntity(legalAttendeeDto);
//            event.addLegalAttendee(legalAttendee);
//            updateEvent(event);
//        }
//    }
//
//
//
//    private EventResponseDto mapEntityToDto(Event event) {
//        // Implement the mapping logic from Entity to DTO
//    }
//
//    private IndividualAttendee mapDtoToEntity(IndividualAttendeeRequestDto individualAttendeeDto) {
//        // Implement the mapping logic from DTO to Entity for IndividualAttendee
//    }
//
//    private LegalAttendee mapDtoToEntity(LegalAttendeeRequestDto legalAttendeeDto) {
//        // Implement the mapping logic from DTO to Entity for LegalAttendee
//    }

//
}