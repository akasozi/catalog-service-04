package com.polarbookshop.catalogservice04;

import com.polarbookshop.catalogservice04.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
class CatalogService04ApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void whenPostRequestThenBookCreated() {

		var expectedBook = Book.of("1231231231", "Cloud Native Spring in Action", "Thomas Vitalle", 35.99, "Polarsophia");

		webTestClient
				.post()
				.uri("/books")
				.bodyValue(expectedBook)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
				});
	}

	@Test
	void whenGetRequestWithIdThenBookReturned() {
		var bookIsbn = "1231231230";
		var bookToCreate = Book.of(bookIsbn, "Cloud Native Spring in Action", "Thomas Vitalle", 36.99, "Polarsophia");

		Book expectedBook = webTestClient
				.post()
				.uri("/books")
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(book -> assertThat(book).isNotNull())
				.returnResult().getResponseBody();

		webTestClient
				.get()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
				});
	}


	@Test
	void whenPutRequestThenBookUpdated() {
		var bookIsbn = "1231231234";
		var bookToCreate = Book.of(bookIsbn, "Cloud Native Spring in Action", "Thomas Vitalle", 36.99, "Polarsophia");

		Book expectedBook = webTestClient
				.post()
				.uri("/books")
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(book -> assertThat(book).isNotNull())
				.returnResult().getResponseBody();

		var bookToUpdate = Book.of(bookIsbn, "Java the Complete reference!", "P.J. Deitel", 19.99, "Polarsophia");

		webTestClient
				.put()
				.uri("/books/" + bookIsbn)
				.bodyValue(bookToUpdate)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Book.class).value(book -> {
					assertThat(book).isNotNull();
					assertThat(book.title()).isEqualTo(bookToUpdate.title());
					assertThat(book.author()).isEqualTo(bookToUpdate.author());
					assertThat(book.price()).isEqualTo(bookToUpdate.price());
				});
	}

	@Test
	void whenDeleteRequestThenBookDeleted() {
		var bookIsbn = "1231231234";
		var bookToCreate = Book.of(bookIsbn, "Cloud Native Spring in Action", "Thomas Vitalle", 36.99, "Polarsophia");

		 webTestClient
				.post()
				.uri("/books")
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated();

		webTestClient
				.delete()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().isNoContent();

		webTestClient
				.get()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody(String.class).value(errorMessage -> {
					assertThat(errorMessage).isEqualTo("The book with ISBN " + bookIsbn + " was not found.");
				});


	}

	@Test
	void whenPostRequestWithDuplicateIsbnThenBookAlreadyExistsExceptionThrown() {
		var bookIsbn = "1231231235";
		var bookToCreate = Book.of(bookIsbn, "Cloud Native Spring in Action", "Thomas Vitalle", 36.99, "Polarsophia");

		webTestClient
				.post()
				.uri("/books")
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated();

		webTestClient
				.post()
				.uri("/books")
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectBody(String.class).value(errorMessage -> {
					assertThat(errorMessage).isEqualTo("A book with ISBN " + bookIsbn + " already exists.");
				});

	}

}
