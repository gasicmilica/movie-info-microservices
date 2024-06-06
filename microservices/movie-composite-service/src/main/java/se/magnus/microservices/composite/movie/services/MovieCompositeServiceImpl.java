package se.magnus.microservices.composite.movie.services;

import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.composite.movie.*;
import se.magnus.api.core.comment.Comment;
import se.magnus.api.core.movie.Movie;
import se.magnus.api.core.rating.Rating;
import se.magnus.api.core.screening.Screening;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.ServiceUtil;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MovieCompositeServiceImpl implements MovieCompositeService {
    private final ServiceUtil serviceUtil;
    private  MovieCompositeIntegration integration;

    public MovieCompositeServiceImpl(ServiceUtil serviceUtil, MovieCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public MovieAggregate getMovie(int movieId) {
        Movie movie = integration.getMovie(movieId);
        if (movie == null) throw new NotFoundException("No movie found for movie id: " + movieId);

        List<Comment> comments = integration.getComments(movieId);
        List<Screening> screenings = integration.getScreenings(movieId);
        List<Rating> ratings = integration.getRatings(movieId);

        return createMovieAggregate(movie, serviceUtil.getServiceAddress(), comments, screenings, ratings);
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
