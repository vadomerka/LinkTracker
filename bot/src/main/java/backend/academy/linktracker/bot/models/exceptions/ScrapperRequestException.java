package backend.academy.linktracker.bot.models.exceptions;

public class ScrapperRequestException extends RuntimeException {
    public ScrapperRequestException(String message) {
        super(message);
    }
}
