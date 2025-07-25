package opqlibrary;

import java.util.List;
import java.util.function.Predicate;

public class CompositeFilter implements Predicate<Book> {
    private final List<Predicate<Book>> filters;
    public CompositeFilter(List<Predicate<Book>> filters) {
        this.filters = filters;
    }
    @Override
    public boolean test(Book book) {
        for (Predicate<Book> filter : filters) {
            if (!filter.test(book)) {
                return false;
            }
        }
        return true;
    }
} 