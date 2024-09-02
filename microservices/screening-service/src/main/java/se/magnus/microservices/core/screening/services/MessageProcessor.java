package se.magnus.microservices.core.screening.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import se.magnus.api.core.screening.Screening;
import se.magnus.api.core.screening.ScreeningService;
import se.magnus.api.event.Event;
import se.magnus.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final ScreeningService screeningService;

    @Autowired
    public MessageProcessor(ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Screening> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

            case CREATE:
                Screening screening = event.getData();
                LOG.info("Create screening with ID: {}/{}", screening.getMovieId(), screening.getScreeningId());
                screeningService.createScreening(screening);
                break;

            case DELETE:
                int movieId = event.getKey();
                LOG.info("Delete screenings with movieId: {}", movieId);
                screeningService.deleteScreenings(movieId);
                break;

            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                LOG.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
