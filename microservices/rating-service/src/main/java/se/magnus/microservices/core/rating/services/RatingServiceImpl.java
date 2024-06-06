package se.magnus.microservices.core.rating.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.comment.Comment;
import se.magnus.api.core.comment.CommentService;
import se.magnus.api.core.rating.Rating;
import se.magnus.api.core.rating.RatingService;
import se.magnus.util.http.ServiceUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class RatingServiceImpl implements RatingService {
    private final ServiceUtil serviceUtil;

    @Autowired
    public RatingServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Rating> getRatings(int movieId) {
        List<Rating> list = new ArrayList<>();
        list.add(new Rating(movieId, 1, "Author 1", new Date(), 1, serviceUtil.getServiceAddress()));
        list.add(new Rating(movieId, 2, "Author 2", new Date(), 2, serviceUtil.getServiceAddress()));
        list.add(new Rating(movieId, 3, "Author 3", new Date(), 10, serviceUtil.getServiceAddress()));

        return list;
    }
}
