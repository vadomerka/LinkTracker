package backend.academy.linktracker.models;

import java.util.Arrays;
import java.util.List;

public record ApiErrorResponse(
    String description,
    String code,
    String exceptionName,
    String exceptionMessage,
    List<String> stacktrace
) {

    public ApiErrorResponse(String description,
                            String code,
                            Exception ex) {
        this(
            description,
            code,
            ex.getClass().getName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .toList()
        );
    }
}
