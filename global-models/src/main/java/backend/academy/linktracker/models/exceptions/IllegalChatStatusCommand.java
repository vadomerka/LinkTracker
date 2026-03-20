package backend.academy.linktracker.models.exceptions;

public class IllegalChatStatusCommand extends RuntimeException {
    public IllegalChatStatusCommand(String message) {
        super(message);
    }
}
