package backend.academy.linktracker.models.exceptions;

public class UnknownChatCommandStage extends RuntimeException {
    public UnknownChatCommandStage(String message) {
        super(message);
    }
}
