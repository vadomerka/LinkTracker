package backend.academy.linktracker.bot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import backend.academy.linktracker.bot.models.commands.TrackCommand;
import backend.academy.linktracker.bot.services.ChatStatusManager;
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

@ExtendWith(MockitoExtension.class)
class TrackCommandTest {

    private static final long CHAT_ID = 42L;
    private static final String VALID_URL = "https://github.com/user/repo";

    @Mock
    TrackedRequestsSender requestsSender;

    @Mock
    TelegramBot bot;

    @Mock
    Chat chat;

    ChatStatusManager csm;
    TrackCommand trackCommand;

    @BeforeEach
    void setUp() {
        csm = new ChatStatusManager();
        trackCommand = new TrackCommand(requestsSender, csm);
        org.mockito.Mockito.when(chat.id()).thenReturn(CHAT_ID);
    }

    @Test
    void firstCall_startsDialogAndRequestsUrl() {
        var response = trackCommand.execute(bot, null, chat, List.of("/track"));
        assertThat(response).contains("Отправьте ссылку");
    }

    @Test
    void secondCall_withUrl_asksAboutTags() {
        trackCommand.execute(bot, null, chat, List.of("/track"));
        var response = trackCommand.execute(bot, null, chat, List.of(VALID_URL));
        assertThat(response).contains(VALID_URL).contains("Добавить теги");
    }

    @Test
    void thirdCall_refusingTags_savesLink() {
        trackCommand.execute(bot, null, chat, List.of("/track"));
        trackCommand.execute(bot, null, chat, List.of(VALID_URL));
        var response = trackCommand.execute(bot, null, chat, List.of("n"));

        assertThat(response).contains("добавлена");
        verify(requestsSender).addTrackingUrl(eq(CHAT_ID), eq(VALID_URL), anyList(), any());
    }

    @Test
    void fullFlow_withTags_savesLinkWithTags() {
        trackCommand.execute(bot, null, chat, List.of("/track"));
        trackCommand.execute(bot, null, chat, List.of(VALID_URL));
        trackCommand.execute(bot, null, chat, List.of("y"));
        trackCommand.execute(bot, null, chat, List.of("work", "hobby"));
        var response = trackCommand.execute(bot, null, chat, List.of("/send"));

        assertThat(response).contains("добавлена");
        verify(requestsSender).addTrackingUrl(eq(CHAT_ID), eq(VALID_URL), eq(List.of("work", "hobby")), any());
    }

    @Test
    void duplicateUrl_showsAlreadyTrackedMessage() {
        doThrow(new ScrapperRequestException("Ссылка уже отслеживается"))
                .when(requestsSender)
                .addTrackingUrl(anyLong(), eq(VALID_URL), anyList(), any());

        trackCommand.execute(bot, null, chat, List.of("/track"));
        trackCommand.execute(bot, null, chat, List.of(VALID_URL));
        var response = trackCommand.execute(bot, null, chat, List.of("n"));

        assertThat(response).contains("уже отслеживается");
    }

    @Test
    void invalidUrl_returnsErrorMessage() {
        var invalidUrl = "123://github.com/user/repo";
        doThrow(new ScrapperRequestException("Некорректная ссылка"))
                .when(requestsSender)
                .addTrackingUrl(anyLong(), eq(invalidUrl), anyList(), any());

        trackCommand.execute(bot, null, chat, List.of("/track"));
        trackCommand.execute(bot, null, chat, List.of(invalidUrl));
        var response = trackCommand.execute(bot, null, chat, List.of("n"));

        assertThat(response).isNotBlank();
    }

    @Test
    void afterCompleted_statusResetToDefault() {
        trackCommand.execute(bot, null, chat, List.of("/track"));
        trackCommand.execute(bot, null, chat, List.of(VALID_URL));
        trackCommand.execute(bot, null, chat, List.of("n"));

        assertThat(csm.isDefault(CHAT_ID)).isTrue();
    }
}
