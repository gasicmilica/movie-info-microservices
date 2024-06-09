package se.magnus.microservices.core.rating;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT)
class RatingServiceApplicationTests {

    @Autowired
    private WebTestClient client;

    @Test
    public void getRatingsByMovieId() {

        int movieId = 1;

        client.get()
                .uri("/rating?movieId=" + movieId)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[0].movieId").isEqualTo(movieId);
    }

    @Test
    public void getRatingsMissingParameter() {

        client.get()
                .uri("/rating")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/rating")
                .jsonPath("$.message").isEqualTo("Required int parameter 'movieId' is not present");
    }

    @Test
    public void getRAtingsInvalidParameter() {

        client.get()
                .uri("/rating?movieId=no-integer")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/rating")
                .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    public void getRatingsNotFound() {

        int movieIdNotFound = 213;

        client.get()
                .uri("/rating?movieId=" + movieIdNotFound)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    public void getRatingsInvalidParameterNegativeValue() {

        int movieIdInvalid = -1;

        client.get()
                .uri("/rating?movieId=" + movieIdInvalid)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/rating")
                .jsonPath("$.message").isEqualTo("Invalid movieId: " + movieIdInvalid);
    }
}
