package backend.academy.linktracker.bot.controllers;

import backend.academy.linktracker.bot.properties.KafkaReceiverProperties;
import backend.academy.linktracker.bot.services.BotChatManager;
import backend.academy.linktracker.models.kafka.ProcessedUpdateMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaBotController {
    private static final Logger log = LoggerFactory.getLogger(KafkaBotController.class);

    private final BotChatManager manager;
    private final KafkaReceiverProperties kafkaReceiverProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KafkaBotController(KafkaReceiverProperties kafkaReceiverProperties, BotChatManager manager) {
        this.kafkaReceiverProperties = kafkaReceiverProperties;
        this.manager = manager;
    }

    @KafkaListener(topics = "#{@kafkaReceiverProperties.topic}", groupId = "#{@kafkaReceiverProperties.group}")
    public void consume(String message) {
        ProcessedUpdateMessage msg;
        try {
            msg = objectMapper.readValue(message, ProcessedUpdateMessage.class);
        } catch (Exception e) {
            log.error("Не удалось десериализовать ProcessedUpdateMessage: {}", message, e);
            return;
        }
        log.info("Получено обновление id={}, chats={}", msg.id(), msg.tgChatIds());
        manager.processUpdate(msg);
    }
}
