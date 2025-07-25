package opqlibrary;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

class BookTest {
    @Test
    void equalsAndHashCodeWork() {
        Book b1 = new Book("A", "B", LocalDate.now());
        Book b2 = new Book("A", "B", LocalDate.now());
        assertThat(b1).isEqualTo(b2);
        assertThat(b1.hashCode()).isEqualTo(b2.hashCode());
    }

    @Test
    void compareToSortsByTitle() {
        Book b1 = new Book("A", "B", LocalDate.now());
        Book b2 = new Book("B", "C", LocalDate.now());
        assertThat(b1.compareTo(b2)).isLessThan(0);
    }

    @Test
    void toStringReturnsUsefulString() {
        Book b = new Book("A", "B", LocalDate.of(2020, 1, 1));
        assertThat(b.toString()).contains("A").contains("B").contains("2020");
    }

    @Test
    void isReadAndIsProgressWork() {
        Book b = new Book("A", "B", LocalDate.now());
        assertThat(b.isRead()).isFalse();
        assertThat(b.isProgress()).isFalse();
        b.startedReadingOn(LocalDate.now());
        assertThat(b.isProgress()).isTrue();
        b.finishedReadingOn(LocalDate.now());
        assertThat(b.isRead()).isTrue();
    }
} 