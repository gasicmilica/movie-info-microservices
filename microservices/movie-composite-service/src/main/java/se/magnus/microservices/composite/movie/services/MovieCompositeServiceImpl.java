package se.magnus.microservices.composite.movie.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import se.magnus.api.composite.movie.*;
import se.magnus.api.core.comment.Comment;
import se.magnus.api.core.movie.Movie;
import se.magnus.api.core.rating.Rating;
import se.magnus.api.core.screening.Screening;
import se.magnus.util.http.ServiceUtil;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MovieCompositeServiceImpl implements MovieCompositeService {
    private final ServiceUtil serviceUtil;
    private  MovieCompositeIntegration integration;
    private static final Logger LOG = LoggerFactory.getLogger(MovieCompositeServiceImpl.class);


    public MovieCompositeServiceImpl(ServiceUtil serviceUtil, MovieCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public Mono<MovieAggregate> getMovie(int movieId) {

        return Mono.zip(
                        values -> createMovieAggregate(
                                (Movie) values[0], serviceUtil.getServiceAddress(), (List<Comment>) values[1], (List<Screening>) values[2], (List<Rating>) values[3]
                        ),
                        integration.getMovie(movieId),
                        integration.getComments(movieId).collectList(),
                        integration.getScreenings(movieId).collectList(),
                        integration.getRatings(movieId).collectList())
                .doOnError(ex -> LOG.warn("getCompositeMovie failed: {}", ex.toString()))
                .log();
    }

    @Override
    public void createCompositeMovie(MovieAggregate body) {
        try {

            LOG.debug("createCompositeMovie: creates a new composite entity for movieId: {}", body.getMovieId());

            Movie movie = new Movie(body.getMovieId(), body.getTitle(), body.getDirector(), body.getReleaseYear(), body.getDuration(), body.getGenre(), null);
            integration.createMovie(movie);

            if (body.getRatings() != null) {
                body.getRatings().forEach(r -> {
                    Rating rating = new Rating(body.getMovieId(), r.getRatingId(), r.getAuthor(), r.getRatingDate(), r.getRatingNumber(), null);
                    integration.createRating(rating);
                });
            }

            if (body.getComments() != null) {
                body.getComments().forEach(r -> {
                    Comment comment = new Comment(body.getMovieId(), r.getCommentId(), r.getAuthor(), r.getCommentDate(), r.getCommentText(), null);
                    integration.createComment(comment);
                });
            }

            if (body.getScreenings() != null) {
                body.getScreenings().forEach(r -> {
                    Screening screening = new Screening(body.getMovieId(), r.getScreeningId(), r.getCinemaName(), r.getScreeningDate(), r.getPrice(), r.getLocation(),  null);
                    integration.createScreening(screening);
                });
            }

            LOG.debug("createCompositeMovie: composite entites created for movieId: {}", body.getMovieId());

        } catch (RuntimeException re) {
            LOG.warn("createCompositeProduct failed", re);
            throw re;
        }
    }

    @Override
    public void deleteCompositeMovie(int movieId) {
        LOG.debug("deleteCompositeMovie: Deletes a movie aggregate for movieId: {}", movieId);

        integration.deleteMovie(movieId);

        integration.deleteComments(movieId);

        integration.deleteRatings(movieId);

        integration.deleteScreenings(movieId);

        LOG.debug("getCompositeMovie: aggregate entities deleted for movieId: {}", movieId);
    }

    private MovieAggregate createMovieAggregate(Movie movie, String serviceAddress, List<Comment> comments, List<Screening> screenings, List<Rating> ratings) {

        int movieId = movie.getMovieId();
        String title = movie.getTitle();
        String director = movie.getDirector();
        int duration = movie.getDuration();
        String genre = movie.getGenre();
        int releaseYear = movie.getReleaseYear();

        List<CommentSummary> commentSummaries = (comments == null) ? null :
                comments.stream()
                        .map(r -> new CommentSummary(r.getCommentId(), r.getAuthor(), r.getCommentDate(), r.getCommentText()))
                        .collect(Collectors.toList());

        List<RatingSummary> ratingSummaries = (ratings == null) ? null :
                ratings.stream()
                        .map(r -> new RatingSummary(r.getRatingId(), r.getAuthor(), r.getRatingDate(), r.getRatingNumber()))
                        .collect(Collectors.toList());

        List<ScreeningSummary> screeningSummaries = (comments == null) ? null :
                screenings.stream()
                        .map(r -> new ScreeningSummary(r.getScreeningId(), r.getCinemaName(), r.getScreeningDate(), r.getPrice(), r.getLocation()))
                        .collect(Collectors.toList());


        String movieAddress = movie.getServiceAddress();
        String commentAddress = (comments != null && !comments.isEmpty()) ? comments.get(0).getServiceAddress() : "";
        String ratingAddress = (ratings != null && !ratings.isEmpty()) ? ratings.get(0).getServiceAddress() : "";
        String screeningAddress = (screenings != null && !screenings.isEmpty()) ? screenings.get(0).getServiceAddress() : "";

        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, movieAddress, commentAddress, ratingAddress, screeningAddress);

        return new MovieAggregate(
                movieId,
                title,
                director,
                releaseYear,
                duration,
                genre,
                serviceAddresses,
                commentSummaries,
                ratingSummaries,
                screeningSummaries
        );
    }
}
