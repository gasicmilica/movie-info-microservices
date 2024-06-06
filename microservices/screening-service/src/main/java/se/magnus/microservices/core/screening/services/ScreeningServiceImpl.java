package se.magnus.microservices.core.screening.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.rating.Rating;
import se.magnus.api.core.rating.RatingService;
import se.magnus.api.core.screening.Screening;
import se.magnus.api.core.screening.ScreeningService;
import se.magnus.util.http.ServiceUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class ScreeningServiceImpl implements ScreeningService {
    private final ServiceUtil serviceUtil;

    @Autowired
    public ScreeningServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Screening> getScreenings(int movieId) {
        List<Screening> list = new ArrayList<>();
        list.add(new Screening(movieId, 1, "Cinema 1", new Date(), 1, "Vuka Karadzica 10, Novi Sad", serviceUtil.getServiceAddress()));
        list.add(new Screening(movieId, 2, "Cinema 2", new Date(), 2, "Omladinska 24, Indjija", serviceUtil.getServiceAddress()));
        list.add(new Screening(movieId, 3, "Cinema 3", new Date(), 10, "Vojvodjanska 2, Novi Sad", serviceUtil.getServiceAddress()));

        return list;
    }
}
