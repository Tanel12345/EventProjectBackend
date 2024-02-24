package ee.MinuTood.Quest.registration.system.userInterface.controllers;

import ee.MinuTood.Quest.registration.system.application.interfaces.EventService;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.EventRequestDto;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.EventResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller klass Event agregaadiga seotud tegevuste teostamiseks
 *
 * @author Tanel Sepp
 */
@RestController
@RequestMapping("/api/events")

public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/createEventWithoutAttendees")
    public ResponseEntity<?> createEventWithoutAttendees(@RequestBody @Valid EventRequestDto eventRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Log validation errors for debugging purposes
            bindingResult.getFieldErrors().forEach(error ->
                    System.out.println("Validation error in field " + error.getField() + ": " + error.getDefaultMessage()));

            // Return a 400 Bad Request response to the client for validation errors
            return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        try {
            // Pass DTO to the service, let the service handle the conversion and further validation
            EventResponseDto createdEvent = eventService.createEventWithoutAttendees(eventRequestDto);

            // Return a 201 Created response with the created event
            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createdEvent);
        } catch (ValidationException e) {
            // Log validation errors for debugging purposes
            System.out.println("Validation error in service: " + e.getMessage());


            // Return a 400 Bad Request response to the client for validation errors from the service
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            // Log other exceptions for debugging purposes
            System.out.println("An error occurred: " + e.getMessage());

            // Return a 500 Internal Server Error response for other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getAllEvents")
    public ResponseEntity<?> getAllEvents() {

        try {
            // Pass DTO to the service, let the service handle the conversion and further validation
            List<EventResponseDto> events = eventService.getAllEvents();

            // Return a 201 Created response for a successful creation
            return new ResponseEntity<>(events, HttpStatus.OK);

        } catch (Exception e) {
            // Log other exceptions for debugging purposes
            System.out.println("An error occurred: " + e.getMessage());

            // Return a 500 Internal Server Error response for other exceptions
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//    @GetMapping("/{eventId}")
//    public ResponseEntity<EventResponseDto> getEventDetails(@PathVariable Long eventId) {
//        // Call service to get event details
//        Event event = eventService.getEventById(eventId);
//
//        // Convert domain entity to DTO and return
//        EventResponseDto eventResponseDto = mapEntityToResponseDto(event);
//        return ResponseEntity.ok(eventResponseDto);
//    }
//
//    // Other methods for handling different endpoints
//
//    private Event mapDtoToEntity(EventRequestDto eventRequestDto) {
//        // Logic to map EventRequestDto to Event entity
//    }
//
//    private EventResponseDto mapEntityToResponseDto(Event event) {
//        // Logic to map Event entity to EventResponseDto
//    }
}