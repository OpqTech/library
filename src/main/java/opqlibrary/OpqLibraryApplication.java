package opqlibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import opqlibrary.Book;
import opqlibrary.BookService;
import opqlibrary.BookShelfCapacityReached;

@SpringBootApplication
public class OpqLibraryApplication {
    private final BookService bookService;
    public OpqLibraryApplication(BookService bookService) {
        this.bookService = bookService;
    }

    @PostConstruct
    public void seedData() {
        try {
            System.out.println("Seeding default books...");
            bookService.addBook(new Book("Linux", "Linus Torvalds", LocalDate.of(2001, 1, 1), "Operating System", "A book about Linux.", 5));
            bookService.addBook(new Book("Docker", "Solomon Hykes", LocalDate.of(2013, 3, 20), "DevOps", "A book about Docker.", 5));
            bookService.addBook(new Book("Jenkins", "Kohsuke Kawaguchi", LocalDate.of(2011, 2, 2), "CI/CD", "A book about Jenkins.", 4));
            bookService.addBook(new Book("Kubernetes", "Joe Beda", LocalDate.of(2015, 6, 7), "Container Orchestration", "A book about Kubernetes.", 5));
            bookService.addBook(new Book("Spring Boot", "Pivotal", LocalDate.of(2016, 4, 1), "Framework", "A book about Spring Boot.", 4));
            System.out.println("Default books seeded.");
        } catch (BookShelfCapacityReached e) {
            System.err.println("Seed data: Book shelf capacity reached. Some books may not be added.");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(OpqLibraryApplication.class, args);
    }
} 