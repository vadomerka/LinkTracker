package backend.academy.linktracker.scrapper.services.senders.bot;

import backend.academy.linktracker.models.exceptions.KafkaSenderException;
import backend.academy.linktracker.models.http.external.UpdateResponse;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequestItem;
import backend.academy.linktracker.models.kafka.RawUpdateMessage;
import backend.academy.linktracker.scrapper.properties.KafkaSenderProperties;
import backend.academy.linktracker.scrapper.services.senders.KafkaSenderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KafkaBotRequestsSender implements BotRequestsSender {
    private static final Logger log = LoggerFactory.getLogger(KafkaBotRequestsSender.class);

    private final KafkaSenderProperties properties;
    private final KafkaSenderService kafkaSenderService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KafkaBotRequestsSender(KafkaSenderProperties properties, KafkaSenderService kafkaSenderService) {
        this.properties = properties;
        this.kafkaSenderService = kafkaSenderService;
    }

    public void sendUpdates(LinkUpdateRequest request) {
        String topic = properties.getTopic();
        String key = properties.getGroup();
        for (LinkUpdateRequestItem item : request.links()) {
            RawUpdateMessage raw = toRawMessage(item, request.chatId());
            String message;
            try {
                message = objectMapper.writeValueAsString(raw);
            } catch (Exception e) {
                throw new KafkaSenderException("Ошибка сериализации RawUpdateMessage", e);
            }
            try {
                kafkaSenderService.sendMessage(topic, key, message);
            } catch (Exception e) {
                throw new KafkaSenderException(
                        String.format("Ошибка при отправке сообщения в kafka topic=%s", topic), e);
            }
        }
    }

    private RawUpdateMessage toRawMessage(LinkUpdateRequestItem item, long chatId) {
        List<UpdateResponse> responses =
                item.data() != null && item.data().data() != null ? item.data().data() : List.of();

        String author = responses.stream()
                .map(UpdateResponse::userName)
                .filter(u -> u != null && !u.isBlank())
                .findFirst()
                .orElse("");

        String description = responses.stream()
                .map(ur -> "[" + ur.type() + "] " + ur.title() + ": " + ur.description())
                .collect(Collectors.joining("\n"));

        long id = (long) item.url().hashCode();

        return new RawUpdateMessage(id, description, author, List.of(chatId));
    }
}
