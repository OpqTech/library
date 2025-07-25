package opqlibrary;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.*;

class BookPublishedYearFilterTest {
    @Test
    void filtersBooksByYear() {
        Book b1 = new Book("A", "B", LocalDate.of(2020, 1, 1));
        Book b2 = new Book("C", "D", LocalDate.of(2021, 1, 1));
        BookPublishedYearFilter filter2020 = new BookPublishedYearFilter(2020);
        List<Book> books = List.of(b1, b2);
        List<Book> filtered = books.stream().filter(filter2020).collect(Collectors.toList());
        assertThat(filtered).containsExactly(b1);
    }
    @Test
    void returnsEmptyIfNoMatch() {
        Book b1 = new Book("A", "B", LocalDate.of(2020, 1, 1));
        BookPublishedYearFilter filter2022 = new BookPublishedYearFilter(2022);
        List<Book> books = List.of(b1);
        List<Book> filtered = books.stream().filter(filter2022).collect(Collectors.toList());
        assertThat(filtered).isEmpty();
    }
} 