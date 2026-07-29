package backend.academy.linktracker.scrapper.models.exceptions;

public class ChatLinkNotFoundException extends RuntimeException {
    public ChatLinkNotFoundException() {
        super("Ссылка не привязана к данному чату.");
    }
}
