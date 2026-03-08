package backend.academy.linktracker.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubResponse (
    @JsonProperty("updated_at") String updatedAt,
    String name,
    @JsonProperty("html_url") String htmlUrl
) {}
