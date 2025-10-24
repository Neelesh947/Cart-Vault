package in.neelesh.order.service;

import in.neelesh.order.dto.CartResponseDto;

public interface CartService {

	CartResponseDto addProductToCart(String userId, String productId, Integer quantity, String realm);

	public CartResponseDto getActiveCartForUser(String userId);
	
	public CartResponseDto removeProductFromCart(String userId, String productId);
	
	public CartResponseDto clearCart(String userId);
	
	public CartResponseDto updateProductQuantity(String userId, String productId, Integer quantity);
}
