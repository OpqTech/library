package opqlibrary;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BookService {
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    private final BookShelf bookShelf;
    @Autowired
    public BookService(BookShelf bookShelf) {
        this.bookShelf = bookShelf;
    }

    public List<Book> getAllBooks() {
        return bookShelf.books();
    }

    public void addBook(Book book) throws BookShelfCapacityReached {
        try {
            bookShelf.add(book);
        } catch (BookShelfCapacityReached e) {
            logger.warn("BookShelf capacity reached when adding book: {} by {}", book.getTitle(), book.getAuthor(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error adding book: {} by {}", book.getTitle(), book.getAuthor(), e);
            throw e;
        }
    }

    public void removeBook(int index) {
        try {
            bookShelf.removeBook(index);
        } catch (Exception e) {
            logger.error("Error removing book at index {}", index, e);
            throw e;
        }
    }

    public void markAsRead(int index) {
        try {
            Book book = bookShelf.books().get(index);
            book.startedReadingOn(java.time.LocalDate.now().minusDays(1));
            book.finishedReadingOn(java.time.LocalDate.now());
        } catch (Exception e) {
            logger.error("Error marking book as read at index {}", index, e);
            throw e;
        }
    }

    public void markInProgress(int index) {
        try {
            Book book = bookShelf.books().get(index);
            book.startedReadingOn(java.time.LocalDate.now());
            book.finishedReadingOn(null);
        } catch (Exception e) {
            logger.error("Error marking book as in progress at index {}", index, e);
            throw e;
        }
    }

    public List<Book> searchBooks(String title, String author) {
        try {
            return bookShelf.books().stream()
                .filter(b -> (title == null || b.getTitle().toLowerCase().contains(title.toLowerCase())))
                .filter(b -> (author == null || b.getAuthor().toLowerCase().contains(author.toLowerCase())))
                .toList();
        } catch (Exception e) {
            logger.error("Error searching books: title='{}', author='{}'", title, author, e);
            throw e;
        }
    }

    public void borrowBook(int index, String borrower, java.time.LocalDate borrowedDate, java.time.LocalDate dueDate) {
        try {
            List<Book> books = bookShelf.books();
            if (index < 0 || index >= books.size()) {
                throw new IllegalArgumentException("Invalid book index for borrowing.");
            }
            Book book = books.get(index);
            book.setBorrowed(true);
            book.setBorrower(borrower);
            book.setBorrowedDate(borrowedDate);
            book.setDueDate(dueDate);
        } catch (Exception e) {
            logger.error("Error borrowing book at index {}: {}", index, e.getMessage(), e);
            throw e;
        }
    }
    public void returnBook(int index) {
        try {
            List<Book> books = bookShelf.books();
            if (index < 0 || index >= books.size()) {
                throw new IllegalArgumentException("Invalid book index for return.");
            }
            Book book = books.get(index);
            book.setBorrowed(false);
            book.setBorrower(null);
            book.setBorrowedDate(null);
            book.setDueDate(null);
        } catch (Exception e) {
            logger.error("Error returning book at index {}: {}", index, e.getMessage(), e);
            throw e;
        }
    }

    public Book getBookByIndex(int index) {
        try {
            List<Book> books = getAllBooks();
            if (index < 0 || index >= books.size()) {
                throw new IllegalArgumentException("Invalid book index.");
            }
            return books.get(index);
        } catch (Exception e) {
            logger.error("Error getting book by index {}: {}", index, e.getMessage(), e);
            throw e;
        }
    }
} 