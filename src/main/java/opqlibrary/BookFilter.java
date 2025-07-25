package opqlibrary;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface BookFilter {
  boolean apply(Book b);
} 