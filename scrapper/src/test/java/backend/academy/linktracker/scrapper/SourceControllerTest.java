package backend.academy.linktracker.scrapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SourceControllerTest {

    @Autowired MockMvc mockMvc;

    private static final String VALID_URL = "https://github.com/user/repo";
    private static final String ADD_LINK_BODY = """
            {
                "url": "https://github.com/user/repo",
                "tags": [],
                "filters": []
            }
            """;
    private static final String REMOVE_LINK_BODY = """
            {
                "url": "https://github.com/user/repo"
            }
            """;

    @Test
    void addChat_returns200() throws Exception {
        mockMvc.perform(post("/tg-chat/100"))
                .andExpect(status().isOk());
    }

    @Test
    void addLinkToExistingChat_returns200() throws Exception {
        mockMvc.perform(post("/tg-chat/101")).andExpect(status().isOk());

        mockMvc.perform(post("/links")
                        .header("tgChatId", 101)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADD_LINK_BODY))
                .andExpect(status().isOk());
    }

    @Test
    void getLinks_returnsAddedLink() throws Exception {
        mockMvc.perform(post("/tg-chat/102")).andExpect(status().isOk());
        mockMvc.perform(post("/links")
                        .header("tgChatId", 102)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADD_LINK_BODY))
                .andExpect(status().isOk());

        mockMvc.perform(get("/links")
                        .header("tgChatId", 102)
                        .header("tag", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links[0].url").value(VALID_URL));
    }

    @Test
    void deleteLink_returns200() throws Exception {
        mockMvc.perform(post("/tg-chat/103")).andExpect(status().isOk());
        mockMvc.perform(post("/links")
                        .header("tgChatId", 103)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADD_LINK_BODY))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/links")
                        .header("tgChatId", 103)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REMOVE_LINK_BODY))
                .andExpect(status().isOk());
    }

    @Test
    void getLinksAfterDelete_doesNotContainDeletedLink() throws Exception {
        mockMvc.perform(post("/tg-chat/104")).andExpect(status().isOk());
        mockMvc.perform(post("/links")
                        .header("tgChatId", 104)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADD_LINK_BODY))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/links")
                        .header("tgChatId", 104)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REMOVE_LINK_BODY))
                .andExpect(status().isOk());

        mockMvc.perform(get("/links")
                        .header("tgChatId", 104)
                        .header("tag", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(0));
    }

    @Test
    void deleteLink_nonExistingChat_returnsError() throws Exception {
        mockMvc.perform(post("/tg-chat/105")).andExpect(status().isOk());
        mockMvc.perform(post("/links")
                        .header("tgChatId", 105)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADD_LINK_BODY))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/links")
                        .header("tgChatId", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REMOVE_LINK_BODY))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getLinks_afterFailedDeleteFromNonExistingChat_stillContainsLink() throws Exception {
        mockMvc.perform(post("/tg-chat/106")).andExpect(status().isOk());
        mockMvc.perform(post("/links")
                        .header("tgChatId", 106)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADD_LINK_BODY))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/links")
                        .header("tgChatId", 998)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REMOVE_LINK_BODY))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(get("/links")
                        .header("tgChatId", 106)
                        .header("tag", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links[0].url").value(VALID_URL));
    }

    @Test
    void addLink_nonExistingChat_returnsError() throws Exception {
        mockMvc.perform(post("/tg-chat/107")).andExpect(status().isOk());

        mockMvc.perform(post("/links")
                        .header("tgChatId", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADD_LINK_BODY))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addLinkToDeletedChat_returnsError() throws Exception {
        mockMvc.perform(post("/tg-chat/108")).andExpect(status().isOk());
        mockMvc.perform(delete("/tg-chat/108")).andExpect(status().isOk());

        mockMvc.perform(post("/links")
                        .header("tgChatId", 108)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADD_LINK_BODY))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteNonExistingChat_returns404() throws Exception {
        mockMvc.perform(delete("/tg-chat/99999"))
                .andExpect(status().isNotFound());
    }
}
