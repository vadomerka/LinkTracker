package backend.academy.linktracker.scrapper.models.exceptions;

public class UrlIsAlreadyTrackedException extends RuntimeException {
    public UrlIsAlreadyTrackedException(String message) {
        super(message);
    }
}
