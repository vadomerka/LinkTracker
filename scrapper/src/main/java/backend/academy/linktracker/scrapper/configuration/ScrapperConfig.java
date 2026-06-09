package backend.academy.linktracker.scrapper.configuration;

import backend.academy.linktracker.scrapper.properties.RequestProperties;
import backend.academy.linktracker.scrapper.services.senders.bot.BotRequestsSender;
import backend.academy.linktracker.scrapper.services.senders.bot.FallbackBotRequestsSender;
import backend.academy.linktracker.scrapper.services.senders.bot.KafkaBotRequestsSender;
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
            FallbackBotRequestsSender fallbackBotRequestsSender) {
        return switch (reqProperties.getType().toLowerCase()) {
            case "kafka" -> kafkaBotRequestsSender;
            case "http" -> fallbackBotRequestsSender;
            default -> throw new IllegalArgumentException("Неподдерживаемый параметр app.request.type");
        };
    }
}
