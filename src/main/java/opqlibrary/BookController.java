package opqlibrary;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@Controller
@RequestMapping("/")
public class BookController {
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;
    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String listBooks(@RequestParam(value = "searchTitle", required = false) String searchTitle,
                            @RequestParam(value = "searchAuthor", required = false) String searchAuthor,
                            Model model) {
        List<Book> books = bookService.getAllBooks();
        if (searchTitle != null && !searchTitle.isEmpty()) {
            books = books.stream().filter(b -> b.getTitle().toLowerCase().contains(searchTitle.toLowerCase())).toList();
        }
        if (searchAuthor != null && !searchAuthor.isEmpty()) {
            books = books.stream().filter(b -> b.getAuthor().toLowerCase().contains(searchAuthor.toLowerCase())).toList();
        }
        model.addAttribute("books", books);
        model.addAttribute("newBook", new Book());
        model.addAttribute("searchTitle", searchTitle);
        model.addAttribute("searchAuthor", searchAuthor);
        return "books";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/password-reset-request")
    public String passwordResetRequestPage() {
        return "password-reset-request";
    }

    @PostMapping
    public String addBook(@ModelAttribute Book newBook) throws BookShelfCapacityReached {
        logger.info("Adding book: {} by {}", newBook.getTitle(), newBook.getAuthor());
        bookService.addBook(newBook);
        return "redirect:/";
    }

    @PostMapping("/markRead/{index}")
    public String markAsRead(@PathVariable int index) {
        logger.info("Marking book at index {} as read", index);
        Book book = bookService.getBookByIndex(index);
        book.startedReadingOn(java.time.LocalDate.now().minusDays(1));
        book.finishedReadingOn(java.time.LocalDate.now());
        return "redirect:/";
    }

    @PostMapping("/markProgress/{index}")
    public String markInProgress(@PathVariable int index) {
        logger.info("Marking book at index {} as in progress", index);
        Book book = bookService.getBookByIndex(index);
        book.startedReadingOn(java.time.LocalDate.now());
        book.finishedReadingOn(null);
        return "redirect:/";
    }

    @PostMapping("/unmarkRead/{index}")
    public String unmarkAsRead(@PathVariable int index) {
        logger.info("Unmarking book at index {} as read", index);
        Book book = bookService.getBookByIndex(index);
        book.startedReadingOn(null);
        book.finishedReadingOn(null);
        return "redirect:/";
    }

    @PostMapping("/unmarkProgress/{index}")
    public String unmarkInProgress(@PathVariable int index) {
        logger.info("Unmarking book at index {} as in progress", index);
        Book book = bookService.getBookByIndex(index);
        book.startedReadingOn(null);
        book.finishedReadingOn(null);
        return "redirect:/";
    }

    @PostMapping("/delete/{index}")
    public String deleteBook(@PathVariable int index) {
        logger.info("Deleting book at index {}", index);
        bookService.removeBook(index);
        return "redirect:/";
    }

    @PostMapping("/borrow")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> borrowBook(
            @RequestParam int index,
            @RequestParam String borrower,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate borrowedDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate
    ) {
        logger.info("Borrowing book at index {} by {} from {} to {}", index, borrower, borrowedDate, dueDate);
        Map<String, Object> response = new java.util.HashMap<>();
        try {
            bookService.borrowBook(index, borrower, borrowedDate, dueDate);
            response.put("status", "ok");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to borrow book at index {}: {}", index, e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/return/{index}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> returnBook(@PathVariable int index) {
        logger.info("Returning (unborrowing) book at index {}", index);
        Map<String, Object> response = new java.util.HashMap<>();
        try {
            bookService.returnBook(index);
            response.put("status", "ok");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to return book at index {}: {}", index, e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/editBook/{index}")
    public String editBook(@PathVariable int index,
                           @RequestParam String title,
                           @RequestParam String author,
                           @RequestParam String genre,
                           @RequestParam String summary,
                           @RequestParam Integer rating,
                           @RequestParam String publishedOn) {
        logger.info("Editing book at index {}: {} by {}", index, title, author);
        Book book = bookService.getBookByIndex(index);
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setSummary(summary);
        book.setRating(rating);
        if (publishedOn != null && !publishedOn.isEmpty()) {
            book.setPublishedOn(java.time.LocalDate.parse(publishedOn));
        }
        return "redirect:/";
    }
} 