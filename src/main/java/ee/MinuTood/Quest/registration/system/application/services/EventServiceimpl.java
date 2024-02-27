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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Apligatsiooni Üritusteteenuse klass
 *
 * @author Tanel Sepp
 */

@Service
@AllArgsConstructor
public class EventServiceimpl implements EventService {

    private ModelMapper modelMapper;

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
    /////////////////////////////////


    @Transactional
    public List<EventResponseDto> getAllEvents() {


        // Perform business logic and save the event
        List<Event> savedEvents = eventRepository.findAll();
        List<EventResponseDto> eventResponseDtos = savedEvents.stream()
                .map(this::mapSavedEventsToEventResponceDto)
                .collect(Collectors.toList());

        return eventResponseDtos;


    }

    private EventResponseDto mapSavedEventsToEventResponceDto(Event event) {
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

////////////////////////////////////////////////////////

    @Transactional
    public EventResponseDto getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Üritust ei leitud ID: " + eventId));
        EventResponseDto eventResponseDto = mapSavedEventsToEventResponceDto(event);
        return eventResponseDto;
    }

    @Transactional
    public Event getEventByIdWithAttendees(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Üritust ei leitud ID: " + eventId));

        return event;
    }


    @Transactional
    public IndividualAttendee addIndividualAttendeeToEventId(Long eventId, IndividualAttendeeRequestDto individualAttendeeRequestDto) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Üritust ei leitud ID: " + eventId));

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


    @Transactional
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }


    @Transactional
    public void deleteIndividualAttendeeByIdFromEventId(Long attendeeId, Long eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        Event event = eventOptional.orElseThrow(() -> new EntityNotFoundException("Üritust ei leitud"));

        boolean isRemoved = event.deleteIndividualAttendeeById(attendeeId);

        if (isRemoved) {
            eventRepository.save(event);
        } else {
            throw new EntityNotFoundException("Sellist osalejat ei leitud");
        }
    }


    @Transactional
    public void deleteLegalAttendeeByIdFromEventId(Long attendeeId, Long eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Event event = eventOptional.orElseThrow(() -> new EntityNotFoundException("Üritust ei leitud"));

        boolean isRemoved = event.deleteLegalAttendeeById(attendeeId);

        if (isRemoved) {
            eventRepository.save(event);
        } else {
            throw new EntityNotFoundException("Sellist osalejat ei leitud");
        }
    }

    //////////////////////////////////////////////////////////////

    @Transactional
    public void updateIndividualAttendeeByIdFromEventId(Long attendeeId, Long eventId, IndividualAttendeeRequestDto individualAttendeeRequestDto) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Event event = eventOptional.orElseThrow(() -> new EntityNotFoundException("Üritust ei leitud"));

        // Kutsub updateIndividualAttendee meetodit Event klassis. Andes kaasa uue uuendatud andmetega IndividualAttendee
        boolean attendeeUpdated = event.updateIndividualAttendee(buildUpdatedAttendee(attendeeId, individualAttendeeRequestDto));

        if (attendeeUpdated) {
            // Kui tagastatakse true siis üritus taassalvestatakse
            eventRepository.save(event);


        } else {
            // Kui osalejat ei litud sellelt ürituselt
            logger.error("eraisikust osalejat ei leitud sellel üritusel");
            throw new EntityNotFoundException("eraisikust osalejat ei leitud sellel üritusel");
        }
    }

    /**
     * Abimeetod eelmisele, osaleja uuendamiseks.
     */
    private IndividualAttendee buildUpdatedAttendee(Long attendeeId, IndividualAttendeeRequestDto individualAttendeeRequestDto) {
        IndividualAttendee updatedAttendee = new IndividualAttendee();
        updatedAttendee.setId(attendeeId);
        updatedAttendee.setFirstName(individualAttendeeRequestDto.getFirstName());
        updatedAttendee.setLastName(individualAttendeeRequestDto.getLastName());
        updatedAttendee.setPersonalCode(individualAttendeeRequestDto.getPersonalCode());
        updatedAttendee.setPaymentMethod(individualAttendeeRequestDto.getPaymentMethod());
        updatedAttendee.setAdditionalInfo(individualAttendeeRequestDto.getAdditionalInfo());
        // Set other fields as needed

        return updatedAttendee;
    }
    ////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////

    @Transactional
    public void updateLegalAttendeeByIdFromEventId(Long attendeeId, Long eventId, LegalAttendeeRequestDto legalAttendeeRequestDto) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Event event = eventOptional.orElseThrow(() -> new EntityNotFoundException("Üritust ei leitud"));

        // Kutsub updateIndividualAttendee meetodit Event klassis. Andes kaasa uue uuendatud andmetega IndividualAttendee
        boolean attendeeUpdated = event.updateLegalAttendee(buildUpdatedAttendee(attendeeId, legalAttendeeRequestDto));

        if (attendeeUpdated) {
            // Kui tagastatakse true siis üritus taassalvestatakse
            eventRepository.save(event);


        } else {
            // Kui osalejat ei litud sellelt ürituselt
            logger.error("Ettevõttest osalejat ei leitud sellel üritusel");
            throw new EntityNotFoundException("Ettevõttest osalejat ei leitud sellel üritusel");
        }
    }

    /**
     * Abimeetod eelmisele, osaleja uuendamiseks.
     */
    private LegalAttendee buildUpdatedAttendee(Long attendeeId, LegalAttendeeRequestDto legalAttendeeRequestDto) {
        LegalAttendee updatedAttendee = new LegalAttendee();
        updatedAttendee.setId(attendeeId);
        updatedAttendee.setCompanyName(legalAttendeeRequestDto.getCompanyName());
        updatedAttendee.setRegistrationCode(legalAttendeeRequestDto.getRegistrationCode());
        updatedAttendee.setNumberOfParticipant(legalAttendeeRequestDto.getNumberOfParticipant());
        updatedAttendee.setPaymentMethod(legalAttendeeRequestDto.getPaymentMethod());
        updatedAttendee.setAdditionalInfo(legalAttendeeRequestDto.getAdditionalInfo());
        // Set other fields as needed

        return updatedAttendee;
    }
////////////////////////////////////////////////////////////////////////

}