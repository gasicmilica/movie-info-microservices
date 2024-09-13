package se.magnus.microservices.composite.movie;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.composite.movie.MovieAggregate;
import se.magnus.api.core.movie.Movie;
import se.magnus.api.event.Event;
import se.magnus.microservices.composite.movie.services.MovieCompositeIntegration;

import static org.hamcrest.Matchers.is;
import static se.magnus.microservices.composite.movie.IsSameEvent.sameEventExceptCreatedAt;

import java.util.concurrent.BlockingQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;
import static org.springframework.http.HttpStatus.OK;
import static reactor.core.publisher.Mono.just;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

@RunWith(SpringRunner.class)
@SpringBootTest(
	webEnvironment=RANDOM_PORT,
	classes = {MovieCompositeServiceApplication.class, TestSecurityConfig.class },
	properties = {"spring.main.allow-bean-definition-overriding=true","eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
public class MessagingTests {

    @Autowired
    private WebTestClient client;

	@Autowired
	private MovieCompositeIntegration.MessageSources channels;

	@Autowired
	private MessageCollector collector;

	BlockingQueue<Message<?>> queueComments = null;
	BlockingQueue<Message<?>> queueMovies = null;
	BlockingQueue<Message<?>> queueRatings = null;
	BlockingQueue<Message<?>> queueScreenings = null;

	@Before
	public void setUp() {
		queueComments = getQueue(channels.outputComments());
		queueMovies = getQueue(channels.outputMovies());
		queueRatings = getQueue(channels.outputRatings());
		queueScreenings = getQueue(channels.outputScreenings());
	}

	@Test
	public void createCompositeMovie1() {
		MovieAggregate composite = new MovieAggregate(1, "t", "d", 2000, 2, "g", null, null, null, null);
		postAndVerifyMovie(composite, OK);

		// Assert one expected new movie events queued up
		assertEquals(1, queueMovies.size());

		Event<Integer, Movie> expectedEvent = new Event(CREATE, composite.getMovieId(),
				new Movie(composite.getMovieId(), composite.getTitle(), composite.getDirector(), composite.getReleaseYear(), composite.getDuration(), composite.getGenre(), null));
		assertThat(queueMovies, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));

		// Assert none comments, ratings and screenings events
		assertEquals(0, queueComments.size());
		assertEquals(0, queueRatings.size());
		assertEquals(0, queueScreenings.size());
	}

	@Test
	public void deleteCompositeMovie() {
		deleteAndVerifyMovie(1, OK);

		// Assert one delete movie event queued up
		assertEquals(1, queueMovies.size());

		Event<Integer, Movie> expectedEvent = new Event(DELETE, 1, null);
		assertThat(queueMovies, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));

		// Assert one delete comment event queued up
		assertEquals(1, queueComments.size());

		Event<Integer, Movie> expectedCommentEvent = new Event(DELETE, 1, null);
		assertThat(queueComments, receivesPayloadThat(sameEventExceptCreatedAt(expectedCommentEvent)));

		// Assert one delete rating event queued up
		assertEquals(1, queueRatings.size());

		Event<Integer, Movie> expectedRatingEvent = new Event(DELETE, 1, null);
		assertThat(queueRatings, receivesPayloadThat(sameEventExceptCreatedAt(expectedRatingEvent)));

		// Assert one delete screening event queued up
		assertEquals(1, queueScreenings.size());

		Event<Integer, Movie> expectedScreeningEvent = new Event(DELETE, 1, null);
		assertThat(queueScreenings, receivesPayloadThat(sameEventExceptCreatedAt(expectedScreeningEvent)));
	}

	private BlockingQueue<Message<?>> getQueue(MessageChannel messageChannel) {
		return collector.forChannel(messageChannel);
	}

	private void postAndVerifyMovie(MovieAggregate compositeMovie, HttpStatus expectedStatus) {
		client.post()
			.uri("/movie-composite")
			.body(just(compositeMovie), MovieAggregate.class)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus);
	}

	private void deleteAndVerifyMovie(int movieId, HttpStatus expectedStatus) {
		client.delete()
			.uri("/movie-composite/" + movieId)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus);
	}
}
