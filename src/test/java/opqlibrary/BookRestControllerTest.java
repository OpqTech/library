package opqlibrary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest(classes = OpqLibraryApplication.class)
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class BookRestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookService;

    @Test
    void getAllBooksReturnsBooks() throws Exception {
        Book book = new Book("Test", "Author", LocalDate.now());
        given(bookService.getAllBooks()).willReturn(List.of(book));
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test"));
    }

    @Test
    void addBookReturnsOk() throws Exception {
        String json = "{\"title\":\"Test\",\"author\":\"Author\",\"publishedOn\":\"2023-01-01\"}";
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void addBookReturnsBadRequestOnCapacityReached() throws Exception {
        String json = "{\"title\":\"Test\",\"author\":\"Author\",\"publishedOn\":\"2023-01-01\"}";
        willThrow(new BookShelfCapacityReached("Capacity reached")).given(bookService).addBook(any(Book.class));
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBookReturnsInternalServerErrorOnException() throws Exception {
        String json = "{\"title\":\"Test\",\"author\":\"Author\",\"publishedOn\":\"2023-01-01\"}";
        willThrow(new RuntimeException("Unexpected error")).given(bookService).addBook(any(Book.class));
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteBookReturnsOk() throws Exception {
        mockMvc.perform(delete("/api/books/0"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBookReturnsInternalServerErrorOnException() throws Exception {
        willThrow(new RuntimeException("Delete error")).given(bookService).removeBook(anyInt());
        mockMvc.perform(delete("/api/books/0"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void markAsReadReturnsOk() throws Exception {
        mockMvc.perform(post("/api/books/markRead/0"))
                .andExpect(status().isOk());
    }

    @Test
    void markAsReadReturnsInternalServerErrorOnException() throws Exception {
        willThrow(new RuntimeException("Read error")).given(bookService).markAsRead(anyInt());
        mockMvc.perform(post("/api/books/markRead/0"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void markInProgressReturnsOk() throws Exception {
        mockMvc.perform(post("/api/books/markProgress/0"))
                .andExpect(status().isOk());
    }

    @Test
    void markInProgressReturnsInternalServerErrorOnException() throws Exception {
        willThrow(new RuntimeException("Progress error")).given(bookService).markInProgress(anyInt());
        mockMvc.perform(post("/api/books/markProgress/0"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void searchBooksReturnsResults() throws Exception {
        Book book = new Book("Java", "Alice", LocalDate.now());
        given(bookService.searchBooks("java", null)).willReturn(List.of(book));
        mockMvc.perform(get("/api/books/search?title=java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java"));
    }
} 