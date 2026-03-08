package backend.academy.linktracker.models.exceptions;

public class ScrapperRequestException extends RuntimeException {
    public ScrapperRequestException(String message) {
        super(message);
    }
}
