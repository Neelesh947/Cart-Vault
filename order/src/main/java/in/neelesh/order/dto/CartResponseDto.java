package in.neelesh.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponseDto {
	private String cartId;
	private String userId;
	private List<CartItemDto> cartItems;

	@Data
	@Builder
	public static class CartItemDto {
		private String cartItemId;
		private String productId;
		private Integer quantity;
		private String productName;
		private BigDecimal productPrice;
	}
}
