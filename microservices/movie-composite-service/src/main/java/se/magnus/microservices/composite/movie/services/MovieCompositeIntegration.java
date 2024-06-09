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
        this.movieServiceUrl = "http://" + movieServiceHost + ":" + movieServicePort + "/movie/";
        this.screeningServiceUrl = "http://" + screeningServiceHost + ":" + screeningServicePort + "/screening?movieId=";
        this.commentServiceUrl = "http://" + commentServiceHost + ":" + commentServicePort + "/comment?movieId=";
        this.ratingServiceUrl = "http://" + ratingServiceHost + ":" + ratingServicePort + "/rating?movieId=";
    }

    public Movie getMovie(int movieId) {

        try {
            String url = movieServiceUrl + movieId;
            LOG.debug("Will call getMovie API on URL: {}", url);

            Movie movie = restTemplate.getForObject(url, Movie.class);
            LOG.debug("Found a movie with id: {}", movie.getMovieId());

            return movie;

        } catch (HttpClientErrorException ex) {

            switch (ex.getStatusCode()) {

                case NOT_FOUND:
                    throw new NotFoundException(getErrorMessage(ex));

                case UNPROCESSABLE_ENTITY :
                    throw new InvalidInputException(getErrorMessage(ex));

                default:
                    LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                    LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                    throw ex;
            }
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

    public List<Comment> getComments(int movieId) {

        try {
            String url = commentServiceUrl + movieId;

            LOG.debug("Will call getRecommendations API on URL: {}", url);
            List<Comment> comments = restTemplate.exchange(url, GET, null,
                    new ParameterizedTypeReference<List<Comment>>() {}).getBody();

            LOG.debug("Found {} recommendations for a product with id: {}", comments.size(), movieId);
            return comments;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Rating> getRatings(int movieId) {

        try {
            String url = ratingServiceUrl + movieId;

            List<Rating> ratings = restTemplate.exchange(url, GET, null,
                    new ParameterizedTypeReference<List<Rating>>() {}).getBody();
            return ratings;

        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<Screening> getScreenings(int movieId) {

        try {
            String url = screeningServiceUrl + movieId;

            List<Screening> screenings = restTemplate.exchange(url, GET, null,
                    new ParameterizedTypeReference<List<Screening>>() {}).getBody();
            return screenings;

        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }
}
