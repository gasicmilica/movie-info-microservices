package se.magnus.microservices.core.movie.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import se.magnus.api.core.movie.Movie;
import se.magnus.api.core.movie.MovieService;
import se.magnus.api.event.Event;
import se.magnus.util.exceptions.EventProcessingException;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final MovieService movieService;

    @Autowired
    public MessageProcessor(MovieService movieService) {
        this.movieService = movieService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Movie> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

        case CREATE:
            Movie movie = event.getData();
            LOG.info("Create movie with ID: {}", movie.getMovieId());
            movieService.createMovie(movie);
            break;

        case DELETE:
            int movieId = event.getKey();
            LOG.info("Delete movie with movieId: {}", movieId);
            movieService.deleteMovie(movieId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
