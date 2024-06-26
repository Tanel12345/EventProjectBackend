package ee.MinuTood.Quest.registration.system.userInterface.controllers;

import ee.MinuTood.Quest.registration.system.application.interfaces.EventService;
import ee.MinuTood.Quest.registration.system.domain.event.Event;
import ee.MinuTood.Quest.registration.system.domain.event.entities.IndividualAttendee;
import ee.MinuTood.Quest.registration.system.domain.event.entities.LegalAttendee;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller klass Event agregaadiga seotud tegevuste teostamiseks.
 * Meetodid siin on iseennast kirjeldavad.
 *
 * @author Tanel Sepp
 */
@RestController
@RequestMapping("/api/events")

public class EventController {

    private final EventService eventService;
    //SLF4J Logger tuleks luua igas klassis eraldi. Võimaldab logida sõnumeid konsooli
    private static Logger logger = LoggerFactory.getLogger(EventController.class);

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }


    @PostMapping("/createEventWithoutAttendees")
    public ResponseEntity<?> createEventWithoutAttendees(@RequestBody @Valid EventRequestDto eventRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Logib valideerimise errorid ja tagastab vastava response entity
            bindingResult.getFieldErrors().forEach(error ->
                    logger.error("Validation error in field: {}" + error.getField() + ": " + error.getDefaultMessage()));

            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
        }
        try {
            // Lisab DTO servicele, service teostab ülejäänud toimingud.
            EventResponseDto createdEvent = eventService.createEventWithoutAttendees(eventRequestDto);
            ApiResponse apiResponse = new ApiResponse("Uus üritus on loodud");
            apiResponse.setEventId(createdEvent.getId());
            // Tagastab 201 Created vastused koos loodud üritusega
            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(apiResponse);
        } catch (ValidationException e) {
            // Logib valideerimise errorid servise levelil

            logger.error("Validation error in service:", e.getMessage(), e);

            // Tagastab 400 Bad Request response Kui servise levelil on exception visatud
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            // Logib teised võimalikud exceptionid
            logger.error("An error occurred: ", e.getMessage(), e);
            //Tagastab 500 Internal Server Error vastuse
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/")
    public String tervitus() {
        return "<h1>Tere</h1>";
    }

        @GetMapping("/getAllEvents")
    public ResponseEntity<?> getAllEvents() {

        try {
            // Get päring suunatakse teenuse klassi vastavale meetodile
            List<EventResponseDto> events = eventService.getAllEvents();

            //Tagastab 201 Created response kui päring õnnestus
            return new ResponseEntity<>(events, HttpStatus.OK);

        } catch (Exception e) {
            // Logib exceptioni consooli kui viimane on visatud.
            logger.error("An error occurred: ", e.getMessage(), e);
            // Tagastab 500 Internal Server Error response exceptioni ilmumisel
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addIndividualAttendeeToEventId/{eventId}")
    public ResponseEntity<?> addIndividualAttendeeToEventId(@PathVariable Long eventId, @Valid @RequestBody IndividualAttendeeRequestDto individualAttendeeRequestDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // Logib dto klassi valideerimise errorid ja tagastab vastava response entity
            bindingResult.getAllErrors().forEach(error ->
                    logger.error("Validation error in field: {} - {}", error.getObjectName(), error.getDefaultMessage()));

            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            // Lisab DTO servicele, service teostab ülejäänud toimingud.
            IndividualAttendee individualAttendee = eventService.addIndividualAttendeeToEventId(eventId, individualAttendeeRequestDto);

            // Tagastab 201 Created response eduka toimingu puhul
            return new ResponseEntity<>(individualAttendee, HttpStatus.CREATED);

            //Püüab ja händlib exceptioni kui eventi ei leita
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ValidationException e) {
            // Logib valideerimise errorid servise levelil ja tagastab
            logger.error("Validation error in service: ", e.getMessage(), e);

            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            // Logib teised võimalikud exceptionid ja tagastab kood 500
            logger.error("An error occurred: ", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/addLegalAttendeeToEventId/{eventId}")
    public ResponseEntity<?> addLegalAttendeeToEventId(@PathVariable Long eventId, @Valid @RequestBody LegalAttendeeRequestDto legalAttendeeRequestDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // Logib dto klassi valideerimise errorid ja tagastab vastava response entity
            bindingResult.getAllErrors().forEach(error ->
                    logger.error("Validation error in field: {} - {}", error.getObjectName(), error.getDefaultMessage()));

            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            // Lisab DTO servicele, service teostab ülejäänud toimingud.
            LegalAttendee legalAttendee = eventService.addLegalAttendeeToEventId(eventId, legalAttendeeRequestDto);

            // Tagastab 201 Created response eduka toimingu puhul
            return new ResponseEntity<>(legalAttendee, HttpStatus.CREATED);

            //Püüab ja händlib exceptioni kui eventi ei leita
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ValidationException e) {
            // Logib valideerimise errorid servise levelil ja tagastab
            logger.error("Validation error in service: ", e.getMessage(), e);

            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            // Logib teised võimalikud exceptionid ja tagastab kood 500
            logger.error("An error occurred: ", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/getEventDetails/{eventId}")
    public ResponseEntity<?> getEventDetailsById(@PathVariable Long eventId) {

        try {
            EventResponseDto eventResponseDto = eventService.getEventById(eventId);
            return ResponseEntity.ok(eventResponseDto);
        } catch (EntityNotFoundException e) {
            logger.error("Üritust ei leitud ID: " + eventId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }


    }

    @GetMapping("/getEventDetailsWithAttendeesById/{eventId}")
    public ResponseEntity<?> getEventDetailsWithAttendeesById(@PathVariable Long eventId) {

        try {
            Event eventResponse = eventService.getEventByIdWithAttendees(eventId);
            return ResponseEntity.ok(eventResponse);
        } catch (EntityNotFoundException e) {
            logger.error("Üritust ei leitud ID: " + eventId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }


    }

    @DeleteMapping("/deleteEventById/{eventId}")
    public ResponseEntity<ApiResponse> deleteEventById(@PathVariable Long eventId) {
        try {
            eventService.deleteEvent(eventId);
            return ResponseEntity.ok(new ApiResponse("Üritus kustutatud edukalt"));
        } catch (EntityNotFoundException e) {
            logger.error("Event not found with ID: " + eventId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("An error occurred while deleting event with ID: " + eventId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Internal Server Error"));
        }
    }

    @DeleteMapping("/{eventId}/individualAttendees/{attendeeId}")
    public ResponseEntity<ApiResponse> deleteIndividualAttendeeFromEvent(
            @PathVariable Long eventId,
            @PathVariable Long attendeeId) {
        try {
            eventService.deleteIndividualAttendeeByIdFromEventId(attendeeId, eventId);
            return ResponseEntity.ok(new ApiResponse("Eraisikust Osavõtja kustutati edukalt"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{eventId}/legalAttendees/{attendeeId}")
    public ResponseEntity<ApiResponse> deleteLegalAttendeeFromEvent(
            @PathVariable Long eventId,
            @PathVariable Long attendeeId) {
        try {
            eventService.deleteLegalAttendeeByIdFromEventId(attendeeId, eventId);
            return ResponseEntity.ok(new ApiResponse("Ettevõttest Osavõtja kustutati edukalt"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
        }

    }

    @PutMapping("/{eventId}/individualAttendees/{attendeeId}")
    public ResponseEntity<?> updateIndividualAttendeeFromEvent(
            @PathVariable Long eventId,
            @PathVariable Long attendeeId, @Valid @RequestBody IndividualAttendeeRequestDto individualAttendeeRequestDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // Logib valideerimise errorid ja tagastab vastava response entity
            bindingResult.getFieldErrors().forEach(error ->
                    logger.error("Validation error in field: {}" + error.getField() + ": " + error.getDefaultMessage()));

            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
        }
        try {
            eventService.updateIndividualAttendeeByIdFromEventId(attendeeId, eventId, individualAttendeeRequestDto);
            return ResponseEntity.ok(new ApiResponse("Eraisikust Osavõtja uuendati edukalt"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @PutMapping("/{eventId}/legalAttendees/{attendeeId}")
    public ResponseEntity<?> updateLegalAttendeeFromEvent(
            @PathVariable Long eventId,
            @PathVariable Long attendeeId, @Valid @RequestBody LegalAttendeeRequestDto legalAttendeeRequestDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // Logib valideerimise errorid ja tagastab vastava response entity
            bindingResult.getFieldErrors().forEach(error ->
                    logger.error("Validation error in field: {}" + error.getField() + ": " + error.getDefaultMessage()));

            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
        }
        try {
            eventService.updateLegalAttendeeByIdFromEventId(attendeeId, eventId, legalAttendeeRequestDto);
            return ResponseEntity.ok(new ApiResponse("Ettevõttest Osavõtja uuendati edukalt"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}