package backend.academy.linktracker.scrapper.models.exceptions;

public class ChatLinkTagNotFoundException extends RuntimeException {
    public ChatLinkTagNotFoundException() {
        super("У данной ссылки в чате нет данного тега");
    }

    public ChatLinkTagNotFoundException(String message) {
        super(message);
    }
}
