package backend.academy.linktracker.models.http.internal;

import backend.academy.linktracker.models.TrackedSource;
import java.util.List;

public record ListSourcesResponse(
    List<TrackedSource> links,
    Integer size
) {}
