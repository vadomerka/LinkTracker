package backend.academy.linktracker.bot.services.requests;

import backend.academy.linktracker.bot.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.models.ApiErrorResponse;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class RequestsUtils {
    public void onScrapperErrors(HttpRequest req, ClientHttpResponse res) throws IOException {
        String body = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
        ApiErrorResponse errorBody = new ObjectMapper().readValue(body, ApiErrorResponse.class);
        throw new ScrapperRequestException(errorBody.description());
    }
}
