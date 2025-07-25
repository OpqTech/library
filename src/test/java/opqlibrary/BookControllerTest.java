package opqlibrary;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(BookController.class)
@WithMockUser(roles = "ADMIN")
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookService;

    @Test
    @DisplayName("GET / should return book list")
    void listBooksReturnsBooks() throws Exception {
        Book book = new Book("Test", "Author", LocalDate.now());
        given(bookService.getAllBooks()).willReturn(List.of(book));
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST / should add a book")
    void addBookAddsBook() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                .param("title", "Test")
                .param("author", "Author")
                .param("publishedOn", "2023-01-01")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("POST /markRead/{index} should mark as read")
    void markAsReadWorks() throws Exception {
        Book book = new Book("Test", "Author", LocalDate.now());
        given(bookService.getBookByIndex(0)).willReturn(book);
        mockMvc.perform(MockMvcRequestBuilders.post("/markRead/0").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("POST /markProgress/{index} should mark in progress")
    void markInProgressWorks() throws Exception {
        Book book = new Book("Test", "Author", LocalDate.now());
        given(bookService.getBookByIndex(0)).willReturn(book);
        mockMvc.perform(MockMvcRequestBuilders.post("/markProgress/0").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("POST /delete/{index} should delete book")
    void deleteBookWorks() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/delete/0").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("POST /borrow should borrow book")
    void borrowBookWorks() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/borrow")
                .param("index", "0")
                .param("borrower", "User")
                .param("borrowedDate", "2023-01-01")
                .param("dueDate", "2023-01-08")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /borrow with invalid index should show error")
    void borrowBookInvalidIndexShowsError() throws Exception {
        willThrow(new IllegalArgumentException("Invalid book index for borrowing.")).given(bookService).borrowBook(anyInt(), anyString(), any(LocalDate.class), any(LocalDate.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/borrow")
                .param("index", "99")
                .param("borrower", "User")
                .param("borrowedDate", "2023-01-01")
                .param("dueDate", "2023-01-08")
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /login should return login page")
    void loginPageReturnsLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Please sign in")));
    }

    @Test
    @DisplayName("GET /register should return register page")
    void registerPageReturnsRegister() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Register")));
    }

    @Test
    @DisplayName("GET /password-reset-request should return password reset request page")
    void passwordResetRequestPageReturnsView() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/password-reset-request"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Password Reset")));
    }

    @Test
    @DisplayName("POST /unmarkRead/{index} should unmark as read")
    void unmarkAsReadWorks() throws Exception {
        Book book = new Book("Test", "Author", LocalDate.now());
        given(bookService.getBookByIndex(0)).willReturn(book);
        mockMvc.perform(MockMvcRequestBuilders.post("/unmarkRead/0").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("POST /unmarkProgress/{index} should unmark in progress")
    void unmarkInProgressWorks() throws Exception {
        Book book = new Book("Test", "Author", LocalDate.now());
        given(bookService.getBookByIndex(0)).willReturn(book);
        mockMvc.perform(MockMvcRequestBuilders.post("/unmarkProgress/0").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("POST /return/{index} should return book")
    void returnBookWorks() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/return/0").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /editBook/{index} should edit book")
    void editBookWorks() throws Exception {
        Book book = new Book("Test", "Author", LocalDate.now());
        given(bookService.getBookByIndex(0)).willReturn(book);
        mockMvc.perform(MockMvcRequestBuilders.post("/editBook/0")
                .param("title", "New Title")
                .param("author", "New Author")
                .param("genre", "Fiction")
                .param("summary", "A summary")
                .param("rating", "5")
                .param("publishedOn", "2023-01-01")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
} 