package backend.academy.linktracker.scrapper.models.exceptions;

public class ChatLinkNotFoundException extends RuntimeException {
    public ChatLinkNotFoundException(String message) {
        super(message);
    }
}
