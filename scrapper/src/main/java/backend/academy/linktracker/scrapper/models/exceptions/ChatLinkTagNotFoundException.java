package backend.academy.linktracker.scrapper.models.exceptions;

public class ChatLinkTagNotFoundException extends RuntimeException {
    public ChatLinkTagNotFoundException(String message) {
        super(message);
    }
}
