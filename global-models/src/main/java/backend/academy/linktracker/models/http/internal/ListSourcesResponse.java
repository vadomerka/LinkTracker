package backend.academy.linktracker.models.http.internal;

import backend.academy.linktracker.models.LinkDto;
import java.util.List;

public record ListSourcesResponse(List<LinkDto> links, Integer size) {}
