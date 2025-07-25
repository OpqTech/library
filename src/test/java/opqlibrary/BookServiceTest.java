package opqlibrary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class BookServiceTest {
    private BookShelf bookShelf;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookShelf = new BookShelf();
        bookService = new BookService(bookShelf);
    }

    @Test
    void canAddAndRetrieveBooks() throws BookShelfCapacityReached {
        Book book = new Book("Test Title", "Test Author", LocalDate.now());
        bookService.addBook(book);
        List<Book> books = bookService.getAllBooks();
        assertThat(books).contains(book);
    }

    @Test
    void canRemoveBookByIndex() throws BookShelfCapacityReached {
        Book book1 = new Book("A", "B", LocalDate.now());
        Book book2 = new Book("C", "D", LocalDate.now());
        bookService.addBook(book1);
        bookService.addBook(book2);
        bookService.removeBook(0);
        assertThat(bookService.getAllBooks()).containsExactly(book2);
    }

    @Test
    void canMarkBookAsRead() throws BookShelfCapacityReached {
        Book book = new Book("A", "B", LocalDate.now());
        bookService.addBook(book);
        bookService.markAsRead(0);
        assertThat(bookService.getAllBooks().get(0).isRead()).isTrue();
    }

    @Test
    void canMarkBookAsInProgress() throws BookShelfCapacityReached {
        Book book = new Book("A", "B", LocalDate.now());
        bookService.addBook(book);
        bookService.markInProgress(0);
        assertThat(bookService.getAllBooks().get(0).isProgress()).isTrue();
    }

    @Test
    void canSearchBooksByTitleAndAuthor() throws BookShelfCapacityReached {
        Book book1 = new Book("Java", "Alice", LocalDate.now());
        Book book2 = new Book("Spring", "Bob", LocalDate.now());
        bookService.addBook(book1);
        bookService.addBook(book2);
        assertThat(bookService.searchBooks("java", null)).containsExactly(book1);
        assertThat(bookService.searchBooks(null, "bob")).containsExactly(book2);
        assertThat(bookService.searchBooks("spring", "bob")).containsExactly(book2);
    }

    @Test
    void borrowBookWithInvalidIndexThrows() {
        assertThatThrownBy(() -> bookService.borrowBook(0, "User", java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(7)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid book index");
    }
    @Test
    void returnBookWithInvalidIndexThrows() {
        assertThatThrownBy(() -> bookService.returnBook(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid book index");
    }
    @Test
    void canBorrowAndReturnBook() throws Exception {
        Book book = new Book("Test", "User", java.time.LocalDate.now());
        bookService.addBook(book);
        bookService.borrowBook(0, "User", java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(7));
        assertThat(bookService.getAllBooks().get(0).isBorrowed()).isTrue();
        assertThat(bookService.getAllBooks().get(0).getBorrower()).isEqualTo("User");
        bookService.returnBook(0);
        assertThat(bookService.getAllBooks().get(0).isBorrowed()).isFalse();
        assertThat(bookService.getAllBooks().get(0).getBorrower()).isNull();
    }

    @Test
    void getBookByIndexReturnsBook() throws BookShelfCapacityReached {
        Book book = new Book("Test", "Author", LocalDate.now());
        bookService.addBook(book);
        Book found = bookService.getBookByIndex(0);
        assertThat(found).isEqualTo(book);
    }

    @Test
    void getBookByIndexWithInvalidIndexThrows() {
        assertThatThrownBy(() -> bookService.getBookByIndex(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid book index");
    }
} 