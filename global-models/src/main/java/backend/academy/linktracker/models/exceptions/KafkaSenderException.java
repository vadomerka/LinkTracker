package backend.academy.linktracker.models.exceptions;

public class KafkaSenderException extends RuntimeException {
    public KafkaSenderException(String message, Throwable cause) {
        super(message, cause);
    }
}
