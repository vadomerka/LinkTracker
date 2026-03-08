package backend.academy.linktracker.models;

import java.util.List;

public record ListSourcesResponse(
    List<TrackedSource> links,
    Integer size
) {}
