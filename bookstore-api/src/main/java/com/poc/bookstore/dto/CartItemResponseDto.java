package com.poc.bookstore.dto;

public class CartItemResponseDto {

	private Long bookId;
	private int quantity;
	private Double subTotal;
	private Double subTotalAfterDiscount;

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(Double subTotal) {
		this.subTotal = subTotal;
	}

	public Double getSubTotalAfterDiscount() {
		return subTotalAfterDiscount;
	}

	public void setSubTotalAfterDiscount(Double subTotalAfterDiscount) {
		this.subTotalAfterDiscount = subTotalAfterDiscount;
	}
}
