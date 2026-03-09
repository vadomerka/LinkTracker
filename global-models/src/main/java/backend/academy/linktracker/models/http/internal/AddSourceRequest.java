package backend.academy.linktracker.models.http.internal;

import java.util.List;

public record AddSourceRequest(String url, List<String> tags, List<String> filters) {}
