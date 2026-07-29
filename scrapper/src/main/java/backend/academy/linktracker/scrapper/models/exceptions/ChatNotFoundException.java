package backend.academy.linktracker.scrapper.models.exceptions;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException() {
        super("Чат не найден.");
    }
}
