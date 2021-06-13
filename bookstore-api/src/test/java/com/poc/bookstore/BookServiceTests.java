package com.poc.bookstore;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.poc.bookstore.dto.BookDto;
import com.poc.bookstore.dto.Classification;
import com.poc.bookstore.entity.Book;
import com.poc.bookstore.exception.BookNotFoundException;
import com.poc.bookstore.repositories.BookRepository;
import com.poc.bookstore.service.BookService;

@ExtendWith(MockitoExtension.class)
class BookServiceTests {

	@Mock
	private BookRepository bookRepositoryMock;

	@Test
	void findAllBooks_ShouldReturnBookList() {

		final double bookPrice = 100d;
		final Long bookId = 1l;

		Book mockBook = getMockBook(bookId, false, bookPrice);

		List<Book> bookList = Arrays.asList(mockBook);

		ModelMapper modelMapper = new ModelMapper();
		BookService bookService = new BookService(bookRepositoryMock, modelMapper);

		when(bookRepositoryMock.findAll()).thenReturn(bookList);

		List<BookDto> bookListReturned = bookService.findAllBooks();

		List<BookDto> bookDtoList = Arrays.asList(modelMapper.map(bookList, BookDto[].class));

		assertEquals(bookDtoList.size(), bookListReturned.size());

		verify(bookRepositoryMock).findAll();
	}

	@Test
	void findBooksByIds_ShouldReturnBookList() {

		final double bookPrice = 100d;
		final Long bookId = 1l;
		List<Long> bookIdList = Arrays.asList(bookId);

		Book mockBook = getMockBook(bookId, false, bookPrice);

		List<Book> bookList = Arrays.asList(mockBook);

		ModelMapper modelMapper = new ModelMapper();
		BookService bookService = new BookService(bookRepositoryMock, modelMapper);

		when(bookRepositoryMock.findAllById(bookIdList)).thenReturn(bookList);

		List<BookDto> bookListReturned = bookService.findBookByIds(bookIdList);

		List<BookDto> bookDtoList = Arrays.asList(modelMapper.map(bookList, BookDto[].class));

		assertEquals(bookDtoList.size(), bookListReturned.size());

		verify(bookRepositoryMock).findAllById(bookIdList);
	}

	@Test
	void filterBookFromList_ShouldReturnBook() {

		final double bookPrice = 100d;
		final Long bookId = 1l;
		Book mockBook = getMockBook(bookId, false, bookPrice);
		List<BookDto> bookList = Arrays.asList(new ModelMapper().map(mockBook, BookDto.class));

		ModelMapper modelMapper = new ModelMapper();
		BookService bookService = new BookService(bookRepositoryMock, modelMapper);

		BookDto bookReturned = bookService.filterBookFromList(bookList, bookId);

		assertEquals(mockBook.getId(), bookReturned.getId());
	}

	@Test
	void getBookDetails_UsingValidId_ShouldReturnABook() {

		final double bookPrice = 100d;
		final Long bookId = 1l;

		Book mockBook = getMockBook(bookId, false, bookPrice);

		ModelMapper modelMapper = new ModelMapper();
		BookService bookService = new BookService(bookRepositoryMock, modelMapper);

		when(bookRepositoryMock.findById(bookId)).thenReturn(Optional.of(mockBook));

		BookDto bookReturned = bookService.getBookDetails(bookId);

		assertEquals(mockBook.getName(), bookReturned.getName());
		assertEquals(mockBook.getId(), bookReturned.getId());

		verify(bookRepositoryMock).findById(bookId);
	}

	@Test
	void getBookDetails_UsingInvalidId_ShouldThrowBookNotFoundExeption() {

		final Long bookId = 1l;

		ModelMapper modelMapper = new ModelMapper();
		BookService bookService = new BookService(bookRepositoryMock, modelMapper);

		when(bookRepositoryMock.findById(bookId)).thenReturn(Optional.ofNullable(null));

		BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
			bookService.getBookDetails(bookId);
		});

		assertEquals("Could not find book " + bookId, exception.getMessage());
	}

	@Test
	void saveBook_ShouldReturnBookId() {

		final double bookPrice = 100d;
		final Long bookId = 1l;

		Book mockBook = getMockBook(bookId, false, bookPrice);

		ModelMapper modelMapper = new ModelMapper();
		BookService bookService = new BookService(bookRepositoryMock, modelMapper);

		BookDto mockBookDTO = modelMapper.map(mockBook, BookDto.class);

		when(bookRepositoryMock.save(any(Book.class))).thenReturn(mockBook);

		Long bookIdReturned = bookService.saveBook(mockBookDTO);

		assertNotNull(bookIdReturned);

		ArgumentCaptor<Book> bookArgument = ArgumentCaptor.forClass(Book.class);

		verify(bookRepositoryMock, times(1)).save(bookArgument.capture());
		verifyNoMoreInteractions(bookRepositoryMock);

	}

	@Test
	void modifyBook_ValidBookId_ShouldReturnBookId() {

		final double bookPrice = 100d;
		final Long bookId = 1l;

		Book mockBook = getMockBook(bookId, false, bookPrice);

		ModelMapper modelMapper = new ModelMapper();
		BookService bookService = new BookService(bookRepositoryMock, modelMapper);

		BookDto mockBookDTO = modelMapper.map(mockBook, BookDto.class);

		when(bookRepositoryMock.findById(bookId)).thenReturn(Optional.of(mockBook));
		when(bookRepositoryMock.save(any(Book.class))).thenReturn(mockBook);

		bookService.modifyBook(mockBookDTO);

		assertDoesNotThrow(() -> bookService.modifyBook(mockBookDTO));
	}

	@Test
	void modifyBook_InvalidBookId_ShouldThrowBookNotFoundException() {

		final double bookPrice = 100d;
		final Long bookId = 1l;

		Book mockBook = getMockBook(bookId, false, bookPrice);

		ModelMapper modelMapper = new ModelMapper();
		BookService bookService = new BookService(bookRepositoryMock, modelMapper);

		BookDto mockBookDTO = modelMapper.map(mockBook, BookDto.class);

		when(bookRepositoryMock.findById(bookId)).thenReturn(Optional.ofNullable(null));

		BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
			bookService.modifyBook(mockBookDTO);
		});

		assertEquals("Could not find book " + bookId, exception.getMessage());

	}
	
	@Test
	void modifyBookClassification_ValidBookId_ShouldReturnBookId() {

		final double bookPrice = 100d;
		final Long bookId = 1l;

		Book mockBook = getMockBook(bookId, false, bookPrice);

		ModelMapper modelMapper = new ModelMapper();
		BookService bookService = new BookService(bookRepositoryMock, modelMapper);

		BookDto mockBookDTO = modelMapper.map(mockBook, BookDto.class);

		when(bookRepositoryMock.findById(bookId)).thenReturn(Optional.of(mockBook));
		when(bookRepositoryMock.save(any(Book.class))).thenReturn(mockBook);

		bookService.modifyBookClassification(mockBookDTO);

		assertDoesNotThrow(() -> bookService.modifyBook(mockBookDTO));
	}

	@Test
	void modifyBookClassification_InvalidBookId_ShouldThrowBookNotFoundException() {

		final double bookPrice = 100d;
		final Long bookId = 1l;

		Book mockBook = getMockBook(bookId, false, bookPrice);

		ModelMapper modelMapper = new ModelMapper();
		BookService bookService = new BookService(bookRepositoryMock, modelMapper);

		BookDto mockBookDTO = modelMapper.map(mockBook, BookDto.class);

		when(bookRepositoryMock.findById(bookId)).thenReturn(Optional.ofNullable(null));

		BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
			bookService.modifyBookClassification(mockBookDTO);
		});

		assertEquals("Could not find book " + bookId, exception.getMessage());

	}	

	@Test
	void deleteBook_ValidBookId_ShouldDeleteIfBookFound() {

		final double bookPrice = 100d;
		final Long bookId = 1l;

		Book mockBook = getMockBook(bookId, false, bookPrice);

		ModelMapper modelMapper = new ModelMapper();
		BookService bookService = new BookService(bookRepositoryMock, modelMapper);

		when(bookRepositoryMock.findById(bookId)).thenReturn(Optional.of(mockBook));

		bookService.deleteBookById(bookId);

		verify(bookRepositoryMock).deleteById(bookId);

	}

	@Test
	void deleteBook_InvalidBookId_ShouldThrowBookNotFoundException() {

		final Long bookId = 1l;

		ModelMapper modelMapper = new ModelMapper();
		BookService bookService = new BookService(bookRepositoryMock, modelMapper);

		when(bookRepositoryMock.findById(bookId)).thenReturn(Optional.ofNullable(null));

		BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
			bookService.deleteBookById(bookId);
		});

		assertEquals("Could not find book " + bookId, exception.getMessage());

	}

	private Book getMockBook(Long id, boolean isFiction, double bookPrice) {
		Book book = new Book();
		book.setId(id);
		book.setAuthor("Sharaf");
		book.setDescription("");
		book.setName("Jungle Book");
		book.setPrice(bookPrice);
		if (isFiction) {
			book.setClassification(Classification.FICTION.toString());
		} else {
			book.setClassification(Classification.ADVENTURE.toString());
		}
		return book;
	}
}
