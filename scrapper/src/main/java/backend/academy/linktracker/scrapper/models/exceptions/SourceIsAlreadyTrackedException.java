package backend.academy.linktracker.scrapper.models.exceptions;

public class SourceIsAlreadyTrackedException extends RuntimeException {
    public SourceIsAlreadyTrackedException(String message) {
        super(message);
    }
}
