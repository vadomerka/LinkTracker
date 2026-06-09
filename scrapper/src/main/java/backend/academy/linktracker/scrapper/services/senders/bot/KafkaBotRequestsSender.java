package backend.academy.linktracker.scrapper.services.senders.bot;

import backend.academy.linktracker.models.exceptions.KafkaSenderException;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.properties.KafkaSenderProperties;
import backend.academy.linktracker.scrapper.services.senders.KafkaSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class KafkaBotRequestsSender implements BotRequestsSender {
    private static final Logger log = LoggerFactory.getLogger(KafkaBotRequestsSender.class);

    private final KafkaSenderProperties properties;
    private final KafkaSenderService kafkaSenderService;

    public KafkaBotRequestsSender(KafkaSenderProperties properties, KafkaSenderService kafkaSenderService) {
        this.properties = properties;
        this.kafkaSenderService = kafkaSenderService;
    }

    public void sendUpdates(LinkUpdateRequest request) {
        SendResult<String, String> result;
        String topic = properties.getTopic(); // String.valueOf(request.chatId());
        String key = properties.getGroup();
        String message = new ObjectMapper().writeValueAsString(request);
        try {
            result = kafkaSenderService.sendMessage(topic, key, message);
        } catch (Exception e) {
            throw new KafkaSenderException(String.format("Ошибка при отправке сообщения в kafka topic=%s", topic), e);
        }
    }
}
