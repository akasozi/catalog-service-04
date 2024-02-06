package com.polarbookshop.catalogservice04.domain;

import com.polarbookshop.catalogservice04.config.DataConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
@Import(DataConfig.class)
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@ActiveProfiles("integration")
class BookRepositoryJdbcTests {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private JdbcAggregateTemplate jdbcAggregateTemplate;

    @Test
    void findBookByIsbnWhenExisting() {
        var bookIsbn = "1234561237";
        var book = Book.of(bookIsbn, "Game of Thrones", "John Snow", 12.90);
        jdbcAggregateTemplate.insert(book);

        Optional<Book> actualBook = bookRepository.findByIsbn(bookIsbn);

        assertThat(actualBook).isPresent();
        assertThat(actualBook.get().isbn()).isEqualTo(book.isbn());
    }

    @Test
    void findByIsbnWhenNotExisting() {
        var bookIsbn = "1234561237";

        Optional<Book> actualBook = bookRepository.findByIsbn(bookIsbn);

        assertThat(actualBook).isNotPresent();
        assertThat(actualBook).isEmpty();
    }

    @Test
    void findAllBooks() {
        var book1 = Book.of("1234567891", "Sherlok Homes", "Unknown", 10.11);
        var book2 = Book.of("1234567892", "No Rules Rules!", "Reid Hastings", 9.99);
        var book3 = Book.of("1234567893", "Cloud Native Spring in Action", "Thomas Vitalle", 8.99);

        jdbcAggregateTemplate.insert(book1);
        jdbcAggregateTemplate.insert(book2);
        jdbcAggregateTemplate.insert(book3);

        Iterable<Book> books = bookRepository.findAll();

        assertThat(StreamSupport.stream(books.spliterator(), true)
                .filter(book -> book.isbn().equals(book1.isbn()) || book.isbn().equals(book2.isbn()) ||
                book.isbn().equals(book3.isbn()))
                .collect(Collectors.toList())).hasSize(3);

    }

    @Test
    void existsByIsbnWhenExisting() {
        var bookIsbn = "1234567891";
        var bookToCreate = Book.of(bookIsbn, "Sherlok Homes", "Unknown", 10.11);
        jdbcAggregateTemplate.insert(bookToCreate);

        boolean existing = bookRepository.existsByIsbn(bookIsbn);

        assertThat(existing).isTrue();
    }

    @Test
    void existsByIsbnWhenNotExisting() {
        boolean existing = bookRepository.existsByIsbn("1234567894");
        assertThat(existing).isFalse();
    }

    @Test
    void deleteByIsbn() {
        var bookIsbn = "1234567891";
        var bookToCreate = Book.of(bookIsbn, "Sherlok Homes", "Unknown", 10.11);
        var persistedBook = jdbcAggregateTemplate.insert(bookToCreate);
        bookRepository.deleteByIsbn(bookIsbn);

        boolean existing = bookRepository.existsByIsbn(bookIsbn);
        assertThat(existing).isFalse();
        assertThat(jdbcAggregateTemplate.findById(persistedBook.id(), Book.class)).isNull();
    }
}