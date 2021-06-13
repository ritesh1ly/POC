package com.poc.bookstore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.poc.bookstore.dto.BookDto;
import com.poc.bookstore.dto.CartItemRequestDto;
import com.poc.bookstore.dto.CartRequestDto;
import com.poc.bookstore.dto.CartResponseDto;
import com.poc.bookstore.dto.Classification;
import com.poc.bookstore.service.BookService;
import com.poc.bookstore.service.CartService;

@ExtendWith(MockitoExtension.class)
class CartServiceTests {

	@Mock
	private BookService bookServiceMock;

	@Test
	void calculateCartAmount_WithPromotion_Fiction_Book_WillPass() {

		final double bookPrice = 100d;
		final double amountAfterDiscount = bookPrice - (bookPrice * 10 / 100);
		final double subTotalAfterDiscount = bookPrice - (bookPrice * 10 / 100);
		final Long bookId = 1l;
		final int quantity = 1;
		final String promotionCode = "SUMMEROFF";
		ModelMapper modelMapper = new ModelMapper();

		BookDto mockFictionBook = getMockBook(bookId, true, bookPrice);

		List<Long> bookingIds = Arrays.asList(bookId);
		List<BookDto> bookList = Arrays.asList(mockFictionBook);

		CartService cartService = new CartService(bookServiceMock, modelMapper);

		when(bookServiceMock.findBookByIds(bookingIds)).thenReturn(bookList);

		when(bookServiceMock.filterBookFromList(bookList, bookId)).thenReturn(mockFictionBook);

		CartRequestDto cartRequestDto = buildCartRequest(promotionCode, quantity, mockFictionBook);

		CartResponseDto cartResponseDto = cartService.calculateCartAmount(cartRequestDto);

		assertEquals(amountAfterDiscount, cartResponseDto.getAmountPayable());
		assertNotNull(cartResponseDto.getCartItems());
		assertEquals(bookId, cartResponseDto.getCartItems().get(0).getBookId());
		assertEquals(quantity, cartResponseDto.getCartItems().get(0).getQuantity());
		assertEquals(bookPrice, cartResponseDto.getCartItems().get(0).getSubTotal());
		assertEquals(subTotalAfterDiscount, cartResponseDto.getCartItems().get(0).getSubTotalAfterDiscount());
	}

	@Test
	void calculateCartAmount_WithPromotion_NonFiction_Book_WillPass() {

		double bookPrice = 100d;
		Long bookId = 1l;
		final int quantity = 1;
		final String promotionCode = "SUMMEROFF";
		ModelMapper modelMapper = new ModelMapper();

		BookDto mockBook = getMockBook(bookId, false, bookPrice);

		List<Long> bookingIds = Arrays.asList(bookId);
		List<BookDto> bookList = Arrays.asList(mockBook);

		CartService cartService = new CartService(bookServiceMock, modelMapper);

		when(bookServiceMock.findBookByIds(bookingIds)).thenReturn(bookList);

		when(bookServiceMock.filterBookFromList(bookList, bookId)).thenReturn(mockBook);

		CartRequestDto cartRequestDto = buildCartRequest(promotionCode, quantity, mockBook);

		CartResponseDto cartResponseDto = cartService.calculateCartAmount(cartRequestDto);

		assertEquals(bookPrice, cartResponseDto.getAmountPayable());
		assertNotNull(cartResponseDto.getCartItems());
		assertEquals(bookId, cartResponseDto.getCartItems().get(0).getBookId());
		assertEquals(quantity, cartResponseDto.getCartItems().get(0).getQuantity());
		assertEquals(bookPrice, cartResponseDto.getCartItems().get(0).getSubTotal());

	}

	@Test
	void calculateCartAmount_WithoutPromotion_Fiction_Book_WillPass() {

		double bookPrice = 100d;
		Long bookId = 1l;
		final int quantity = 1;
		BookDto mockBook = getMockBook(bookId, true, bookPrice);
		ModelMapper modelMapper = new ModelMapper();

		List<Long> bookingIds = Arrays.asList(bookId);
		List<BookDto> bookList = Arrays.asList(mockBook);

		CartService cartService = new CartService(bookServiceMock, modelMapper);

		when(bookServiceMock.findBookByIds(bookingIds)).thenReturn(bookList);

		when(bookServiceMock.filterBookFromList(bookList, bookId)).thenReturn(mockBook);

		CartRequestDto cartRequestDto = buildCartRequest(null, quantity, mockBook);

		CartResponseDto cartResponseDto = cartService.calculateCartAmount(cartRequestDto);

		assertEquals(bookPrice, cartResponseDto.getAmountPayable());
	}

	@Test
	void calculateCartAmount_WithoutPromotion_NonFiction_Book_WillPass() {

		double bookPrice = 100d;
		Long bookId = 1l;
		final int quantity = 1;
		BookDto mockBook = getMockBook(bookId, false, bookPrice);
		ModelMapper modelMapper = new ModelMapper();

		List<Long> bookingIds = Arrays.asList(bookId);
		List<BookDto> bookList = Arrays.asList(mockBook);

		CartService cartService = new CartService(bookServiceMock, modelMapper);

		when(bookServiceMock.findBookByIds(bookingIds)).thenReturn(bookList);

		when(bookServiceMock.filterBookFromList(bookList, bookId)).thenReturn(mockBook);

		CartRequestDto cartRequestDto = buildCartRequest(null, quantity, mockBook);

		CartResponseDto cartResponseDto = cartService.calculateCartAmount(cartRequestDto);

		assertEquals(bookPrice, cartResponseDto.getAmountPayable());
	}

	private CartRequestDto buildCartRequest(String promotionCode, int quantity, BookDto book) {
		CartRequestDto cartDto = new CartRequestDto();
		cartDto.setPromotionCode(promotionCode);
		List<CartItemRequestDto> cartItems = new ArrayList<>();
		CartItemRequestDto cartItem = new CartItemRequestDto();
		cartItem.setBookId(book.getId());
		cartItem.setQuantity(quantity);
		cartItems.add(cartItem);
		cartDto.setCartItems(cartItems);
		return cartDto;
	}

	private BookDto getMockBook(Long id, boolean isFiction, double bookPrice) {
		BookDto bookDto = new BookDto();
		bookDto.setId(id);
		bookDto.setAuthor("Sharaf");
		bookDto.setDescription("");
		bookDto.setName("Jungle Book");
		bookDto.setPrice(bookPrice);
		if (isFiction) {
			bookDto.setClassification(Classification.FICTION.toString());
		} else {
			bookDto.setClassification(Classification.ADVENTURE.toString());
		}
		return bookDto;
	}

}
