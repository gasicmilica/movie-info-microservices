package se.magnus.microservices.composite.movie.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import se.magnus.api.composite.movie.*;
import se.magnus.api.core.comment.Comment;
import se.magnus.api.core.movie.Movie;
import se.magnus.api.core.rating.Rating;
import se.magnus.api.core.screening.Screening;
import se.magnus.util.http.ServiceUtil;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MovieCompositeServiceImpl implements MovieCompositeService {
    private final ServiceUtil serviceUtil;
    private  MovieCompositeIntegration integration;
    private static final Logger LOG = LoggerFactory.getLogger(MovieCompositeServiceImpl.class);
    private final SecurityContext nullSC = new SecurityContextImpl();

    public MovieCompositeServiceImpl(ServiceUtil serviceUtil, MovieCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public Mono<MovieAggregate> getMovie(int movieId) {

        return Mono.zip(
                        values -> createMovieAggregate(
                                (SecurityContext) values[0], (Movie) values[1], serviceUtil.getServiceAddress(), (List<Comment>) values[2], (List<Screening>) values[3], (List<Rating>) values[4]
                        ),
                        ReactiveSecurityContextHolder.getContext().defaultIfEmpty(nullSC),
                        integration.getMovie(movieId),
                        integration.getComments(movieId).collectList(),
                        integration.getScreenings(movieId).collectList(),
                        integration.getRatings(movieId).collectList())
                .doOnError(ex -> LOG.warn("getCompositeMovie failed: {}", ex.toString()))
                .log();
    }

    private MovieAggregate createMovieAggregate(SecurityContext sc, Movie movie, String serviceAddress, List<Comment> comments, List<Screening> screenings, List<Rating> ratings) {

        logAuthorizationInfo(sc);

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

    @Override
    public Mono<Void> createCompositeMovie(MovieAggregate body) {
        return ReactiveSecurityContextHolder.getContext().doOnSuccess(sc -> internalCreateCompositeMovie(sc, body)).then();
    }

    public void internalCreateCompositeMovie(SecurityContext sc, MovieAggregate body) {
        try {
            logAuthorizationInfo(sc);

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
                    Screening screening = new Screening(body.getMovieId(), r.getScreeningId(), r.getCinemaName(), r.getScreeningDate(), r.getPrice(), r.getLocation(), null);
                    integration.createScreening(screening);
                });
            }

            LOG.debug("createCompositeMovie: composite entites created for movieId: {}", body.getMovieId());

        } catch (RuntimeException re) {
            LOG.warn("createCompositeMovie failed", re);
            throw re;
        }
    }

    @Override
    public Mono<Void> deleteCompositeMovie(int movieId) {
        return ReactiveSecurityContextHolder.getContext().doOnSuccess(sc -> internalDeleteCompositeMovie(sc, movieId)).then();
    }

    public void internalDeleteCompositeMovie(SecurityContext sc, int movieId) {
        logAuthorizationInfo(sc);

        LOG.debug("deleteCompositeMovie: Deletes a movie aggregate for movieId: {}", movieId);

        integration.deleteMovie(movieId);
        integration.deleteComments(movieId);
        integration.deleteRatings(movieId);
        integration.deleteScreenings(movieId);

        LOG.debug("getCompositeMovie: aggregate entities deleted for movieId: {}", movieId);
    }


    private void logAuthorizationInfo(SecurityContext sc) {
        if (sc != null && sc.getAuthentication() != null && sc.getAuthentication() instanceof JwtAuthenticationToken) {
            Jwt jwtToken = ((JwtAuthenticationToken)sc.getAuthentication()).getToken();
            logAuthorizationInfo(jwtToken);
        } else {
            LOG.warn("No JWT based Authentication supplied, running tests are we?");
        }
    }

    private void logAuthorizationInfo(Jwt jwt) {
        if (jwt == null) {
            LOG.warn("No JWT supplied, running tests are we?");
        } else {
            if (LOG.isDebugEnabled()) {
                URL issuer = jwt.getIssuer();
                List<String> audience = jwt.getAudience();
                Object subject = jwt.getClaims().get("sub");
                Object scopes = jwt.getClaims().get("scope");
                Object expires = jwt.getClaims().get("exp");

                LOG.debug("Authorization info: Subject: {}, scopes: {}, expires {}: issuer: {}, audience: {}", subject, scopes, expires, issuer, audience);
            }
        }
    }
}
