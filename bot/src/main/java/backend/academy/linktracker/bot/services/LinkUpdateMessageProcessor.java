package backend.academy.linktracker.bot.services;

import backend.academy.linktracker.models.exceptions.MessageValidationException;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class LinkUpdateMessageProcessor {

    private final BotChatManager manager;
    private final ObjectMapper objectMapper;

    public LinkUpdateMessageProcessor(BotChatManager manager) {
        this.manager = manager;
        this.objectMapper = new ObjectMapper();
    }

    public void process(String message) {
        LinkUpdateRequest request = deserialize(message);
        validate(request);
        manager.processUpdate(request);
    }

    private LinkUpdateRequest deserialize(String message) {
        try {
            return objectMapper.readValue(message, LinkUpdateRequest.class);
        } catch (Exception e) {
            throw new MessageValidationException("Не удалось десериализовать сообщение Kafka", e);
        }
    }

    private void validate(LinkUpdateRequest request) {
        if (request == null) {
            throw new MessageValidationException("Тело сообщения не может быть пустым");
        }
        if (request.chatId() == null) {
            throw new MessageValidationException("Поле chatId обязательно");
        }
        if (request.links() == null || request.links().isEmpty()) {
            throw new MessageValidationException("Список links не может быть пустым");
        }
        for (var link : request.links()) {
            if (link == null || link.url() == null || link.url().isBlank()) {
                throw new MessageValidationException("Каждая ссылка должна содержать непустой url");
            }
        }
    }
}
