package com.poc.bookstore.dto;

import java.util.ArrayList;
import java.util.List;

public class CartResponseDto {

	private List<CartItemResponseDto> cartItems;
	private String promotionCode;
	private Double amountPayable;

	public List<CartItemResponseDto> getCartItems() {
		return cartItems != null && !cartItems.isEmpty() ? cartItems : new ArrayList<>();
	}

	public void setCartItems(List<CartItemResponseDto> cartItems) {
		this.cartItems = cartItems;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public Double getAmountPayable() {
		return amountPayable;
	}

	public void setAmountPayable(Double amountPayable) {
		this.amountPayable = amountPayable;
	}

}
