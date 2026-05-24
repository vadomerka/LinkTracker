package backend.academy.linktracker.bot.controllers;

import backend.academy.linktracker.bot.properties.KafkaReceiverProperties;
import backend.academy.linktracker.bot.services.BotChatManager;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class KafkaBotController {
    private final BotChatManager manager;
    private KafkaReceiverProperties kafkaReceiverProperties;

    public KafkaBotController(KafkaReceiverProperties kafkaReceiverProperties, BotChatManager manager) {
        this.kafkaReceiverProperties = kafkaReceiverProperties;
        this.manager = manager;
    }

    @KafkaListener(topics = "#{@kafkaReceiverProperties.topic}", groupId = "#{@kafkaReceiverProperties.group}")
    public void consumerGroupA(String message) {
        LinkUpdateRequest req = new ObjectMapper().readValue(message, LinkUpdateRequest.class);
        System.out.println("Получено сообщение - " + message);
        manager.processUpdate(req);
    }
}
