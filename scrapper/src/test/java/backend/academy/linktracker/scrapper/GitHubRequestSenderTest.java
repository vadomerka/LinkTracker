package backend.academy.linktracker.scrapper;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import backend.academy.linktracker.scrapper.properties.GithubProperties;
import backend.academy.linktracker.scrapper.services.requests.GitHubRequestSender;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

@SpringBootTest
@ActiveProfiles("test")
@EnableWireMock
class GitHubRequestSenderTest {

    @InjectWireMock
    WireMockServer wireMock;

    GitHubRequestSender sender;

    @BeforeEach
    void setUp() {
        var props = new GithubProperties();
        props.setToken("test-token");
        sender = new GitHubRequestSender(props);
    }

    @Test
    void validResponse_returnsInstant() {
        stubFor(get(urlEqualTo("/repos/user/repo"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                  "updated_at": "2024-01-15T10:30:00Z",
                                  "name": "repo",
                                  "html_url": "https://github.com/user/repo"
                                }
                                """)));

        var url = wireMock.baseUrl() + "/repos/user/repo";
        var result = sender.getResponse(url);

        assertThat(result).isNotNull();
        assertThat(result.toString()).isEqualTo("2024-01-15T10:30:00Z");
    }

    @Test
    void errorStatusResponse_throwsException() {
        stubFor(get(urlEqualTo("/repos/user/notfound"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"message\": \"Not Found\"}")));

        var url = wireMock.baseUrl() + "/repos/user/notfound";

        assertThatCode(() -> sender.getResponse(url)).isInstanceOf(Exception.class);
    }

    @Test
    void serverError_doesNotCrashApplication() {
        stubFor(get(urlEqualTo("/repos/user/error"))
                .willReturn(aResponse().withStatus(500).withBody("Internal Server Error")));

        var url = wireMock.baseUrl() + "/repos/user/error";

        assertThatCode(() -> sender.getResponse(url)).isInstanceOf(Exception.class);
    }

    @Test
    void malformedBody_doesNotCrashApplication() {
        stubFor(get(urlEqualTo("/repos/user/broken"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("not-a-json-at-all")));

        var url = wireMock.baseUrl() + "/repos/user/broken";

        assertThatCode(() -> sender.getResponse(url)).isInstanceOf(Exception.class);
    }
}
