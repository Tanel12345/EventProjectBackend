package ee.MinuTood.Quest.registration.system.application.services;

import ee.MinuTood.Quest.registration.system.application.interfaces.EventService;
import ee.MinuTood.Quest.registration.system.domain.event.Event;
import ee.MinuTood.Quest.registration.system.domain.event.entities.IndividualAttendee;
import ee.MinuTood.Quest.registration.system.domain.event.entities.LegalAttendee;
import ee.MinuTood.Quest.registration.system.domain.event.repositories.EventRepository;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.EventRequestDto;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.EventResponseDto;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.IndividualAttendeeRequestDto;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.LegalAttendeeRequestDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(EventServiceimpl.class);


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

    @Transactional
    public EventResponseDto getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->new EntityNotFoundException("Üritust ei leitud ID: " + eventId));
        EventResponseDto eventResponseDto = mapSavedEventsToEventResponceDto(event);
        return eventResponseDto;
    }

    @Transactional
    public IndividualAttendee addIndividualAttendeeToEventId(Long eventId, IndividualAttendeeRequestDto individualAttendeeRequestDto) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with ID: " + eventId));

        try {
            IndividualAttendee individualAttendee = modelMapper.map(individualAttendeeRequestDto, IndividualAttendee.class);
            // Set the associated Event
            individualAttendee.setEvent(existingEvent);
            existingEvent.addIndividualAttendee(individualAttendee);
            Event savedEvent = eventRepository.save(existingEvent);


            return individualAttendee;

        } catch (Exception e) {
            // Log other exceptions for debugging purposes
            logger.error("An error occurred in the service: ", e);
            throw new ValidationException("Internal Server Error");
        }
    }
    @Transactional
    public LegalAttendee addLegalAttendeeToEventId(Long eventId, LegalAttendeeRequestDto legalAttendeeRequestDto) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with ID: " + eventId));

        try {
            LegalAttendee legalAttendee = modelMapper.map(legalAttendeeRequestDto, LegalAttendee.class);
            // Set the associated Event
            legalAttendee.setEvent(existingEvent);
            existingEvent.addLegalAttendee(legalAttendee);
            Event savedEvent = eventRepository.save(existingEvent);


            return legalAttendee;

        } catch (Exception e) {
            // Log other exceptions for debugging purposes
            logger.error("An error occurred in the service: ", e);
            throw new ValidationException("Internal Server Error");
        }
    }


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