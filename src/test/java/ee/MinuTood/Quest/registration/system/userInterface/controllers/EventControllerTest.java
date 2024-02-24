package ee.MinuTood.Quest.registration.system.userInterface.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ee.MinuTood.Quest.registration.system.domain.event.Event;
import ee.MinuTood.Quest.registration.system.domain.event.repositories.EventRepository;
import ee.MinuTood.Quest.registration.system.domain.event.valueobjects.LocationAddress;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.EventRequestDto;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.EventResponseDto;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testi klass Event agregaadiga seotud tegevuste testimiseks
 *
 * @author Tanel Sepp
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//Loads an EmbeddedWebApplicationContext and provides a real servlet environment. Embedded servlet containers are started and listening on a random port.
@AutoConfigureMockMvc
public class EventControllerTest {
    @LocalServerPort // Lisab servleti randomli genereeritud kohaliku pordi muutujasse
    private int port;
    private String requestBody;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    private TestRestTemplate restTemplate;


    @BeforeEach
    void setUp() throws Exception {

        objectMapper.registerModule(new JavaTimeModule());//registering a module for handling Java 8 Date and Time types with the Jackson ObjectMapper

    }

    @AfterEach
    void tearDown() {
    }

    /**
     * See meetod on Evendi loomise controlleri testimiseks.
     * Kasutame RestAsured Java kogumikku päringu tegemiseks.
     *
     * @throws JsonProcessingException
     */

    @Test
    public void testCreateEventWithoutAttendees() throws JsonProcessingException {
        // Loo request bodyle json string
        EventRequestDto eventRequestDto = new EventRequestDto();
        eventRequestDto.setName("TestEvent1");
        eventRequestDto.setTime(LocalDateTime.parse("2025-12-01T12:00:00"));

        LocationAddress locationAddress = new LocationAddress();
        locationAddress.setStreet("SampleStreet");

        locationAddress.setCity("SampleCity");
        locationAddress.setState("SampleState");
        locationAddress.setZipCode("345345345");

        eventRequestDto.setLocationAddress(locationAddress);
        eventRequestDto.setAdditionalInfo("SampleInfo");

        // Convert the object to JSON
        requestBody = objectMapper.writeValueAsString(eventRequestDto);

        //Testi osa
        Response response = given()
                .port(port)  // Use the injected port
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when()
                .post("/api/events/createEventWithoutAttendees")
                .andReturn();
        //Võimaldab kohest checki lisades veel .then().statusCode(201)


        // Kontrollib kas status kood on vastav etteantule
        assertThat(response.getStatusCode()).isEqualTo(201);

        // Deserialize the response into EventResponseDto
        EventResponseDto createdEventResponse = response.as(EventResponseDto.class);

        // Retrieve the added event from the database using your repository or data access layer
        Optional<Event> addedEventOptional = eventRepository.findById(createdEventResponse.getId());
        //Kontrollib kas optional on olemas
        assertThat(addedEventOptional).isPresent();
        //Extractib Wrapperist tema sisu
        Event addedEvent = addedEventOptional.get();
        //Kontrollib ega evendi id ei ole null
        assertThat(addedEvent.getId()).isNotNull();
        //Kontrollib kas nimi vastab eeldatavale
        assertThat(addedEvent.getName()).isEqualTo(createdEventResponse.getName());
        //Kontrollib kas lisatud event on sama mis testitud controllerilt saadud event.
        assertThat(addedEvent.getId()).isEqualTo(createdEventResponse.getId());


    }


    /**
     * Test kontrollib andmebaasis olevate ürituste ja api päringu kaudu saadud ürituste arvu.
     * Test kasutab RestTemplate, Springi pakutavat http päringuid teostavat raamistikku
     *
     * @throws JsonProcessingException
     */

    @Test
    public void testGetAllEvents() throws JsonProcessingException {

        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/api/events/getAllEvents", String.class);
        System.out.println("svsdv" + response);

        // Kontrollib status koodi
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Deserialaisib string jsoni eventresponcedto listiks
        List<EventResponseDto> eventsFromResponse = objectMapper.readValue(
                response.getBody(),
                new TypeReference<List<EventResponseDto>>() {
                }
        );

        // Andmebaasi päring kontrolliks
        long eventsCountInDatabase = eventRepository.count(); // Assuming eventRepository is your repository

        // Kontrollib kas päringuvastusena saadud ürituste arv kattub andmebaasis olevate ürituste arvuga
        assertThat(eventsFromResponse).hasSize((int) eventsCountInDatabase);
    }


}