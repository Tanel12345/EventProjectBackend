package ee.MinuTood.Quest.registration.system.userInterface.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ee.MinuTood.Quest.registration.system.domain.event.Event;
import ee.MinuTood.Quest.registration.system.domain.event.entities.IndividualAttendee;
import ee.MinuTood.Quest.registration.system.domain.event.entities.LegalAttendee;
import ee.MinuTood.Quest.registration.system.domain.event.repositories.EventRepository;
import ee.MinuTood.Quest.registration.system.domain.event.valueobjects.LocationAddress;
import ee.MinuTood.Quest.registration.system.userInterface.dtos.*;
import ee.MinuTood.Quest.registration.system.userInterface.enums.PaymentMethod;
import io.restassured.response.Response;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Copy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testi klass Event agregaadiga seotud Eventcontroller API de testimiseks. Tegemist on end to end testidega
 * mis ei pruugi olla üksteisest sõltumatud. Mõeldud käivitamiseks ükshaaval. Iga test testib ühte API meetodit
 *
 * @author Tanel Sepp
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//Loads an EmbeddedWebApplicationContext and provides a real servlet environment. Embedded servlet containers are started and listening on a random port.
@AutoConfigureMockMvc
@Transactional
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
        // Päring andmebaasist lisatud sündmuse saamiseks
        List<Event> addedEventsBefore = eventRepository.findAll();
        System.out.println(addedEventsBefore.size());

        // Loo päringu keha JSON stringiks
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

        // Serializeeri objekt
        String requestBody = objectMapper.writeValueAsString(eventRequestDto);
        System.out.println(requestBody);



        // Päringu tegemine RestAssured libarit kasutades.
        // (RestTemplate is another way to make HTTP requests in Java.
        // It's a synchronous client for performing HTTP requests in Spring Framework. While RestAssured is primarily
        // focused on testing RESTful APIs, RestTemplate is often used for making HTTP requests within applications,
        // such as in microservices architectures. Both RestAssured and RestTemplate serve similar purposes but have different use cases and APIs.)
        Response response = given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when()
                .post("/api/events/createEventWithoutAttendees")
                .andReturn();

        // Kontrolli vastuse staatusekoodi
        response.then().statusCode(201);

        // Deserialiseeri vastus ApiResponse objektiks
        ApiResponse apiResponse = response.as(ApiResponse.class);

        // Kontrolli vastuses olevat sõnumit
        assertThat(apiResponse.getMessage()).isEqualTo("Uus üritus on loodud");



        // Kontrolli, et vähemalt üks sündmus on andmebaasis olemas
        assertThat(addedEventsBefore).isNotEmpty();

        await().atMost(5, SECONDS).untilAsserted(() -> {
            List<Event> addedEventsAfter = eventRepository.findAll();
            System.out.println(addedEventsAfter.size());

            // Kontrollib, et ürituste arv on suurenenud ühe võrra
            assertThat(addedEventsAfter.size()).isEqualTo(addedEventsBefore.size() + 1);
        });
    }

    /**
     * Test kontrollib andmebaasis olevate ürituste ja api päringu kaudu saadud ürituste arvu.
     * Test kasutab RestTemplate, Springi pakutavat http päringuid teostavat raamistikku
     *
     * @throws JsonProcessingException
     */

    @Test
    public void testGetAllEvents() throws JsonProcessingException {
        //Loome ürituse requestDto ja lisame üritus andmebaasi tabelisse
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
        System.out.println(eventRequestDto);
        ResponseEntity<ApiResponse> addEventResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/events/createEventWithoutAttendees",
                eventRequestDto,
                ApiResponse.class
        );
        // Kontrollime et lisamine oli edukas
        assertThat(addEventResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(addEventResponse.getBody()).isNotNull();

        //Extractime vastuse body
        ApiResponse responseBody =  addEventResponse.getBody();
        //Eeldame et vastuse bodyle antud lisatud evendi id ei ole null
        assertThat(responseBody.getEventId()).isNotNull();
        //Extractime evendi id
        Long eventId = responseBody.getEventId();



        //Resttemplatega päringu tegemine
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/api/events/getAllEvents", String.class);


        // Kontrollib status koodi
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Deserialaisib string jsoni eventresponcedto listiks
        List<EventResponseDto> eventsFromResponse = objectMapper.readValue(
                response.getBody(),
                new TypeReference<List<EventResponseDto>>() {
                }
        );

        // Andmebaasi päring kontrolliks
        long eventsCountInDatabase = eventRepository.count();

        // Kontrollib kas päringuvastusena saadud ürituste arv kattub andmebaasis olevate ürituste arvuga
        assertThat(eventsFromResponse).hasSize((int) eventsCountInDatabase);
    }

    /**
     * Test kontrollib kas andmebaasi lisandus eraisikust osaleja.
     * Test kasutab RestTemplate, Springi pakutavat http päringuid teostavat raamistikku
     *
     * @throws JsonProcessingException
     */

    @Test
    public void testIndividualAttendeeToEventId() throws JsonProcessingException {
        //Loome ürituse requestDto ja lisame üritus andmebaasi tabelisse
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
        System.out.println(eventRequestDto);
        ResponseEntity<ApiResponse> addEventResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/events/createEventWithoutAttendees",
                eventRequestDto,
                ApiResponse.class
        );
        // Kontrollime et lisamine oli edukas
        assertThat(addEventResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(addEventResponse.getBody()).isNotNull();

        //Extractime vastuse body
        ApiResponse responseBody =  addEventResponse.getBody();
        //Eeldame et vastuse bodyle antud lisatud evendi id ei ole null
        assertThat(responseBody.getEventId()).isNotNull();
        //Extractime evendi id
        Long eventId = responseBody.getEventId();

        //Nüüd on vähemalt üks event sisestatud
       List<Event> events = eventRepository.findAll();
       Event event = events.get(0);
        //Pärib andmebaasist selle evendi
        EventResponseDto eventDetails = restTemplate.getForObject(
                "http://localhost:" + port + "/api/events/getEventDetails/"+ event.getId(),
                EventResponseDto.class);
        Long initialAttendeesCount = eventDetails.getAttendeesCount(); //üritusest osavõtjate arv

        //Lisab üritusele ühe eraisikust osaleja

        IndividualAttendeeRequestDto individualAttendeeRequestDto = new IndividualAttendeeRequestDto();
        individualAttendeeRequestDto.setFirstName("Tanel");
        individualAttendeeRequestDto.setLastName("Sepp");
        individualAttendeeRequestDto.setPersonalCode("12321232123");
        individualAttendeeRequestDto.setPaymentMethod(PaymentMethod.CASH);

        // loome päringule headerid
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Loome HttpEntity koos headerite ja bodyga
        HttpEntity<IndividualAttendeeRequestDto> requestEntity = new HttpEntity<>(individualAttendeeRequestDto, headers);

        // Teostame Post päringu
        ResponseEntity<IndividualAttendee> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/events/addIndividualAttendeeToEventId/"+event.getId(),
                HttpMethod.POST,
                requestEntity,
                IndividualAttendee.class

        );

        // Eeldame et vastuse staatus on 201 ja body on olemas.
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());


        //Pärib andmebaasist uuesti sama ürituse
        EventResponseDto neweventDetails = restTemplate.getForObject(
                "http://localhost:" + port + "/api/events/getEventDetails/"+event.getId(),
                EventResponseDto.class);
        Long updatedAttendeesCount = neweventDetails.getAttendeesCount(); //Esimesest üritusest osavõtjate arv nüüd
        // Eeldame et osavõtjaid on ühe võrra rohkem

        assertEquals(initialAttendeesCount + 1, updatedAttendeesCount);

    }

    /**
     * Test kontrollib kas andmebaasi lisandus ettevõttest osaleja.
     * Test kasutab RestTemplate, Springi pakutavat http päringuid teostavat raamistikku
     *
     * @throws JsonProcessingException
     */

    @Test
    public void testLegalAttendeeToEventId() throws JsonProcessingException {
//Loome ürituse requestDto ja lisame üritus andmebaasi tabelisse
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
        System.out.println(eventRequestDto);
        ResponseEntity<ApiResponse> addEventResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/events/createEventWithoutAttendees",
                eventRequestDto,
                ApiResponse.class
        );
        // Kontrollime et lisamine oli edukas
        assertThat(addEventResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(addEventResponse.getBody()).isNotNull();

        //Extractime vastuse body
        ApiResponse responseBody =  addEventResponse.getBody();
        //Eeldame et vastuse bodyle antud lisatud evendi id ei ole null
        assertThat(responseBody.getEventId()).isNotNull();
        //Extractime evendi id
        Long eventId = responseBody.getEventId();




        //Vähemalt üks event on sisestatud
        List<Event> events = eventRepository.findAll();
        Event event = events.get(0);

        //Pärib andmebaasist esimese ürituse
        EventResponseDto eventDetails = restTemplate.getForObject(
                "http://localhost:" + port + "/api/events/getEventDetails/"+event.getId(),
                EventResponseDto.class);
        Long initialAttendeesCount = eventDetails.getAttendeesCount(); //Esimesest üritusest osavõtjate arv


        //Lisab üritusele ühe eraisikust osaleja

        LegalAttendeeRequestDto legalAttendeeRequestDto = new LegalAttendeeRequestDto();
        legalAttendeeRequestDto.setCompanyName("Tanel");
        legalAttendeeRequestDto.setRegistrationCode(123212312L);
        legalAttendeeRequestDto.setPaymentMethod(PaymentMethod.BANKTRANSFER);
        legalAttendeeRequestDto.setNumberOfParticipant(12L);

        // loome Päringule headerid
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Loome HttpEntity koos headerite ja bodyga
        HttpEntity<LegalAttendeeRequestDto> requestEntity = new HttpEntity<>(legalAttendeeRequestDto, headers);

        // Teostame Post päringu üritusele osaleja lisamiseks
        ResponseEntity<LegalAttendee> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/events/addLegalAttendeeToEventId/"+event.getId(),
                HttpMethod.POST,
                requestEntity,
                LegalAttendee.class

        );

        // Eeldame et vastuse staatus on 201 ja body on olemas.
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());


        //Pärib andmebaasist uuesti sama ürituse
        EventResponseDto neweventDetails = restTemplate.getForObject(
                "http://localhost:" + port + "/api/events/getEventDetails/"+event.getId(),
                EventResponseDto.class);
        Long updatedAttendeesCount = neweventDetails.getAttendeesCount(); //Esimesest üritusest osavõtjate arv nüüd
        // Eeldame et osavõtjaid on ühe võrra rohkem
        assertEquals(initialAttendeesCount + 1, updatedAttendeesCount);

    }

    /**
     * Test kontrollib kas saab soovitud ürituse, sisestatud ürituse id abil.
     * Test kasutab RestTemplate, Springi pakutavat http päringuid teostavat raamistikku
     *
     * @throws JsonProcessingException
     */

    @Test
    public void testGetEventDetails() throws JsonProcessingException {

        //Loome ürituse requestDto ja lisame üritus andmebaasi tabelisse
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
        System.out.println(eventRequestDto);
        ResponseEntity<ApiResponse> addEventResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/events/createEventWithoutAttendees",
                eventRequestDto,
                ApiResponse.class
        );
        // Kontrollime et lisamine oli edukas
        assertThat(addEventResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(addEventResponse.getBody()).isNotNull();

       //Extractime vastuse body
       ApiResponse responseBody =  addEventResponse.getBody();
       //Eeldame et vastuse bodyle antud lisatud evendi id ei ole null
       assertThat(responseBody.getEventId()).isNotNull();
       //Extractime evendi id
       Long eventId = responseBody.getEventId();



       //Teeme get päringu lisatud evendi id ga
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/events/getEventDetails/" + String.valueOf(eventId),
                String.class
        );
//
        // Kontrollime vastuse koodi
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Deserialiseme jsoni EventResponseDtoks
        EventResponseDto eventFromResponse = objectMapper.readValue(
                response.getBody(),
                EventResponseDto.class
        );
//
        // Tõmbame sama id ga evendi otse andmebaasist
        Optional<Event> eventFromDatabase = eventRepository.findById(eventId);

        // Eeldame et event on olemas
        assertThat(eventFromDatabase).isPresent();

        // Kui on olemas siis võrdleme kas päringuvastusena saadud event on võrdne andmebaasist otse tõmmatuga.
        eventFromDatabase.ifPresent(event -> {
            assertThat(eventFromResponse.getId()).isEqualTo(event.getId());

        });
    }

    /**
     * Test kontrollib kas saab soovitud ürituse, sisestatud ürituse id abil, mis tuleb koos osalejatega.
     * Test kasutab RestTemplate, Springi pakutavat http päringuid teostavat raamistikku
     *
     * @throws JsonProcessingException
     */

    @Test
    public void testgetEventDetailsWithAttendeesById() throws JsonProcessingException {

        //Loome ürituse requestDto ja lisame üritus andmebaasi tabelisse
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
        ResponseEntity<ApiResponse> addEventResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/events/createEventWithoutAttendees",
                eventRequestDto,
                ApiResponse.class
        );
        // Kontrollime et lisamine oli edukas
        assertThat(addEventResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(addEventResponse.getBody()).isNotNull();

        //Extractime vastuse body
        ApiResponse responseBody =  addEventResponse.getBody();
        //Eeldame et vastuse bodyle antud lisatud evendi id ei ole null
        assertThat(responseBody.getEventId()).isNotNull();
        //Extractime evendi id
        Long eventId = responseBody.getEventId();


        //Teeme get päringu lisatud evendi id ga

        ResponseEntity<EventResponseDto> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/events/getEventDetailsWithAttendeesById/" + eventId,
                EventResponseDto.class
        );

        // Kontrollime vastuse koodi
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

//        Extractime vastusest EventRespomseDto
        EventResponseDto eventFromResponse = response.getBody();

        // Tõmbame sama id ga evendi otse andmebaasist
        Optional<Event> eventFromDatabase = eventRepository.findById(eventId);

        // Eeldame et event on olemas
        assertThat(eventFromDatabase).isPresent();

        // Kui on olemas siis võrdleme kas päringuvastusena saadud event on võrdne andmebaasist otse tõmmatuga.
        eventFromDatabase.ifPresent(event -> {
            assertThat(eventFromResponse.getId()).isEqualTo(event.getId());

        });
    }

    /**
     * Test kontrollib kas saab kustutada soovitud ürituse, sisestatud ürituse id abil.
     * Test kasutab RestTemplate, Springi pakutavat http päringuid teostavat raamistikku
     *
     * @throws JsonProcessingException
     */

    @Test
    public void testDeleteEventById() {
        //Loome ürituse requestDto ja lisame üritus andmebaasi tabelisse
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
        ResponseEntity<ApiResponse> addEventResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/events/createEventWithoutAttendees",
                eventRequestDto,
                ApiResponse.class
        );
        // Kontrollime et lisamine oli edukas
        assertThat(addEventResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(addEventResponse.getBody()).isNotNull();

        // Extractime saadud vastusest id
        ApiResponse addedEvent = addEventResponse.getBody();
        assertThat(addedEvent).isNotNull();
        Long eventId = addedEvent.getEventId();


        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/events/deleteEventById/" + eventId,
                HttpMethod.DELETE,
                null,
                String.class);


        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("{\"message\":\"Üritus kustutatud edukalt\",\"eventId\":0}");
    }

    /**
     * Test kontrollib kas saab kustutada soovitud eraisikust osaleja soovitud ürituselt
     * Test kasutab RestTemplate, Springi pakutavat http päringuid teostavat raamistikku
     *
     * @throws JsonProcessingException
     */

    @Test
    public void testDeleteIndividualAttendeeFromEvent() {
        //Loome ürituse requestDto ja lisame üritus andmebaasi tabelisse
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
        ResponseEntity<ApiResponse> addEventResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/events/createEventWithoutAttendees",
                eventRequestDto,
                ApiResponse.class
        );
        // Kontrollime et lisamine oli edukas
        assertThat(addEventResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(addEventResponse.getBody()).isNotNull();

        //Extractime vastuse body
        ApiResponse responseBody =  addEventResponse.getBody();
        //Eeldame et vastuse bodyle antud lisatud evendi id ei ole null
        assertThat(responseBody.getEventId()).isNotNull();
        //Extractime evendi id
        Long eventId = responseBody.getEventId();



        //Loome ja lisame Osaleja
        IndividualAttendeeRequestDto individualAttendeeRequestDto = new IndividualAttendeeRequestDto();
        individualAttendeeRequestDto.setFirstName("Tanel");
        individualAttendeeRequestDto.setLastName("Sepp");
        individualAttendeeRequestDto.setPersonalCode("12321232123");
        individualAttendeeRequestDto.setPaymentMethod(PaymentMethod.CASH);

        // loome päringule headerid
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Loome HttpEntity koos headerite ja bodyga
        HttpEntity<IndividualAttendeeRequestDto> requestEntity = new HttpEntity<>(individualAttendeeRequestDto, headers);

        // Teostame Post päringu
        ResponseEntity<IndividualAttendee> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/events/addIndividualAttendeeToEventId/"+eventId,
                HttpMethod.POST,
                requestEntity,
                IndividualAttendee.class

        );

        // Eeldame et vastuse staatus on 201 ja body on olemas.
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        //Extractime lisatud osaleja id
        IndividualAttendee addedAttendee = response.getBody();
        Long individualAttendeeId = addedAttendee.getId();



        //Kustutame eelnevalt lisatud osaleja ürituselt
        ResponseEntity<String> response1 = restTemplate.exchange(
                "http://localhost:" + port + "/api/events/" + eventId + "/individualAttendees/" + individualAttendeeId,
                HttpMethod.DELETE,
                null,
                String.class);

        assertThat(response1.getStatusCodeValue()).isEqualTo(200);
        assertThat(response1.getBody()).isEqualTo("{\"message\":\"Eraisikust Osavõtja kustutati edukalt\",\"eventId\":0}");


    }

    /**
     * Test kontrollib kas saab kustutada soovitud ettevõttest osaleja soovitud ürituselt
     * Test kasutab RestTemplate, Springi pakutavat http päringuid teostavat raamistikku
     *
     * @throws JsonProcessingException
     */

    @Test
    public void testDeleteLegalAttendeeFromEvent() {
        //Loome ürituse requestDto ja lisame üritus andmebaasi tabelisse
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
        ResponseEntity<ApiResponse> addEventResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/events/createEventWithoutAttendees",
                eventRequestDto,
                ApiResponse.class
        );
        // Kontrollime et lisamine oli edukas
        assertThat(addEventResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(addEventResponse.getBody()).isNotNull();

        //Extractime vastuse body
        ApiResponse responseBody =  addEventResponse.getBody();
        //Eeldame et vastuse bodyle antud lisatud evendi id ei ole null
        assertThat(responseBody.getEventId()).isNotNull();
        //Extractime evendi id
        Long eventId = responseBody.getEventId();




        //Loome ja lisame Osaleja
        LegalAttendeeRequestDto legalAttendeeRequestDto = new LegalAttendeeRequestDto();
        legalAttendeeRequestDto.setCompanyName("businesname");
        legalAttendeeRequestDto.setRegistrationCode(1232123L);
        legalAttendeeRequestDto.setNumberOfParticipant(2L);
        legalAttendeeRequestDto.setPaymentMethod(PaymentMethod.CASH);

        // loome päringule headerid
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Loome HttpEntity koos headerite ja bodyga
        HttpEntity<LegalAttendeeRequestDto> requestEntity = new HttpEntity<>(legalAttendeeRequestDto, headers);

        // Teostame Post päringu loodud osaleja lisamiseks.
        ResponseEntity<LegalAttendee> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/events/addLegalAttendeeToEventId/"+eventId,
                HttpMethod.POST,
                requestEntity,
                LegalAttendee.class

        );

        // Eeldame et vastuse staatus on 201 ja body on olemas.
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        //Extractime lisatud osaleja id
        LegalAttendee addedAttendee = response.getBody();
        Long legalAttendeeId = addedAttendee.getId();

        System.out.println(legalAttendeeId+"   "+eventId);



        //Kustutame eelnevalt lisatud osaleja ürituselt nr 1
        ResponseEntity<String> response1 = restTemplate.exchange(
                "http://localhost:" + port + "/api/events/" + eventId + "/legalAttendees/" + legalAttendeeId,
                HttpMethod.DELETE,
                null,
                String.class);

        assertThat(response1.getStatusCodeValue()).isEqualTo(200);
        assertThat(response1.getBody()).isEqualTo("{\"message\":\"Ettevõttest Osavõtja kustutati edukalt\",\"eventId\":0}");


    }

    /**
     * Test kontrollib kas saab uuendada soovitud eraisikust osalejat soovitud üritusel
     * Test kasutab RestTemplate, Springi pakutavat http päringuid teostavat raamistikku
     *
     * @throws JsonProcessingException
     */
    @Test
    public void testUpdateIndividualAttendeeFromEvent() {
        //Loome ürituse requestDto ja lisame üritus andmebaasi tabelisse
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
        ResponseEntity<ApiResponse> addEventResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/events/createEventWithoutAttendees",
                eventRequestDto,
                ApiResponse.class
        );
        // Kontrollime et lisamine oli edukas
        assertThat(addEventResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(addEventResponse.getBody()).isNotNull();

        //Extractime vastuse body
        ApiResponse responseBody =  addEventResponse.getBody();
        //Eeldame et vastuse bodyle antud lisatud evendi id ei ole null
        assertThat(responseBody.getEventId()).isNotNull();
        //Extractime evendi id
        Long eventId = responseBody.getEventId();



        //Loome ja lisame Osaleja
        IndividualAttendeeRequestDto individualAttendeeRequestDto = new IndividualAttendeeRequestDto();
        individualAttendeeRequestDto.setFirstName("Tanel");
        individualAttendeeRequestDto.setLastName("Sepp");
        individualAttendeeRequestDto.setPersonalCode("12321232123");
        individualAttendeeRequestDto.setPaymentMethod(PaymentMethod.CASH);

        // loome päringule headerid
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Loome HttpEntity koos headerite ja bodyga
        HttpEntity<IndividualAttendeeRequestDto> requestEntity = new HttpEntity<>(individualAttendeeRequestDto, headers);

        // Teostame Post päringu
        ResponseEntity<IndividualAttendee> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/events/addIndividualAttendeeToEventId/"+eventId,
                HttpMethod.POST,
                requestEntity,
                IndividualAttendee.class

        );

        // Eeldame et vastuse staatus on 201 ja body on olemas.
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        //Extractime lisatud osaleja id
        IndividualAttendee addedAttendee = response.getBody();
        Long individualAttendeeId = addedAttendee.getId();






        // Loob uue ja Uuendab osaleja dto objekti andmetega
        IndividualAttendeeRequestDto updatedAttendeeDto = new IndividualAttendeeRequestDto();
        updatedAttendeeDto.setFirstName("Updated Name2");
        updatedAttendeeDto.setLastName("Updated Last");
        updatedAttendeeDto.setPersonalCode("23432343234");
        updatedAttendeeDto.setPaymentMethod(PaymentMethod.CASH);

        //Teeme put päringu andes loodud muudetud andmetega dto objekti kaasa
        ResponseEntity<?> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/api/events/{eventId}/individualAttendees/{attendeeId}",
                HttpMethod.PUT,
                new HttpEntity<>(updatedAttendeeDto),
                String.class,
                eventId,
                individualAttendeeId);

        // Eeldame et vastus on ok 200
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());



        // Pärib andmebaasist uuesti sisestatud ürituse koos osalejatega
        Event newEventResponse = restTemplate.getForObject(
                "http://localhost:" + port + "/api/events/getEventDetailsWithAttendeesById/"+eventId,
                Event.class);
        List<IndividualAttendee> newIndividualAttendees = newEventResponse.getIndividualAttendees(); // Osalejate list
        IndividualAttendee newIndividualAttendee = newIndividualAttendees.get(0);

        // Võrdleme fielde updatetud osaleja ja updatemiseks loodud osaleja fieldidega
        assertEquals(newIndividualAttendee.getId(), individualAttendeeId);
        assertEquals(newIndividualAttendee.getFirstName(), updatedAttendeeDto.getFirstName());
        assertEquals(newIndividualAttendee.getPersonalCode(), updatedAttendeeDto.getPersonalCode());
        assertEquals(newIndividualAttendee.getPaymentMethod(), updatedAttendeeDto.getPaymentMethod());


    }

    /**
     * Test kontrollib kas saab uuendada soovitud ettevõttest osalejat soovitud üritusel
     * Test kasutab RestTemplate, Springi pakutavat http päringuid teostavat raamistikku
     *
     * @throws JsonProcessingException
     */
    @Test
    public void testUpdateLegalAttendeeFromEvent() {
        //Loome ürituse requestDto ja lisame üritus andmebaasi tabelisse
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
        ResponseEntity<ApiResponse> addEventResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/events/createEventWithoutAttendees",
                eventRequestDto,
                ApiResponse.class
        );
        // Kontrollime et lisamine oli edukas
        assertThat(addEventResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(addEventResponse.getBody()).isNotNull();

        //Extractime vastuse body
        ApiResponse responseBody =  addEventResponse.getBody();
        //Eeldame et vastuse bodyle antud lisatud evendi id ei ole null
        assertThat(responseBody.getEventId()).isNotNull();
        //Extractime evendi id
        Long eventId = responseBody.getEventId();




        //Loome ja lisame Osaleja
        LegalAttendeeRequestDto legalAttendeeRequestDto = new LegalAttendeeRequestDto();
        legalAttendeeRequestDto.setCompanyName("businesname");
        legalAttendeeRequestDto.setRegistrationCode(1232123L);
        legalAttendeeRequestDto.setNumberOfParticipant(2L);
        legalAttendeeRequestDto.setPaymentMethod(PaymentMethod.CASH);

        // loome päringule headerid
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Loome HttpEntity koos headerite ja bodyga
        HttpEntity<LegalAttendeeRequestDto> requestEntity = new HttpEntity<>(legalAttendeeRequestDto, headers);

        // Teostame Post päringu loodud osaleja lisamiseks.
        ResponseEntity<LegalAttendee> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/events/addLegalAttendeeToEventId/"+eventId,
                HttpMethod.POST,
                requestEntity,
                LegalAttendee.class

        );

        // Eeldame et vastuse staatus on 201 ja body on olemas.
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        //Extractime lisatud osaleja id
        LegalAttendee addedAttendee = response.getBody();
        Long legalAttendeeId = addedAttendee.getId();




        // Loob uue ja Uuendab osaleja dto objekti andmetega
        LegalAttendeeRequestDto updatedAttendeeDto = new LegalAttendeeRequestDto();
        updatedAttendeeDto.setCompanyName("Updated Name2");
        updatedAttendeeDto.setRegistrationCode(223243324L);
        updatedAttendeeDto.setNumberOfParticipant(3L);
        updatedAttendeeDto.setPaymentMethod(PaymentMethod.CASH);

        //Teeme put päringu andes loodud muudetud andmetega dto objekti kaasa
        ResponseEntity<?> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/api/events/{eventId}/legalAttendees/{attendeeId}",
                HttpMethod.PUT,
                new HttpEntity<>(updatedAttendeeDto),
                String.class,
                eventId,
                legalAttendeeId);

        // Eeldame et vastus on ok 200
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());


        // Pärib andmebaasist uuesti esimese ürituse koos osalejatega
        Event newEventResponse = restTemplate.getForObject(
                "http://localhost:" + port + "/api/events/getEventDetailsWithAttendeesById/"+eventId,
                Event.class);
        List<LegalAttendee> newLegalAttendees = newEventResponse.getLegalAttendees(); // Osalejate list
        LegalAttendee newLegalAttendee = newLegalAttendees.get(0);

        // Võrdleme fielde updatetud osaleja ja updatemiseks loodud osaleja fieldidega
        assertEquals(newLegalAttendee.getId(), legalAttendeeId);
        assertEquals(newLegalAttendee.getCompanyName(), updatedAttendeeDto.getCompanyName());
        assertEquals(newLegalAttendee.getRegistrationCode(), updatedAttendeeDto.getRegistrationCode());
        assertEquals(newLegalAttendee.getNumberOfParticipant(), updatedAttendeeDto.getNumberOfParticipant());
        assertEquals(newLegalAttendee.getPaymentMethod(), updatedAttendeeDto.getPaymentMethod());


    }
}


