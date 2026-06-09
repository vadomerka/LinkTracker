package backend.academy.linktracker.scrapper.services.cache;

import backend.academy.linktracker.models.http.internal.ListSourcesResponse;
import backend.academy.linktracker.scrapper.properties.ValkeyCacheProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import java.util.Optional;

@Service
@ConditionalOnProperty(prefix = "app.valkey", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ListSourcesCacheService {
    private static final Logger log = LoggerFactory.getLogger(ListSourcesCacheService.class);

    private final StringRedisTemplate redisTemplate;
    private final ValkeyCacheProperties properties;
    private final ObjectMapper objectMapper;

    public ListSourcesCacheService(
            StringRedisTemplate redisTemplate, ValkeyCacheProperties properties, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public Optional<ListSourcesResponse> get(Long tgChatId) {
        String cached = redisTemplate.opsForValue().get(cacheKey(tgChatId));
        if (cached == null) {
            return Optional.empty();
        }
        log.info("Кешированный список ссылок Tg-Chat-Id={}", tgChatId);
        return Optional.of(objectMapper.readValue(cached, ListSourcesResponse.class));
    }

    public void put(Long tgChatId, ListSourcesResponse response) {
        redisTemplate
                .opsForValue()
                .set(
                        cacheKey(tgChatId),
                        objectMapper.writeValueAsString(response),
                        properties.getListCache().getTtl());
        log.info("Список ссылок сохранен Tg-Chat-Id={}", tgChatId);
    }

    public void remove(Long tgChatId) {
        Boolean deleted = redisTemplate.delete(cacheKey(tgChatId));
        log.info("Список ссылок удален for Tg-Chat-Id={}, deleted={}", tgChatId, deleted);
    }

    public String cacheKey(Long tgChatId) {
        return properties.getListCache().getKeyPrefix() + tgChatId;
    }
}
