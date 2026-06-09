package backend.academy.linktracker.scrapper.ratelimit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.linktracker.scrapper.configuration.WebConfig;
import backend.academy.linktracker.scrapper.controllers.rest.SourceController;
import backend.academy.linktracker.scrapper.controllers.rest.SourceControllerAdvice;
import backend.academy.linktracker.scrapper.properties.RateLimitProperties;
import backend.academy.linktracker.scrapper.services.cache.CachedListSourcesService;
import backend.academy.linktracker.scrapper.services.updates.LinkUpdateService;
import backend.academy.linktracker.scrapper.controllers.db.DataController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {SourceController.class, SourceControllerAdvice.class})
@Import({WebConfig.class, RateLimitInterceptor.class})
@EnableConfigurationProperties(RateLimitProperties.class)
@TestPropertySource(properties = "app.rate-limit.capacity=3")
class RateLimitIntegrationTest {

    @MockitoBean
    DataController dataController;

    @MockitoBean
    CachedListSourcesService cachedListSourcesService;

    @MockitoBean
    LinkUpdateService linkUpdateService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void returns429AfterCapacityExceeded() throws Exception {
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/tg-chat/1"))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(post("/tg-chat/1"))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void differentIps_haveIndependentBuckets() throws Exception {
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/tg-chat/1")
                            .header("X-Forwarded-For", "10.0.0.1"))
                    .andExpect(status().isOk());
        }
        mockMvc.perform(post("/tg-chat/1")
                        .header("X-Forwarded-For", "10.0.0.1"))
                .andExpect(status().isTooManyRequests());

        mockMvc.perform(post("/tg-chat/1")
                        .header("X-Forwarded-For", "10.0.0.2"))
                .andExpect(status().isOk());
    }
}
