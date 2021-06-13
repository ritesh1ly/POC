package com.poc.bookstore;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.bookstore.controller.CartController;
import com.poc.bookstore.dto.CartRequestDto;
import com.poc.bookstore.dto.CartResponseDto;
import com.poc.bookstore.service.CartService;

@WebMvcTest(CartController.class)
class CartControllerMockTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CartService cartServiceMock;

	@Test
	void addToCart_ShouldReturn200() throws Exception {

		double amount = 100d;
		CartRequestDto cartRequest = getCartRequest();

		CartResponseDto cartResponse = getCartResponse(amount);
		when(cartServiceMock.calculateCartAmount(any())).thenReturn(cartResponse);
		this.mockMvc.perform(post("/cart").contentType(MediaType.APPLICATION_JSON).content(asJsonString(cartRequest)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.amountPayable").value(amount));

	}

	private CartRequestDto getCartRequest() {
		CartRequestDto cart = new CartRequestDto();
		return cart;
	}

	private CartResponseDto getCartResponse(double amount) {
		CartResponseDto cartReponse = new CartResponseDto();
		cartReponse.setAmountPayable(amount);
		return cartReponse;
	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
