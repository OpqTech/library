package opqlibrary;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.*;

class CompositeFilterTest {
    @Test
    void combinesMultipleFilters() {
        Book b1 = new Book("A", "B", LocalDate.of(2020, 1, 1));
        Book b2 = new Book("C", "D", LocalDate.of(2021, 1, 1));
        Predicate<Book> year2020 = book -> book.getPublishedOn().getYear() == 2020;
        Predicate<Book> authorB = book -> book.getAuthor().equals("B");
        CompositeFilter filter = new CompositeFilter(List.of(year2020, authorB));
        List<Book> books = List.of(b1, b2);
        List<Book> filtered = books.stream().filter(filter).collect(Collectors.toList());
        assertThat(filtered).containsExactly(b1);
    }
    @Test
    void returnsEmptyIfNoMatch() {
        Book b1 = new Book("A", "B", LocalDate.of(2020, 1, 1));
        Predicate<Book> year2021 = book -> book.getPublishedOn().getYear() == 2021;
        CompositeFilter filter = new CompositeFilter(List.of(year2021));
        List<Book> books = List.of(b1);
        List<Book> filtered = books.stream().filter(filter).collect(Collectors.toList());
        assertThat(filtered).isEmpty();
    }
} 