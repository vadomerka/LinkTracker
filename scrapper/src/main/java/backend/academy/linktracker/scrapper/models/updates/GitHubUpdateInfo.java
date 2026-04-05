package backend.academy.linktracker.scrapper.models.updates;

import backend.academy.linktracker.models.http.external.GithubPRUpdateResponse;
import java.util.List;
import lombok.Getter;

@Getter
public class GitHubUpdateInfo implements UpdateInfo {
    private final String url;
    private final Integer size;
    private final List<GithubPRUpdateResponse> response;

    public GitHubUpdateInfo(String url, Integer size, List<GithubPRUpdateResponse> response) {
        this.url = url;
        this.size = size;
        this.response = response;
    }
}
