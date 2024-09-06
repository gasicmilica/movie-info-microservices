package se.magnus.microservices.composite.movie.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.magnus.api.core.comment.Comment;
import se.magnus.api.core.comment.CommentService;
import se.magnus.api.core.movie.Movie;
import se.magnus.api.core.movie.MovieService;
import se.magnus.api.core.rating.Rating;
import se.magnus.api.core.rating.RatingService;
import se.magnus.api.core.screening.Screening;
import se.magnus.api.core.screening.ScreeningService;
import se.magnus.api.event.Event;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.HttpErrorInfo;

import java.io.IOException;
import static reactor.core.publisher.Flux.empty;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

@EnableBinding(MovieCompositeIntegration.MessageSources.class)
@Component
public class MovieCompositeIntegration implements MovieService, CommentService, RatingService, ScreeningService {

    private static final Logger LOG = LoggerFactory.getLogger(MovieCompositeIntegration.class);

    private final String movieServiceUrl = "http://movie";
    private final String commentServiceUrl = "http://comment";
    private final String ratingServiceUrl  = "http://rating";
    private final String screeningServiceUrl = "http://screening";

    private final ObjectMapper mapper;

    private WebClient webClient;
    private final WebClient.Builder webClientBuilder;

    private MessageSources messageSources;

    public interface MessageSources {

        String OUTPUT_MOVIES = "output-movies";
        String OUTPUT_SCREENINGS = "output-screenings";
        String OUTPUT_RATINGS = "output-ratings";
        String OUTPUT_COMMENTS = "output-comments";

        @Output(OUTPUT_MOVIES)
        MessageChannel outputMovies();

        @Output(OUTPUT_SCREENINGS)
        MessageChannel outputScreenings();

        @Output(OUTPUT_RATINGS)
        MessageChannel outputRatings();

        @Output(OUTPUT_COMMENTS)
        MessageChannel outputComments();
    }

    @Autowired
    public MovieCompositeIntegration(
            ObjectMapper mapper,
            MessageSources messageSources,
            WebClient.Builder webClientBuilder
    ) {

        this.mapper = mapper;
        this.messageSources = messageSources;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<Movie> getMovie(int movieId) {
        String url = movieServiceUrl + "/movie/" + movieId;
        LOG.debug("Will call the getMovie API on URL: {}", url);

        return getWebClient().get().uri(url).retrieve().bodyToMono(Movie.class).log()
                .onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
    }

    @Override
    public Movie createMovie(Movie body) {
        messageSources.outputMovies().send(MessageBuilder.withPayload(new Event(CREATE, body.getMovieId(), body)).build());
        return body;
    }

    @Override
    public void deleteMovie(int movieId) {
        messageSources.outputMovies().send(MessageBuilder.withPayload(new Event(DELETE, movieId, null)).build());
    }

    @Override
    public Flux<Comment> getComments(int movieId) {
        String url = commentServiceUrl + "/comment?movieId=" + movieId;
        LOG.debug("Will call getComments API on URL: {}", url);

        // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
        return getWebClient().get().uri(url).retrieve().bodyToFlux(Comment.class).log().onErrorResume(error -> empty());
    }

    @Override
    public Comment createComment(Comment body) {
        messageSources.outputComments().send(MessageBuilder.withPayload(new Event(CREATE, body.getMovieId(), body)).build());
        return body;
    }

    @Override
    public void deleteComments(int movieId) {
        messageSources.outputComments().send(MessageBuilder.withPayload(new Event(DELETE, movieId, null)).build());
    }

    @Override
    public Flux<Rating> getRatings(int movieId) {
        String url = ratingServiceUrl + "/rating?movieId=" + movieId;;
        LOG.debug("Will call getRatings API on URL: {}", url);

        // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
        return getWebClient().get().uri(url).retrieve().bodyToFlux(Rating.class).log().onErrorResume(error -> empty());
    }

    @Override
    public Rating createRating(Rating body) {
        messageSources.outputRatings().send(MessageBuilder.withPayload(new Event(CREATE, body.getMovieId(), body)).build());
        return body;
    }

    @Override
    public void deleteRatings(int movieId) {
        messageSources.outputRatings().send(MessageBuilder.withPayload(new Event(DELETE, movieId, null)).build());
    }

    @Override
    public Flux<Screening> getScreenings(int movieId) {
        String url = screeningServiceUrl + "/screening?movieId=" + movieId;
        LOG.debug("Will call getScreenings API on URL: {}", url);

        // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
        return getWebClient().get().uri(url).retrieve().bodyToFlux(Screening.class).log().onErrorResume(error -> empty());
    }

    @Override
    public Screening createScreening(Screening body) {
        messageSources.outputScreenings().send(MessageBuilder.withPayload(new Event(CREATE, body.getMovieId(), body)).build());
        return body;
    }

    @Override
    public void deleteScreenings(int movieId) {
        messageSources.outputScreenings().send(MessageBuilder.withPayload(new Event(DELETE, movieId, null)).build());
    }

    private Mono<Health> getHealth(String url) {
        url += "/actuator/health";
        LOG.debug("Will call the Health API on URL: {}", url);
        return getWebClient().get().uri(url).retrieve().bodyToMono(String.class)
                .map(s -> new Health.Builder().up().build())
                .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
                .log();
    }

    public Mono<Health> getMovieHealth() {
        return getHealth(movieServiceUrl);
    }

    public Mono<Health> getCommentHealth() {
        return getHealth(commentServiceUrl);
    }

    public Mono<Health> getRatingHealth() {
        return getHealth(ratingServiceUrl);
    }

    public Mono<Health> getScreeningHealth() {
        return getHealth(screeningServiceUrl);
    }

    private WebClient getWebClient() {
        if (webClient == null) {
            webClient = webClientBuilder.build();
        }
        return webClient;
    }

    private Throwable handleException(Throwable ex) {

        if (!(ex instanceof WebClientResponseException)) {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }

        WebClientResponseException wcre = (WebClientResponseException)ex;

        switch (wcre.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(wcre));

            case UNPROCESSABLE_ENTITY :
                return new InvalidInputException(getErrorMessage(wcre));

            default:
                LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
                LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
                return ex;
        }
    }

    private String getErrorMessage(WebClientResponseException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }
}
