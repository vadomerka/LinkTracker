package backend.academy.linktracker.bot;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.spring.EnableWireMock;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EnableWireMock
class BotControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void postValidUpdate_returns200() throws Exception {
        mockMvc.perform(post("/tg-chat/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"links": ["https://github.com/user/repo"]}
                            """))
                .andExpect(status().isOk());
    }

    @Test
    void postInvalidJson_returnsClientError() throws Exception {
        mockMvc.perform(post("/tg-chat/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().is4xxClientError());
    }
}
