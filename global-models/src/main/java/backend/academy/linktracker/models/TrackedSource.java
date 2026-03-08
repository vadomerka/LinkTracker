package backend.academy.linktracker.models;

import java.util.List;

public record TrackedSource(Integer id, String url, List<String> tags, List<String> filters) {}
