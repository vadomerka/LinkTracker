package backend.academy.linktracker.scrapper.models.updates;

import backend.academy.linktracker.models.http.external.GithubUpdateResponse;
import java.util.List;

public record GitHubUpdateData(List<GithubUpdateResponse> pullRequest, List<GithubUpdateResponse> issue)
        implements UpdateInfo {}
