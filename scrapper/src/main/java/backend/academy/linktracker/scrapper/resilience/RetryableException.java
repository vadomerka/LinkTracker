package backend.academy.linktracker.scrapper.resilience;

public class RetryableException extends RuntimeException {
    public RetryableException(String message) {
        super(message);
    }
}
