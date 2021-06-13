package com.poc.bookstore;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import com.poc.bookstore.dto.BookDto;
import com.poc.bookstore.dto.Classification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookstoreApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;	

	@Test
	void saveNewBook_ShouldReturnSuccess() throws Exception {
		BookDto book = getNewBookDto();
		Long expectedBookId = 1l;

		HttpEntity<BookDto> entity = new HttpEntity<BookDto>(book);

		ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/books"), HttpMethod.POST, entity,
				String.class);

		String actual = response.getHeaders().get(HttpHeaders.LOCATION).get(0);

		assertTrue(actual.contains("/books/" + expectedBookId));

	}

	@Test
	@Sql({ "/test-book-data.sql" })
	void findAllBooks_ShouldReturnBookList() throws Exception {

		ResponseEntity<String> response = restTemplate.getForEntity(createURLWithPort("/books"), String.class);

		BookDto expectedBook = getNewBookDto();
		expectedBook.setId(1l);

		assertTrue(response.getBody().contains("The Hobbit"));
		assertTrue(response.getBody().contains("10"));
		assertTrue(response.getBody().contains("J. R. R. Tolkien"));
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}

	private BookDto getNewBookDto() {
		BookDto book = new BookDto();
		book.setAuthor("J. R. R. Tolkien");
		book.setClassification(Classification.FICTION.toString());
		book.setDescription(
				"The Hobbit, or There and Back Again is a children's fantasy novel by English author J. R. R. Tolkien.");
		book.setIsbn("978-3-16-148410-0");
		book.setName("The Hobbit");
		book.setPrice(100d);
		return book;
	}

}
