export interface CartItemDto {
  cartItemId: string;
  productId: string;
  quantity: number;
  productName: string;
  productPrice: number; // Use number for BigDecimal
}

export interface CartResponseDto {
  cartId: string;
  userId: string;
  cartItems: CartItemDto[];
}
