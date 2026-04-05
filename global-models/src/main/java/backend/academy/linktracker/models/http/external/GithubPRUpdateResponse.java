package backend.academy.linktracker.models.http.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubPRUpdateResponse(
        Long id,
        Integer number,
        @JsonProperty("updated_at") String updatedAt,
        @JsonProperty("html_url") String htmlUrl) {}
