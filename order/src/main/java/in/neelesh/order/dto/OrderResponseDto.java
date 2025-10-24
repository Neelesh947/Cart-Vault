package in.neelesh.order.dto;

import java.math.BigDecimal;
import java.util.List;

import in.neelesh.common.enums.OrderStatus;
import in.neelesh.common.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {
	
	private String orderId;
	private String orderNumber;
	private String userId;
	private String shippingAddressId;
	private BigDecimal totalAmount;
	private OrderStatus orderStatus;
	private PaymentStatus paymentStatus;
	private String paymentId;
	private List<OrderItemDto> orderItems;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class OrderItemDto {
		
		private String productId;
		private String productName;
		private Integer quantity;
		private BigDecimal price;
		private BigDecimal totalPrice;
	}
}