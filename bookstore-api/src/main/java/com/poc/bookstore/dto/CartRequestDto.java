package com.poc.bookstore.dto;

import java.util.List;

public class CartRequestDto {

	private List<CartItemRequestDto> cartItems;
	private String promotionCode;

	public List<CartItemRequestDto> getCartItems() {
		return cartItems;
	}

	public void setCartItems(List<CartItemRequestDto> cartItems) {
		this.cartItems = cartItems;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

}
