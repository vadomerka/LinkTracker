package backend.academy.linktracker.scrapper.models.exceptions;

public class SourceIsAlreadyTrackedException extends RuntimeException {
    public SourceIsAlreadyTrackedException() {
        super("Ссылка уже добавлена.");
    }
}
