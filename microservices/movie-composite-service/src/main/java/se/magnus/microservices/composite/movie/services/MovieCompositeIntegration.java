package se.magnus.microservices.composite.movie.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import se.magnus.api.core.comment.Comment;
import se.magnus.api.core.comment.CommentService;
import se.magnus.api.core.movie.Movie;
import se.magnus.api.core.movie.MovieService;
import se.magnus.api.core.rating.Rating;
import se.magnus.api.core.rating.RatingService;
import se.magnus.api.core.screening.Screening;
import se.magnus.api.core.screening.ScreeningService;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.HttpErrorInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class MovieCompositeIntegration implements MovieService, CommentService, RatingService, ScreeningService {

    private static final Logger LOG = LoggerFactory.getLogger(MovieCompositeIntegration.class);

    private final String movieServiceUrl;
    private final String commentServiceUrl;
    private final String ratingServiceUrl;
    private final String screeningServiceUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;


    @Autowired
    public MovieCompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,

            @Value("${app.movie-service.host}") String movieServiceHost,
            @Value("${app.movie-service.port}") int movieServicePort,

            @Value("${app.comment-service.host}") String commentServiceHost,
            @Value("${app.comment-service.port}") int commentServicePort,

            @Value("${app.rating-service.host}") String ratingServiceHost,
            @Value("${app.rating-service.port}") int ratingServicePort,

            @Value("${app.screening-service.host}") String screeningServiceHost,
            @Value("${app.screening-service.port}") int screeningServicePort
    ) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;

        this.movieServiceUrl = "http://" + movieServiceHost + ":" + movieServicePort + "/movie";
        this.screeningServiceUrl = "http://" + screeningServiceHost + ":" + screeningServicePort + "/screening";
        this.commentServiceUrl = "http://" + commentServiceHost + ":" + commentServicePort + "/comment";
        this.ratingServiceUrl = "http://" + ratingServiceHost + ":" + ratingServicePort + "/rating";
    }

    public Movie getMovie(int movieId) {
        try {
            String url = movieServiceUrl + "/" + movieId;
            LOG.debug("Will call the getMovie API on URL: {}", url);

            Movie product = restTemplate.getForObject(url, Movie.class);
            LOG.debug("Found a product with id: {}", product.getMovieId());

            return product;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Movie createMovie(Movie body) {
        try {
            String url = movieServiceUrl;
            LOG.debug("Will post a new movie to URL: {}", url);

            Movie movie = restTemplate.postForObject(url, body, Movie.class);
            LOG.debug("Created a movie with id: {}", movie.getMovieId());

            return movie;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteMovie(int movieId) {
        try {
            String url = movieServiceUrl + "/" + movieId;
            LOG.debug("Will call the deleteMovie API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<Comment> getComments(int movieId) {

        try {
            String url = commentServiceUrl + "?movieId=" + movieId;

            LOG.debug("Will call getComments API on URL: {}", url);
            List<Comment> comments = restTemplate.exchange(url, GET, null,
                    new ParameterizedTypeReference<List<Comment>>() {}).getBody();

            LOG.debug("Found {} comment for a movie with id: {}", comments.size(), movieId);
            return comments;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Comment createComment(Comment body) {
        try {
            String url = commentServiceUrl;
            LOG.debug("Will post a new comment to URL: {}", url);

            Comment recommendation = restTemplate.postForObject(url, body, Comment.class);
            LOG.debug("Created a comment with id: {}", recommendation.getMovieId());

            return recommendation;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteComments(int movieId) {
        try {
            String url = commentServiceUrl + "?movieId=" + movieId;
            LOG.debug("Will call the deleteComments API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<Rating> getRatings(int movieId) {
        try {
            String url = ratingServiceUrl + "?movieId=" + movieId;;

            LOG.debug("Will call getRatings API on URL: {}", url);
            List<Rating> ratings = restTemplate.exchange(url, GET, null,
                    new ParameterizedTypeReference<List<Rating>>() {}).getBody();

            LOG.debug("Found {} rating for a movie with id: {}", ratings.size(), movieId);
            return ratings;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting ratings, return zero ratings: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Rating createRating(Rating body) {
        try {
            String url = ratingServiceUrl;
            LOG.debug("Will post a new rating to URL: {}", url);

            Rating rating = restTemplate.postForObject(url, body, Rating.class);
            LOG.debug("Created a rating with id: {}", rating.getMovieId());

            return rating;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteRatings(int movieId) {
        try {
            String url = ratingServiceUrl + "?movieId=" + movieId;
            LOG.debug("Will call the deleteRatings API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<Screening> getScreenings(int movieId) {

        try {
            String url = screeningServiceUrl + "?movieId=" + movieId;

            List<Screening> screenings = restTemplate.exchange(url, GET, null,
                    new ParameterizedTypeReference<List<Screening>>() {}).getBody();
            return screenings;

        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    @Override
    public Screening createScreening(Screening body) {
        try {
            String url = screeningServiceUrl;
            LOG.debug("Will post a new screening to URL: {}", url);

            Screening screening = restTemplate.postForObject(url, body, Screening.class);
            LOG.debug("Created a screening with id: {}", screening.getMovieId());

            return screening;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteScreenings(int movieId) {
        try {
            String url = screeningServiceUrl + "?movieId=" + movieId;
            LOG.debug("Will call the deleteScreenings API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(ex));

            case UNPROCESSABLE_ENTITY :
                return new InvalidInputException(getErrorMessage(ex));

            default:
                LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                return ex;
        }
    }
}
