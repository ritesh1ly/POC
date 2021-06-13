package com.poc.bookstore.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.poc.bookstore.dto.BookDto;
import com.poc.bookstore.service.BookService;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@RestController
@RequestMapping("books")
public class BookController {

	private final BookService bookService;

	BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@GetMapping
	public List<BookDto> findAllBooks() {
		return bookService.findAllBooks();
	}

	@GetMapping("/{id}")
	public BookDto findOneBook(@PathVariable Long id) {

		return bookService.getBookDetails(id);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updateBook(@RequestBody BookDto bookDto, @PathVariable Long id) {
        bookDto.setId(id);
		bookService.modifyBook(bookDto);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Void> modifyBookClassification(@RequestBody BookDto bookDto, @PathVariable Long id) {
        bookDto.setId(id);
		bookService.modifyBookClassification(bookDto);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping
	@Bulkhead(name = "saveNewBook", fallbackMethod = "tooManyRequestBulkheadHandler")
	@RateLimiter(name = "saveNewBook", fallbackMethod = "tooManyRequestRateLimterHandler")
	@Retry(name = "saveNewBook", fallbackMethod = "retryHandler")
	@TimeLimiter(name = "saveNewBook", fallbackMethod = "timeoutHandler")
	public ResponseEntity<Void> saveNewBook(@RequestBody @Valid BookDto newBook, UriComponentsBuilder builder) {
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("saveNewBook>"+newBook.getName());
		Long bookId = bookService.saveBook(newBook);
		// Set the book id as location header
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(builder.path("/books/{id}").buildAndExpand(bookId).toUri());
		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}
	public ResponseEntity<Void>  tooManyRequestBulkheadHandler(BookDto newBook, UriComponentsBuilder builder, Throwable throwable){
		HttpHeaders headers = new HttpHeaders();
		System.out.println("error tooManyRequestBulkheadHandler saveNewBook>"+newBook.getName());
		return new ResponseEntity<>(headers, HttpStatus.TOO_MANY_REQUESTS);
	}
	
	public ResponseEntity<Void>  tooManyRequestRateLimterHandler(BookDto newBook, UriComponentsBuilder builder, Throwable throwable){
		HttpHeaders headers = new HttpHeaders();
		System.out.println("error tooManyRequestRateLimterHandler saveNewBook>"+newBook.getName());
		return new ResponseEntity<>(headers, HttpStatus.TOO_MANY_REQUESTS);
	}
	
	public ResponseEntity<Void>  retryHandler(BookDto newBook, UriComponentsBuilder builder, Throwable throwable){
		HttpHeaders headers = new HttpHeaders();
		System.out.println("error Retry saveNewBook>"+newBook.getName());
		return new ResponseEntity<>(headers, HttpStatus.GATEWAY_TIMEOUT);
	}
	
	public ResponseEntity<Void>  timeoutHandler(BookDto newBook, UriComponentsBuilder builder, Throwable throwable){
		HttpHeaders headers = new HttpHeaders();
		System.out.println("error Timeout saveNewBook>"+newBook.getName());
		return new ResponseEntity<>(headers, HttpStatus.REQUEST_TIMEOUT);
	}
	
	@DeleteMapping("/{id}")
	public void deleteEmployee(@PathVariable Long id) {
		bookService.deleteBookById(id);
	}
	
	

}
