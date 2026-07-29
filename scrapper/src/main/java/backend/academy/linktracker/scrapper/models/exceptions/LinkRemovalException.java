package backend.academy.linktracker.scrapper.models.exceptions;

public class LinkRemovalException extends RuntimeException {
    public LinkRemovalException() {
        super("Ошибка при удалении ссылки.");
    }
}
