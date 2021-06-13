package com.poc.bookstore;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.bookstore.controller.BookController;
import com.poc.bookstore.dto.BookDto;
import com.poc.bookstore.dto.Classification;
import com.poc.bookstore.exception.BookNotFoundException;
import com.poc.bookstore.service.BookService;

@WebMvcTest(BookController.class)
class BookControllerMockTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookService bookServiceMock;

	@Test
	void findBooks_ShouldReturnBooks() throws Exception {

		List<BookDto> bookList = Arrays.asList(getBook());

		when(bookServiceMock.findAllBooks()).thenReturn(bookList);
		this.mockMvc.perform(get("/books")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Jungle Book")).andExpect(jsonPath("$[0].price").value(100));

	}

	@Test
	void findOneBook_ValidBookId_ShouldReturnBook() throws Exception {

		Long bookId = 1l;

		when(bookServiceMock.getBookDetails(bookId)).thenReturn(getBook());
		this.mockMvc.perform(get("/books/" + bookId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Jungle Book")).andExpect(jsonPath("$.price").value(100))
				.andExpect(jsonPath("$.id").value(1));

	}

	@Test
	void findOneBook_InvalidBookId_ShouldReturn404() throws Exception {

		Long bookId = 1l;

		when(bookServiceMock.getBookDetails(bookId)).thenThrow(new BookNotFoundException(bookId));
		this.mockMvc.perform(get("/books/" + bookId)).andExpect(status().isNotFound());

	}

	@Test
	void updateBook_ValidBookId_ShouldReturn204() throws Exception {

		Long bookId = 1l;

		BookDto book = getBook();

		doNothing().when(bookServiceMock).modifyBook(any());

		this.mockMvc
				.perform(put("/books/" + bookId).contentType(MediaType.APPLICATION_JSON).content(asJsonString(book)))
				.andExpect(status().isNoContent());

	}

	@Test
	void updateBook_InvalidBookId_ShouldReturn404() throws Exception {

		Long bookId = 1l;

		BookDto book = getBook();

		doThrow(new BookNotFoundException(bookId)).when(bookServiceMock).modifyBook(any());

		this.mockMvc
				.perform(put("/books/" + bookId).contentType(MediaType.APPLICATION_JSON).content(asJsonString(book)))
				.andExpect(status().isNotFound());

	}
	
	@Test
	void updateBookClassification_ValidBookId_ShouldReturn204() throws Exception {

		Long bookId = 1l;

		BookDto book = getBook();

		doNothing().when(bookServiceMock).modifyBookClassification(any());

		this.mockMvc
				.perform(patch("/books/" + bookId).contentType(MediaType.APPLICATION_JSON).content(asJsonString(book)))
				.andExpect(status().isNoContent());

	}

	@Test
	void updateBookClassification_InvalidBookId_ShouldReturn404() throws Exception {

		Long bookId = 1l;

		BookDto book = getBook();

		doThrow(new BookNotFoundException(bookId)).when(bookServiceMock).modifyBookClassification(any());

		this.mockMvc
				.perform(patch("/books/" + bookId).contentType(MediaType.APPLICATION_JSON).content(asJsonString(book)))
				.andExpect(status().isNotFound());

	}

	@Test
	void saveNewBook_ShouldReturn201AndBookId() throws Exception {

		Long bookId = 1l;

		BookDto book = getBook();

		when(bookServiceMock.saveBook(any())).thenReturn(bookId);

		this.mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(asJsonString(book)))
				.andExpect(status().isCreated())
				.andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/books/" + bookId));
	}

	@Test
	void deleteBook_ValidBookId_ShouldReturn200() throws Exception {

		Long bookId = 1l;

		BookDto book = getBook();

		doNothing().when(bookServiceMock).deleteBookById(any());

		this.mockMvc
				.perform(delete("/books/" + bookId).contentType(MediaType.APPLICATION_JSON).content(asJsonString(book)))
				.andExpect(status().isOk());

	}

	@Test
	void deleteBook_InvalidBookId_ShouldReturn404() throws Exception {

		Long bookId = 1l;

		BookDto book = getBook();

		doThrow(new BookNotFoundException(bookId)).when(bookServiceMock).deleteBookById(any());

		this.mockMvc
				.perform(delete("/books/" + bookId).contentType(MediaType.APPLICATION_JSON).content(asJsonString(book)))
				.andExpect(status().isNotFound());

	}

	private BookDto getBook() {
		BookDto bookDto = new BookDto();
		bookDto.setName("Jungle Book");
		bookDto.setClassification(Classification.FICTION.toString());
		bookDto.setPrice(100d);
		bookDto.setId(1l);
		bookDto.setAuthor("Sharaf");
		return bookDto;
	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
