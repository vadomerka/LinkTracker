package backend.academy.linktracker.scrapper.models.exceptions;

public class LinkNotFoundException extends RuntimeException {
    public LinkNotFoundException(String message) {
        super(message);
    }
}
