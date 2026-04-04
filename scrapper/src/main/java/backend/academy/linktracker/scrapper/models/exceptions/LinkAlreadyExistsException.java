package backend.academy.linktracker.scrapper.models.exceptions;

public class LinkAlreadyExistsException extends RuntimeException {
    public LinkAlreadyExistsException() {
        super("Ссылка уже добавлена в базу.");
    }
}
