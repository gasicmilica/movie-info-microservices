package se.magnus.microservices.composite.movie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import se.magnus.api.core.movie.Movie;
import se.magnus.api.event.Event;

import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static se.magnus.microservices.composite.movie.IsSameEvent.sameEventExceptCreatedAt;

public class IsSameEventTests {

	ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testEventObjectCompare() throws JsonProcessingException {

    	// Event #1 and #2 are the same event, but occurs as different times
		// Event #3 and #4 are different events
		Event<Integer, Movie> event1 = new Event<>(CREATE, 1, new Movie(1,"title", "Director", 2010, 2, "Genre", null));
		Event<Integer, Movie> event2 = new Event<>(CREATE, 1, new Movie(1,"title", "Director", 2010, 2, "Genre", null));
		Event<Integer, Movie> event3 = new Event<>(DELETE, 1, null);
		Event<Integer, Movie> event4 = new Event<>(CREATE, 1, new Movie(2,"title", "Director", 2010, 2, "Genre", null));

		String event1JSon = mapper.writeValueAsString(event1);

		assertThat(event1JSon, is(sameEventExceptCreatedAt(event2)));
		assertThat(event1JSon, not(sameEventExceptCreatedAt(event3)));
		assertThat(event1JSon, not(sameEventExceptCreatedAt(event4)));
    }
}
