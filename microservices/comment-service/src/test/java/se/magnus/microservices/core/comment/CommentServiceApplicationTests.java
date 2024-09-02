package se.magnus.microservices.core.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.comment.Comment;
import org.springframework.http.HttpStatus;
import se.magnus.api.core.movie.Movie;
import se.magnus.api.event.Event;
import se.magnus.microservices.core.comment.persistence.CommentRepository;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT)
class CommentServiceApplicationTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private CommentRepository repository;

    @Autowired
    private Sink channels;

    private AbstractMessageChannel input = null;

    @BeforeEach
    public void setupDb() {
        input = (AbstractMessageChannel) channels.input();
        repository.deleteAll().block();
    }

    @Test
    public void getCommentsByMovieId() {

        int movieId = 1;

        sendCreateCommentEvent(movieId, 1);
        sendCreateCommentEvent(movieId, 2);
        sendCreateCommentEvent(movieId, 3);

        assertEquals(3, (long) repository.findByMovieId(movieId).count().block());

        getAndVerifyCommentsByMovieId(movieId, OK)
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[2].movieId").isEqualTo(movieId);
    }

    @Test
    public void getCommentsMissingParameter() {

        getAndVerifyCommentsByMovieId("", BAD_REQUEST)
                .jsonPath("$.path").isEqualTo("/comment")
                .jsonPath("$.message").isEqualTo("Required int parameter 'movieId' is not present");
    }

    @Test
    public void getCommentsInvalidParameter() {

        client.get()
                .uri("/comment?movieId=no-integer")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/comment")
                .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    public void getCommentsNotFound() {

        getAndVerifyCommentsByMovieId("?movieId=113", OK)
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    public void getCommentsInvalidParameterNegativeValue() {

        int movieIdInvalid = -1;


        getAndVerifyCommentsByMovieId("?movieId=" + movieIdInvalid, UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo("/comment")
                .jsonPath("$.message").isEqualTo("Invalid movieId: " + movieIdInvalid);
    }

    private WebTestClient.BodyContentSpec getAndVerifyCommentsByMovieId(int movieId, HttpStatus expectedStatus) {
        return getAndVerifyCommentsByMovieId("?movieId=" + movieId, expectedStatus);
    }

    private WebTestClient.BodyContentSpec getAndVerifyCommentsByMovieId(String movieIdQuery, HttpStatus expectedStatus) {
        return client.get()
                .uri("/comment" + movieIdQuery)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    private void sendCreateCommentEvent(int movieId, int commentId) {
        Comment comment = new Comment(movieId, commentId,"author", new Date(), "content", "mock address");
        Event<Integer, Movie> event = new Event(CREATE, movieId, comment);
        input.send(new GenericMessage<>(event));
    }

    private void sendDeleteRecommendationEvent(int movieId) {
        Event<Integer, Movie> event = new Event(DELETE, movieId, null);
        input.send(new GenericMessage<>(event));
    }
}
