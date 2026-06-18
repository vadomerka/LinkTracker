package backend.academy.linktracker.bot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import backend.academy.linktracker.bot.models.commands.UntrackCommand;
import backend.academy.linktracker.bot.services.requests.TrackedRequestsSender;
import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UntrackCommandTest {

    private static final long CHAT_ID = 42L;
    private static final String VALID_URL = "https://github.com/user/repo";
    @Mock TrackedRequestsSender requestsSender;
    @Mock TelegramBot bot;
    @Mock Chat chat;
    UntrackCommand untrackCommand;

    @BeforeEach
    void setUp() {
        untrackCommand = new UntrackCommand(requestsSender);
        org.mockito.Mockito.when(chat.id()).thenReturn(CHAT_ID);
    }

    @Test
    void validUrl_removesLinkAndReturnsSuccess() {
        var response = untrackCommand.execute(bot, null, chat, List.of("/untrack", VALID_URL));

        verify(requestsSender).removeTrackingUrl(CHAT_ID, VALID_URL);
        assertThat(response).contains("успешно удалена");
    }

    @Test
    void nonExistingUrl_returnsErrorFromScrapper() {
        doThrow(new ScrapperRequestException("Ссылка не найдена"))
                .when(requestsSender).removeTrackingUrl(CHAT_ID, VALID_URL);

        var response = untrackCommand.execute(bot, null, chat, List.of("/untrack", VALID_URL));

        assertThat(response).contains("не найдена");
    }

    @Test
    void noUrlProvided_returnsErrorMessage() {
        var response = untrackCommand.execute(bot, null, chat, List.of("/untrack"));

        assertThat(response).isNotBlank();
    }
}
