package se.magnus.microservices.core.rating.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import se.magnus.api.core.rating.Rating;
import se.magnus.api.core.rating.RatingService;
import se.magnus.api.event.Event;
import se.magnus.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final RatingService ratingService;

    @Autowired
    public MessageProcessor(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Rating> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

            case CREATE:
                Rating rating = event.getData();
                LOG.info("Create rating with ID: {}/{}", rating.getMovieId(), rating.getRatingId());
                ratingService.createRating(rating);
                break;

            case DELETE:
                int movieId = event.getKey();
                LOG.info("Delete ratings with movieId: {}", movieId);
                ratingService.deleteRatings(movieId);
                break;

            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                LOG.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
