package in.neelesh.order.service;

import java.util.List;

import in.neelesh.order.dto.OrderResponseDto;

public interface OrderService {

	OrderResponseDto checkoutOrder(String userId, String shippingAddressId, String realm);

	OrderResponseDto getOrderByNumber(String userId, String orderNumber);

	public List<OrderResponseDto> getOrdersByUser(String userId);

	public OrderResponseDto updateOrderStatus(String orderNumber, String status);

	public OrderResponseDto updatePaymentStatus(String orderNumber, String paymentStatus, String paymentId);

	public List<OrderResponseDto> getAllOrders();

}
