package se.magnus.microservices.core.rating;

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
import se.magnus.api.core.movie.Movie;
import se.magnus.api.core.rating.Rating;
import se.magnus.api.event.Event;
import se.magnus.microservices.core.rating.persistence.RatingRepository;
import se.magnus.util.exceptions.InvalidInputException;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0", "eureka.client.enabled=false", "spring.cloud.config.enabled=false", "server.error.include-message=always"})
class RatingServiceApplicationTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private RatingRepository repository;

    @Autowired
    private Sink channels;

    private AbstractMessageChannel input = null;

    @BeforeEach
    public void setupDb() {
        input = (AbstractMessageChannel) channels.input();
        repository.deleteAll().block();
    }

    @Test
    public void getRatingsByMovieId() {

        int movieId = 1;

        sendCreateRatingEvent(movieId, 1);
        sendCreateRatingEvent(movieId, 2);
        sendCreateRatingEvent(movieId, 3);

        assertEquals(3, (long) repository.findByMovieId(movieId).count().block());

        getAndVerifyRatingsByMovieId(movieId, OK)
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[2].movieId").isEqualTo(movieId)
                .jsonPath("$[2].ratingId").isEqualTo(3);
    }

    @Test
    public void getRatingsMissingParameter() {

        getAndVerifyRatingsByMovieId("", BAD_REQUEST)
                .jsonPath("$.path").isEqualTo("/rating")
                .jsonPath("$.message").isEqualTo("Required int parameter 'movieId' is not present");
    }

    @Test
    public void getRatingsInvalidParameter() {

        getAndVerifyRatingsByMovieId("?movieId=no-integer", BAD_REQUEST)
                .jsonPath("$.path").isEqualTo("/rating")
                .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    public void getRatingsNotFound() {

        getAndVerifyRatingsByMovieId("?movieId=113", OK)
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    public void getRatingsInvalidParameterNegativeValue() {

        int movieIdInvalid = -1;

        getAndVerifyRatingsByMovieId("?movieId=" + movieIdInvalid, UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo("/rating")
                .jsonPath("$.message").isEqualTo("Invalid movieId: " + movieIdInvalid);
    }

    @Test
    public void deleteRatings() {

        int movieId = 1;
        int ratingId = 1;

        sendCreateRatingEvent(movieId, ratingId);
        assertEquals(1, (long)repository.findByMovieId(movieId).count().block());

        sendDeleteRatingEvent(movieId);
        assertEquals(0, (long)repository.findByMovieId(movieId).count().block());

        sendDeleteRatingEvent(movieId);
    }


    private WebTestClient.BodyContentSpec getAndVerifyRatingsByMovieId(int movieId, HttpStatus expectedStatus) {
        return getAndVerifyRatingsByMovieId("?movieId=" + movieId, expectedStatus);
    }

    private WebTestClient.BodyContentSpec getAndVerifyRatingsByMovieId(String movieIdQuery, HttpStatus expectedStatus) {
        return client.get()
                .uri("/rating" + movieIdQuery)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    @Test
    public void duplicateError() {

        int movieId = 1;
        int ratingId = 1;

        sendCreateRatingEvent(movieId, ratingId);

        assertEquals(1, (long)repository.count().block());

        try {
            sendCreateRatingEvent(movieId, ratingId);
            fail("Expected a MessagingException here!");
        } catch (MessagingException me) {
            if (me.getCause() instanceof InvalidInputException)	{
                InvalidInputException iie = (InvalidInputException)me.getCause();
                assertEquals("Duplicate key, Movie Id: 1, Rating Id:1", iie.getMessage());
            } else {
                fail("Expected a InvalidInputException as the root cause!");
            }
        }

        assertEquals(1, (long)repository.count().block());
    }

    private void sendCreateRatingEvent(int movieId, int ratingId) {
        Rating rating = new Rating(movieId, ratingId,"author", new Date(), 7, "mock address");
        Event<Integer, Movie> event = new Event(CREATE, movieId, rating);
        input.send(new GenericMessage<>(event));
    }

    private void sendDeleteRatingEvent(int movieId) {
        Event<Integer, Movie> event = new Event(DELETE, movieId, null);
        input.send(new GenericMessage<>(event));
    }
}
