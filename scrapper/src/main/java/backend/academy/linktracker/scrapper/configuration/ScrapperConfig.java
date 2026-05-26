package backend.academy.linktracker.scrapper.configuration;

import backend.academy.linktracker.scrapper.properties.RequestProperties;
import backend.academy.linktracker.scrapper.services.senders.BotRequestsSender;
import backend.academy.linktracker.scrapper.services.senders.HttpBotRequestsSender;
import backend.academy.linktracker.scrapper.services.senders.KafkaBotRequestsSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableScheduling
@EnableTransactionManagement
public class ScrapperConfig {
    @Bean
    public BotRequestsSender botRequestSender(
            RequestProperties reqProperties,
            KafkaBotRequestsSender kafkaBotRequestsSender,
            HttpBotRequestsSender httpBotRequestsSender) {
        return switch (reqProperties.getType().toLowerCase()) {
            case "kafka" -> kafkaBotRequestsSender;
            case "http" -> httpBotRequestsSender;
            default -> throw new IllegalArgumentException("Неподдерживаемый параметр app.request.type");
        };
    }
}
