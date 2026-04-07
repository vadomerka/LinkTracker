package backend.academy.linktracker.scrapper.models.updates;

import backend.academy.linktracker.models.http.external.StackOverflowUpdateResponse;
import java.util.List;

public record StackOverflowUpdateData(
        List<StackOverflowUpdateResponse> answers, List<StackOverflowUpdateResponse> comments) implements UpdateInfo {}
