package backend.academy.linktracker.scrapper;

import backend.academy.linktracker.models.http.internal.AddSourceRequest;
import backend.academy.linktracker.models.http.internal.ListSourcesResponse;
import backend.academy.linktracker.models.http.internal.RemoveSourceRequest;
import backend.academy.linktracker.scrapper.services.cache.ListSourcesCacheService;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import tools.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ScrapperApplication.class)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class ListSourcesCacheIntegrationTest {

    private static final AtomicLong CHAT_ID_SEQUENCE = new AtomicLong(1_000L);
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:18-alpine"));
    @Container
    static RedisContainer valkey = new RedisContainer(DockerImageName.parse("valkey/valkey:8.0-alpine"));
    private long chatId;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ListSourcesCacheService cacheService;
    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("app.valkey.standalone.enabled", () -> "true");
        registry.add("app.valkey.standalone.host", valkey::getHost);
        registry.add("app.valkey.standalone.port", () -> valkey.getMappedPort(6379));
    }

    @BeforeEach
    void setUp() throws Exception {
        chatId = CHAT_ID_SEQUENCE.incrementAndGet();
        cacheService.remove(chatId);
        mockMvc.perform(post("/tg-chat/{id}", chatId)).andExpect(status().isOk());
    }

    @Test
    void getList_cachesResponseByTgChatId() throws Exception {
        var addRequest = new AddSourceRequest("https://github.com/academy/test", List.of("work"), List.of());
        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", chatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        cacheService.remove(chatId);

        mockMvc.perform(get("/links").header("Tg-Chat-Id", chatId)).andExpect(status().isOk());

        String cachedJson = redisTemplate.opsForValue().get(cacheService.cacheKey(chatId));
        assertThat(cachedJson).isNotBlank();

        ListSourcesResponse cached = objectMapper.readValue(cachedJson, ListSourcesResponse.class);
        assertThat(cached.links()).hasSize(1);
        assertThat(cached.links().getFirst().url()).isEqualTo("https://github.com/academy/test");
    }

    @Test
    void addAndRemoveLink_invalidatesListCache() throws Exception {
        var url = "https://stackoverflow.com/questions/42";
        mockMvc.perform(get("/links").header("Tg-Chat-Id", chatId)).andExpect(status().isOk());
        assertThat(redisTemplate.hasKey(cacheService.cacheKey(chatId))).isTrue();

        var addRequest = new AddSourceRequest(url, List.of(), List.of());
        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", chatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());
        assertThat(redisTemplate.hasKey(cacheService.cacheKey(chatId))).isFalse();

        mockMvc.perform(get("/links").header("Tg-Chat-Id", chatId)).andExpect(status().isOk());
        assertThat(redisTemplate.hasKey(cacheService.cacheKey(chatId))).isTrue();

        var removeRequest = new RemoveSourceRequest(url);
        mockMvc.perform(delete("/links")
                        .header("Tg-Chat-Id", chatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(removeRequest)))
                .andExpect(status().isOk());
        assertThat(redisTemplate.hasKey(cacheService.cacheKey(chatId))).isFalse();
    }
}
