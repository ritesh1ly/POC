package com.poc.bookstore.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.poc.bookstore.dto.BookDto;
import com.poc.bookstore.entity.Book;
import com.poc.bookstore.exception.BookNotFoundException;
import com.poc.bookstore.exception.BookValidationException;
import com.poc.bookstore.repositories.BookRepository;

@Service
public class BookService {

	private final BookRepository bookRepository;
	private final ModelMapper modelMapper;

	public BookService(BookRepository bookRepository, ModelMapper modelMapper) {
		this.bookRepository = bookRepository;
		this.modelMapper = modelMapper;
	}

	public List<BookDto> findAllBooks() {
		Iterable<Book> bookEntityList = bookRepository.findAll();
		return Arrays.asList(modelMapper.map(bookEntityList, BookDto[].class));
	}

	public List<BookDto> findBookByIds(List<Long> bookIds) {
		Iterable<Book> bookEntityList = bookRepository.findAllById(bookIds);
		return Arrays.asList(modelMapper.map(bookEntityList, BookDto[].class));
	}

	public BookDto getBookDetails(Long bookId) {
		Book bookEntity = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
		return modelMapper.map(bookEntity, BookDto.class);
	}

	public Long saveBook(BookDto bookDto) {
		if(bookDto.getName()==null || bookDto.getName().length()==0)
		{
			throw new BookValidationException();
		
		}
		Book bookEntity = modelMapper.map(bookDto, Book.class);
		bookRepository.save(bookEntity);
		return bookEntity.getId();
	}

	public void modifyBook(BookDto bookDto) {
		if (!bookRepository.findById(bookDto.getId()).isPresent()) {
			throw new BookNotFoundException(bookDto.getId());
		}
		bookRepository.save(modelMapper.map(bookDto, Book.class));
	}

	public void modifyBookClassification(BookDto bookDto) {
		Optional<Book> existingBookEntry = bookRepository.findById(bookDto.getId());
		if (!existingBookEntry.isPresent()) {
			throw new BookNotFoundException(bookDto.getId());
		}
		Book existingBook = existingBookEntry.get();
		existingBook.setClassification(bookDto.getClassification());
		bookRepository.save(existingBook);
	}

	public void deleteBookById(Long bookId) {
		if (!bookRepository.findById(bookId).isPresent()) {
			throw new BookNotFoundException(bookId);
		}
		bookRepository.deleteById(bookId);
	}

	public BookDto filterBookFromList(List<BookDto> bookList, Long bookId) {
		return bookList.stream().filter(book -> bookId.equals(book.getId())).findAny().orElse(null);
	}
}
