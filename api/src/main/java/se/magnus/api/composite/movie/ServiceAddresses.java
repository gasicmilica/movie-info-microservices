package se.magnus.api.composite.movie;

public class ServiceAddresses {
    private final String compositeAddress;
    private final String movieAddress;
    private final String commentAddress;
    private final String ratingAddress;
    private final String screeningAddress;

    public ServiceAddresses() {
        this.compositeAddress = null;
        this.movieAddress = null;
        this.commentAddress = null;
        this.ratingAddress = null;
        this.screeningAddress = null;
    }

    public ServiceAddresses(String compositeAddressString, String movieAddress, String commentAddress, String ratingAddress, String screeningAddress) {
        this.compositeAddress = compositeAddressString;
        this.movieAddress = movieAddress;
        this.commentAddress = commentAddress;
        this.ratingAddress = ratingAddress;
        this.screeningAddress = screeningAddress;
    }

    public String getMovieAddress() {
        return movieAddress;
    }

    public String getCompositeAddress() {
        return compositeAddress;
    }

    public String getCommentAddress() {
        return commentAddress;
    }

    public String getRatingAddress() {
        return ratingAddress;
    }

    public String getScreeningAddress() {
        return screeningAddress;
    }
}
