// package backend.academy.linktracker.bot;
//
// import backend.academy.linktracker.bot.models.commands.ListCommand;
// import backend.academy.linktracker.bot.services.ChatStatusManager;
// import backend.academy.linktracker.bot.services.requests.TrackedRequestsSender;
// import backend.academy.linktracker.models.http.internal.ListSourcesResponse;
// import com.pengrad.telegrambot.TelegramBot;
// import com.pengrad.telegrambot.model.Chat;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.http.ResponseEntity;
// import java.util.List;
//
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.when;
//
// @ExtendWith(MockitoExtension.class)
// class ListCommandTest {
//
//    private static final long CHAT_ID = 42L;
//    private static final String VALID_URL = "https://github.com/user/repo";
//
//    @Mock
//    TrackedRequestsSender requestsSender;
//
//    @Mock
//    TelegramBot bot;
//
//    @Mock
//    Chat chat;
//
//    ChatStatusManager csm;
//    ListCommand listCommand;
//
//    @BeforeEach
//    void setUp() {
//        csm = new ChatStatusManager();
//        listCommand = new ListCommand(requestsSender, csm);
//        org.mockito.Mockito.when(chat.id()).thenReturn(CHAT_ID);
//    }
//
//    @Test
//    void firstCall_asksForTag() {
//        var response = listCommand.execute(bot, null, chat, List.of("/list"));
//        assertThat(response).contains("тег");
//    }
//
//    @Test
//    void withTag_filtersLinks() {
//        var source = new TrackedSource(1, VALID_URL, List.of("work"), List.of());
//        when(requestsSender.getTrackingUrls(eq(CHAT_ID), eq("work")))
//                .thenReturn(ResponseEntity.ok(new ListSourcesResponse(List.of(source), 1)));
//
//        listCommand.execute(bot, null, chat, List.of("/list"));
//        var response = listCommand.execute(bot, null, chat, List.of("work"));
//
//        assertThat(response).contains(VALID_URL);
//    }
//
//    @Test
//    void skipTag_returnsAllLinks() {
//        var source = new TrackedSource(1, VALID_URL, List.of(), List.of());
//        when(requestsSender.getTrackingUrls(eq(CHAT_ID), eq("")))
//                .thenReturn(ResponseEntity.ok(new ListSourcesResponse(List.of(source), 1)));
//
//        listCommand.execute(bot, null, chat, List.of("/list"));
//        var response = listCommand.execute(bot, null, chat, List.of("/next"));
//
//        assertThat(response).contains(VALID_URL);
//    }
//
//    @Test
//    void emptyList_returnsEmptyMessage() {
//        when(requestsSender.getTrackingUrls(eq(CHAT_ID), eq("")))
//                .thenReturn(ResponseEntity.ok(new ListSourcesResponse(List.of(), 0)));
//
//        listCommand.execute(bot, null, chat, List.of("/list"));
//        var response = listCommand.execute(bot, null, chat, List.of("/next"));
//
//        assertThat(response).contains("пуст");
//    }
//
//    @Test
//    void emptyListWithTag_returnsTagNotFoundMessage() {
//        when(requestsSender.getTrackingUrls(eq(CHAT_ID), eq("unknown")))
//                .thenReturn(ResponseEntity.ok(new ListSourcesResponse(List.of(), 0)));
//
//        listCommand.execute(bot, null, chat, List.of("/list"));
//        var response = listCommand.execute(bot, null, chat, List.of("unknown"));
//
//        assertThat(response).contains("не найдены");
//    }
// }
