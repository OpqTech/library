package opqlibrary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/books")
public class BookRestController {
    private static final Logger logger = LoggerFactory.getLogger(BookRestController.class);
    private final BookService bookService;

    public BookRestController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> getAllBooks() {
        logger.info("Fetching all books");
        return bookService.getAllBooks();
    }

    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        try {
            logger.info("Adding book via REST: {} by {}", book.getTitle(), book.getAuthor());
            bookService.addBook(book);
            return ResponseEntity.ok().build();
        } catch (BookShelfCapacityReached e) {
            logger.warn("Failed to add book via REST: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error adding book via REST", e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @DeleteMapping("/{index}")
    public ResponseEntity<?> deleteBook(@PathVariable int index) {
        try {
            logger.info("Deleting book via REST at index {}", index);
            bookService.removeBook(index);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting book via REST at index {}: {}", index, e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @PostMapping("/markRead/{index}")
    public ResponseEntity<?> markAsRead(@PathVariable int index) {
        try {
            logger.info("Marking book as read via REST at index {}", index);
            bookService.markAsRead(index);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error marking book as read via REST at index {}: {}", index, e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @PostMapping("/markProgress/{index}")
    public ResponseEntity<?> markInProgress(@PathVariable int index) {
        try {
            logger.info("Marking book as in progress via REST at index {}", index);
            bookService.markInProgress(index);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error marking book as in progress via REST at index {}: {}", index, e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam(required = false) String title, @RequestParam(required = false) String author) {
        logger.info("Searching books via REST: title='{}', author='{}'", title, author);
        return bookService.searchBooks(title, author);
    }
} 