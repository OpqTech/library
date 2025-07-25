package opqlibrary;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class Book implements Comparable<Book> {
    private String title;
    private String author;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate publishedOn;
    private LocalDate startedReadingOn;
    private LocalDate finishedReadingOn;
    private String genre;
    private String summary;
    private Integer rating;
    private boolean borrowed;
    private String borrower;
    private java.time.LocalDate borrowedDate;
    private java.time.LocalDate dueDate;

    public Book() {
        this.title = null;
        this.author = null;
        this.publishedOn = null;
        this.genre = null;
        this.summary = null;
        this.rating = null;
    }

    public Book(String title, String author, LocalDate publishedOn) {
        this.title = title;
        this.author = author;
        this.publishedOn = publishedOn;
        this.genre = null;
        this.summary = null;
        this.rating = null;
    }

    public Book(String title, String author, LocalDate publishedOn, String genre, String summary, Integer rating) {
        this.title = title;
        this.author = author;
        this.publishedOn = publishedOn;
        this.genre = genre;
        this.summary = summary;
        this.rating = rating;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public LocalDate getPublishedOn() { return publishedOn; }
    public void setPublishedOn(LocalDate publishedOn) { this.publishedOn = publishedOn; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public boolean isRead() {
        return startedReadingOn != null && finishedReadingOn != null;
    }
    public boolean isProgress() {
        return startedReadingOn != null && finishedReadingOn == null;
    }
    public boolean getRead() { return isRead(); }
    public boolean getProgress() { return isProgress(); }

    public boolean isBorrowed() { return borrowed; }
    public void setBorrowed(boolean borrowed) { this.borrowed = borrowed; }
    public String getBorrower() { return borrower; }
    public void setBorrower(String borrower) { this.borrower = borrower; }
    public java.time.LocalDate getBorrowedDate() { return borrowedDate; }
    public void setBorrowedDate(java.time.LocalDate borrowedDate) { this.borrowedDate = borrowedDate; }
    public java.time.LocalDate getDueDate() { return dueDate; }
    public void setDueDate(java.time.LocalDate dueDate) { this.dueDate = dueDate; }
    public boolean isOverdue() {
        return borrowed && dueDate != null && dueDate.isBefore(java.time.LocalDate.now());
    }

    public void startedReadingOn(LocalDate startedOn) {
        this.startedReadingOn = startedOn;
    }

    public void finishedReadingOn(LocalDate finishedOn) {
        this.finishedReadingOn = finishedOn;
    }

    public String getPublishedOnFormatted() {
        return publishedOn != null ? java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd").format(publishedOn) : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        if (title != null ? !title.equals(book.title) : book.title != null) return false;
        if (author != null ? !author.equals(book.author) : book.author != null) return false;
        return publishedOn != null ? publishedOn.equals(book.publishedOn) : book.publishedOn == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (publishedOn != null ? publishedOn.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Book that) {
        return this.title.compareTo(that.title);
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publishedOn=" + publishedOn +
                ", genre='" + genre + '\'' +
                ", rating=" + rating +
                ", summary='" + summary + '\'' +
                '}';
    }
} 