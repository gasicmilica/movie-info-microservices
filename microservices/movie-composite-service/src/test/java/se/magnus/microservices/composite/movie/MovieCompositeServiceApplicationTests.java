package se.magnus.microservices.composite.movie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.comment.Comment;
import se.magnus.api.core.movie.Movie;
import se.magnus.api.core.rating.Rating;
import se.magnus.api.core.screening.Screening;
import se.magnus.microservices.composite.movie.services.MovieCompositeIntegration;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;

import java.util.Date;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT)
class MovieCompositeServiceApplicationTests {

    private static final int MOVIE_ID_OK = 1;
    private static final int MOVIE_ID_NOT_FOUND = 2;
    private static final int MOVIE_ID_INVALID = 3;

    @Autowired
    private WebTestClient client;

    @MockBean
    private MovieCompositeIntegration compositeIntegration;

    @BeforeEach
    public void setUp() {

        when(compositeIntegration.getMovie(MOVIE_ID_OK)).
                thenReturn(new Movie(MOVIE_ID_OK, "title", "Director", 2010, 2, "Genre", "mock-address"));
        when(compositeIntegration.getComments(MOVIE_ID_OK)).
                thenReturn(singletonList(new Comment(MOVIE_ID_OK, 1, "author", new Date(), "content", "mock address")));
        when(compositeIntegration.getRatings(MOVIE_ID_OK)).
                thenReturn(singletonList(new Rating(MOVIE_ID_OK, 1, "author", new Date(), 6, "mock address")));
        when(compositeIntegration.getScreenings(MOVIE_ID_OK)).
                thenReturn(singletonList(new Screening(MOVIE_ID_OK, 1, "Cineplex", new Date(), 6, "Omladinska 1, Novi Sad", "mock address")));

        when(compositeIntegration.getMovie(MOVIE_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + MOVIE_ID_NOT_FOUND));

        when(compositeIntegration.getMovie(MOVIE_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + MOVIE_ID_INVALID));
    }

    @Test
    public void getMovieById() {

        client.get()
                .uri("/movie-composite/" + MOVIE_ID_OK)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.movieId").isEqualTo(MOVIE_ID_OK)
                .jsonPath("$.comments.length()").isEqualTo(1)
                .jsonPath("$.ratings.length()").isEqualTo(1)
                .jsonPath("$.screenings.length()").isEqualTo(1);
    }

    @Test
    public void getMovieNotFound() {

        client.get()
                .uri("/movie-composite/" + MOVIE_ID_NOT_FOUND)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(APPLICATION_JSON) 
                .expectBody()
                .jsonPath("$.path").isEqualTo("/movie-composite/" + MOVIE_ID_NOT_FOUND)
                .jsonPath("$.message").isEqualTo("NOT FOUND: " + MOVIE_ID_NOT_FOUND);
    }

    @Test
    public void getMovieInvalidInput() {

        client.get()
                .uri("/movie-composite/" + MOVIE_ID_INVALID)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/movie-composite/" + MOVIE_ID_INVALID)
                .jsonPath("$.message").isEqualTo("INVALID: " + MOVIE_ID_INVALID);
    }
}
