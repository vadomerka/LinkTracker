package backend.academy.linktracker.models.exceptions;

public class UrlFormatException extends RuntimeException {
    public UrlFormatException(String message) {
        super(message);
    }
}
