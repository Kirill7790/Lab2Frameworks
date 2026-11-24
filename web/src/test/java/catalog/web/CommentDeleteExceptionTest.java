package catalog.web;

import catalog.core.exception.CommentTooOldException;
import catalog.core.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user", roles = {"USER"})
class CommentDeleteExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Test
    void deleteTooOldComment_returns400WithJsonError() throws Exception {
        doThrow(new CommentTooOldException(
                "Коментар створено більше ніж 24 години тому і не може бути видалений"))
                .when(commentService).delete(anyLong(), anyLong(), any(Instant.class));

        mockMvc.perform(post("/comments/delete")
                        .with(csrf())
                        .param("bookId", "1")
                        .param("commentId", "2")
                        .param("createdAt", "2024-01-01T00:00:00Z"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Коментар створено більше ніж 24 години тому і не може бути видалений"));
    }
}