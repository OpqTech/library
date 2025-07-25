package opqlibrary;

import java.util.function.Predicate;

public class BookPublishedYearFilter implements Predicate<Book> {
    private final int year;
    public BookPublishedYearFilter(int year) {
        this.year = year;
    }
    @Override
    public boolean test(Book book) {
        return book.getPublishedOn() != null && book.getPublishedOn().getYear() == year;
    }
} 