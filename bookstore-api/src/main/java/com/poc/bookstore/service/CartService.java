package com.poc.bookstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.poc.bookstore.dto.BookDto;
import com.poc.bookstore.dto.CartItemRequestDto;
import com.poc.bookstore.dto.CartItemResponseDto;
import com.poc.bookstore.dto.CartRequestDto;
import com.poc.bookstore.dto.CartResponseDto;
import com.poc.bookstore.dto.Classification;

@Service
public class CartService {

	private final BookService bookService;
	private final ModelMapper modelMapper;
	private static final String PROMOTION_CODE = "SUMMEROFF";

	public CartService(BookService bookService, ModelMapper modelMapper) {
		this.bookService = bookService;
		this.modelMapper = modelMapper;
	}

	public CartResponseDto calculateCartAmount(CartRequestDto cart) {

		Double amountPayable = 0d;
		CartResponseDto cartResponse = modelMapper.map(cart, CartResponseDto.class);
		List<CartItemResponseDto> cartItemResponseList = new ArrayList<>();
		// extract book IDs from the cart array and find their details from database
		List<Long> bookIds = cart.getCartItems().stream().map(CartItemRequestDto::getBookId)
				.collect(Collectors.toList());
		List<BookDto> bookList = bookService.findBookByIds(bookIds);

		// iterate cart items and calculate payable amount after applying discount
		for (CartItemRequestDto cartItemDto : cart.getCartItems()) {
			BookDto bookDetails = bookService.filterBookFromList(bookList, cartItemDto.getBookId());

			CartItemResponseDto cartItemResponse = calculateCartItemPayable(cartItemDto, bookDetails,
					cart.getPromotionCode());

			cartItemResponseList.add(cartItemResponse);

			amountPayable += cartItemResponse.getSubTotalAfterDiscount();
		}

		// returning cart with amount payable and breakups against each item
		cartResponse.setAmountPayable(amountPayable);
		cartResponse.setCartItems(cartItemResponseList);
		return cartResponse;
	}

	private CartItemResponseDto calculateCartItemPayable(CartItemRequestDto cartRequestItem, BookDto bookDetails,
			String promotionCode) {

		// apply discount only if the correct promotion code is available
		int discountPerc = PROMOTION_CODE.equalsIgnoreCase(promotionCode)
				? getDiscountPercentage(bookDetails.getClassification())
				: 0;
		Double originalAmount = bookDetails.getPrice() * cartRequestItem.getQuantity();
		Double amountAfterDiscount = originalAmount - (originalAmount * discountPerc / 100);

		CartItemResponseDto cartItemResponseDto = modelMapper.map(cartRequestItem, CartItemResponseDto.class);
		// returning back the sub-total and discounted amount if the client want to
		// display to user
		cartItemResponseDto.setSubTotal(originalAmount);
		cartItemResponseDto.setSubTotalAfterDiscount(amountAfterDiscount);
		return cartItemResponseDto;
	}

	private int getDiscountPercentage(String bookClassification) {
		if (Classification.FICTION.toString().equals(bookClassification)) {
			return 10;
		} else {
			return 0;
		}
	}

}
