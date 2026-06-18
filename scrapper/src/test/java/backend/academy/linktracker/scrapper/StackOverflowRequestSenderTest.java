package backend.academy.linktracker.scrapper;

import backend.academy.linktracker.scrapper.properties.GithubProperties;
import backend.academy.linktracker.scrapper.services.requests.StackOverflowRequestSender;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ActiveProfiles("test")
@EnableWireMock
class StackOverflowRequestSenderTest {

    @InjectWireMock
    WireMockServer wireMock;

    StackOverflowRequestSender sender;

    @BeforeEach
    void setUp() {
        var props = new GithubProperties();
        props.setToken("test-token");
        sender = new StackOverflowRequestSender(props);
    }

    @Test
    void validResponse_returnsInstant() {
        stubFor(get(urlEqualTo("/questions/12345"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                            {
                              "items": [
                                {
                                  "last_activity_date": 1705315800
                                }
                              ]
                            }
                            """)));

        var url = wireMock.baseUrl() + "/questions/12345";
        var result = sender.getResponse(url);

        assertThat(result).isNotNull();
        assertThat(result.getEpochSecond()).isEqualTo(1705315800L);
    }

    @Test
    void errorStatusResponse_throwsException() {
        stubFor(get(urlEqualTo("/questions/notfound"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                            {
                                "error_id": 400,
                                "error_message": "bad_parameter"
                            }
                            """)));

        var url = wireMock.baseUrl() + "/questions/notfound";

        assertThatCode(() -> sender.getResponse(url)).isInstanceOf(Exception.class);
    }

    @Test
    void emptyItemsList_throwsException() {
        stubFor(get(urlEqualTo("/questions/empty"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"items\": []}")));

        var url = wireMock.baseUrl() + "/questions/empty";

        assertThatCode(() -> sender.getResponse(url)).isInstanceOf(Exception.class);
    }

    @Test
    void malformedBody_doesNotCrashApplication() {
        stubFor(get(urlEqualTo("/questions/wrong"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{ wrong json }")));

        var url = wireMock.baseUrl() + "/questions/wrong";

        assertThatCode(() -> sender.getResponse(url)).isInstanceOf(Exception.class);
    }
}
