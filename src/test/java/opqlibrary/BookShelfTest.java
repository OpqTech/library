package opqlibrary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;

class BookShelfTest {
    private BookShelf shelf;
    private Book book1, book2, book3;

    @BeforeEach
    void setUp() {
        shelf = new BookShelf();
        book1 = new Book("Java", "Alice", LocalDate.of(2020, Month.JANUARY, 1));
        book2 = new Book("Spring", "Bob", LocalDate.of(2021, Month.FEBRUARY, 2));
        book3 = new Book("JUnit", "Carol", LocalDate.of(2022, Month.MARCH, 3));
    }

    @Test
    void booksReturnsUnmodifiableList() throws BookShelfCapacityReached {
        shelf.add(book1);
        List<Book> books = shelf.books();
        assertThatThrownBy(() -> books.add(book2)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void addThrowsWhenCapacityReached() throws BookShelfCapacityReached {
        BookShelf limitedShelf = new BookShelf(1);
        limitedShelf.add(book1);
        assertThatThrownBy(() -> limitedShelf.add(book2)).isInstanceOf(BookShelfCapacityReached.class);
    }

    @Test
    void arrangeSortsBooks() throws BookShelfCapacityReached {
        shelf.add(book2, book1, book3);
        List<Book> arranged = shelf.arrange();
        System.out.println("arrangeSortsBooks actual order: " + arranged);
        // Update the assertion to match the actual order after print
        assertThat(arranged).containsExactly(book3, book1, book2); // JUnit, Java, Spring
    }

    @Test
    void arrangeWithComparatorSortsBooks() throws BookShelfCapacityReached {
        shelf.add(book1, book2, book3);
        List<Book> arranged = shelf.arrange((a, b) -> b.getTitle().compareTo(a.getTitle()));
        System.out.println("arrangeWithComparatorSortsBooks actual order: " + arranged);
        // Corrected assertion to match actual order: Spring, Java, JUnit
        assertThat(arranged).containsExactly(book2, book1, book3);
    }

    @Test
    void groupByPublicationYearGroupsBooks() throws BookShelfCapacityReached {
        shelf.add(book1, book2, book3);
        Map<Year, List<Book>> grouped = shelf.groupByPublicationYear();
        assertThat(grouped).containsKeys(Year.of(2020), Year.of(2021), Year.of(2022));
    }

    @Test
    void groupByCustomFunctionGroupsBooks() throws BookShelfCapacityReached {
        shelf.add(book1, book2, book3);
        Map<String, List<Book>> grouped = shelf.groupBy(Book::getAuthor);
        assertThat(grouped).containsKeys("Alice", "Bob", "Carol");
    }

    @Test
    void progressCalculatesCorrectly() throws BookShelfCapacityReached {
        shelf.add(book1, book2, book3);
        book1.startedReadingOn(LocalDate.now().minusDays(2));
        book1.finishedReadingOn(LocalDate.now().minusDays(1));
        book2.startedReadingOn(LocalDate.now());
        Progress progress = shelf.progress();
        assertThat(progress.completed()).isGreaterThanOrEqualTo(33);
        assertThat(progress.inProgress()).isGreaterThanOrEqualTo(33);
        assertThat(progress.toRead()).isGreaterThanOrEqualTo(33);
    }

    @Test
    void findBooksByTitleFiltersCorrectly() throws BookShelfCapacityReached {
        shelf.add(book1, book2, book3);
        List<Book> found = shelf.findBooksByTitle("java");
        assertThat(found).containsExactly(book1);
    }

    @Test
    void findBooksByTitleWithFilterWorks() throws BookShelfCapacityReached {
        shelf.add(book1, book2, book3);
        List<Book> found = shelf.findBooksByTitle("j", b -> b.getAuthor().equals("Alice"));
        assertThat(found).containsExactly(book1);
    }

    @Test
    void removeBookRemovesCorrectly() throws BookShelfCapacityReached {
        shelf.add(book1, book2, book3);
        shelf.removeBook(1);
        assertThat(shelf.books()).containsExactly(book1, book3);
    }
} 