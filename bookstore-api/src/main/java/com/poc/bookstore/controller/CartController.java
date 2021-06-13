package com.poc.bookstore.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poc.bookstore.dto.CartRequestDto;
import com.poc.bookstore.dto.CartResponseDto;
import com.poc.bookstore.service.CartService;

@RestController
@RequestMapping("cart")
public class CartController {

	private final CartService cartService;

	CartController(CartService cartService) {
		this.cartService = cartService;
	}

	@PostMapping
	public ResponseEntity<CartResponseDto> addToCart(@RequestBody @Valid CartRequestDto cart) {
		CartResponseDto cartResponse = cartService.calculateCartAmount(cart);
		return new ResponseEntity<>(cartResponse, HttpStatus.OK);
	}
}
