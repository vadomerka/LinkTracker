package backend.academy.linktracker.scrapper.models.exceptions;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException() {
        super("Тег не найден.");
    }
}
