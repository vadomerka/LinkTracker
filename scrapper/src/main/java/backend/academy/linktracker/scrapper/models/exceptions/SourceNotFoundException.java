package backend.academy.linktracker.scrapper.models.exceptions;

public class SourceNotFoundException extends RuntimeException {
    public SourceNotFoundException(String message) {
        super(message);
    }
}
