package backend.academy.linktracker.models.http.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubUpdateResponse(
        @JsonProperty("updated_at") String updatedAt,
        String name,
        @JsonProperty("html_url") String htmlUrl) {}
