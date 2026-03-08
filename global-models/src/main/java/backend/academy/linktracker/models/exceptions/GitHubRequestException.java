package backend.academy.linktracker.models.exceptions;

public class GitHubRequestException extends RuntimeException {
    public GitHubRequestException(String message) {
        super(message);
    }
}
