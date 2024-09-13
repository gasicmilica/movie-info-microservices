package se.magnus.microservices.core.screening;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.screening.Screening;
import se.magnus.api.event.Event;
import se.magnus.microservices.core.screening.persistence.ScreeningRepository;
import se.magnus.util.exceptions.InvalidInputException;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {
        "logging.level.se.magnus=DEBUG",
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:screening-db",
        "server.error.include-message=always"})
class ScreeningServiceApplicationTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private ScreeningRepository repository;

    @Autowired
    private Sink channels;

    private AbstractMessageChannel input = null;

    @BeforeEach
    public void setupDb() {
        input = (AbstractMessageChannel) channels.input();
        repository.deleteAll();
    }

    @Test
    public void getScreeningsByMovieId() {

        int movieId = 1;

        assertEquals(0, repository.findByMovieId(movieId).size());

        sendCreateScreeningEvent(movieId, 1);
        sendCreateScreeningEvent(movieId, 2);
        sendCreateScreeningEvent(movieId, 3);

        assertEquals(3, repository.findByMovieId(movieId).size());

        getAndVerifyScreeningsByMovieId(movieId, OK)
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[2].movieId").isEqualTo(movieId)
                .jsonPath("$[2].screeningId").isEqualTo(3);
    }

    @Test
    public void duplicateError() {

        int movieId = 1;
        int screeningId = 1;

        assertEquals(0, repository.count());

        sendCreateScreeningEvent(movieId, screeningId);

        assertEquals(1, repository.count());

        try {
            sendCreateScreeningEvent(movieId, screeningId);
            fail("Expected a MessagingException here!");
        } catch (MessagingException me) {
            if (me.getCause() instanceof InvalidInputException)	{
                InvalidInputException iie = (InvalidInputException)me.getCause();
                assertEquals("Duplicate key, Movie Id: 1, Screening Id:1", iie.getMessage());
            } else {
                fail("Expected a InvalidInputException as the root cause!");
            }
        }

        assertEquals(1, repository.count());
    }

    @Test
    public void deleteScreenings() {
        int movieId = 1;
        int screeningId = 1;

        sendCreateScreeningEvent(movieId, screeningId);
        assertEquals(1, repository.findByMovieId(movieId).size());

        sendDeleteScreeningEvent(movieId);
        assertEquals(0, repository.findByMovieId(movieId).size());

        sendDeleteScreeningEvent(movieId);
    }
    @Test
    public void getScreeningsMissingParameter() {
        getAndVerifyScreeningsByMovieId("", BAD_REQUEST)
                .jsonPath("$.path").isEqualTo("/screening")
                .jsonPath("$.message").isEqualTo("Required int parameter 'movieId' is not present");
    }

    @Test
    public void getScreeningsInvalidParameter() {
        getAndVerifyScreeningsByMovieId("?movieId=no-integer", BAD_REQUEST)
                .jsonPath("$.path").isEqualTo("/screening")
                .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    public void getScreeningsNotFound() {
        getAndVerifyScreeningsByMovieId("?movieId=313", OK)
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    public void getScreeningsInvalidParameterNegativeValue() {
        int movieIdInvalid = -1;

        getAndVerifyScreeningsByMovieId("?movieId=" + movieIdInvalid, UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo("/screening")
                .jsonPath("$.message").isEqualTo("Invalid movieId: " + movieIdInvalid);
    }

    private WebTestClient.BodyContentSpec getAndVerifyScreeningsByMovieId(int movieId, HttpStatus expectedStatus) {
        return getAndVerifyScreeningsByMovieId("?movieId=" + movieId, expectedStatus);
    }

    private WebTestClient.BodyContentSpec getAndVerifyScreeningsByMovieId(String movieIdQuery, HttpStatus expectedStatus) {
        return client.get()
                .uri("/screening" + movieIdQuery)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    private void sendCreateScreeningEvent(int movieId, int screeningId) {
        Screening screening = new Screening(movieId, screeningId, "cinema", new Date(), 1200, "location", "mock-address");
        Event<Integer, Screening> event = new Event(CREATE, movieId, screening);
        input.send(new GenericMessage<>(event));
    }

    private void sendDeleteScreeningEvent(int movieId) {
        Event<Integer, Screening> event = new Event(DELETE, movieId, null);
        input.send(new GenericMessage<>(event));
    }

}
