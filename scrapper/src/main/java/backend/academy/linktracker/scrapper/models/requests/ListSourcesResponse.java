package backend.academy.linktracker.scrapper.models.requests;

import backend.academy.linktracker.scrapper.models.TrackedSource;
import java.util.List;

public record ListSourcesResponse(
    List<TrackedSource> links,
    Integer size
) {}
