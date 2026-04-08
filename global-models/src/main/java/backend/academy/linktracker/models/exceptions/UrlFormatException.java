package backend.academy.linktracker.models.exceptions;

public class UrlFormatException extends RuntimeException {
    public UrlFormatException() {
        super("Ссылка не соответсвует формату.");
    }

    public UrlFormatException(String message) {
        super(message);
    }
}
