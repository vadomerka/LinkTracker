package backend.academy.linktracker.scrapper.models.exceptions;

public class ChatAlreadyExistsException extends RuntimeException {
    public ChatAlreadyExistsException() {
        super("Чат уже зарегестрирован.");
    }

    public ChatAlreadyExistsException(String message) {
        super(message);
    }
}
