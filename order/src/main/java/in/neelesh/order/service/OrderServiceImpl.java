package in.neelesh.order.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import in.neelesh.common.dto.KeycloakuserDto;
import in.neelesh.common.entity.Address;
import in.neelesh.common.entity.Cart;
import in.neelesh.common.entity.Order;
import in.neelesh.common.entity.OrderItem;
import in.neelesh.common.entity.User;
import in.neelesh.common.enums.OrderStatus;
import in.neelesh.common.enums.PaymentStatus;
import in.neelesh.order.dto.OrderResponseDto;
import in.neelesh.order.dto.OrderResponseDto.OrderItemDto;
import in.neelesh.order.repository.AddressRepository;
import in.neelesh.order.repository.CartRepository;
import in.neelesh.order.repository.OrderRepository;
import in.neelesh.order.repository.UserRepository;
import in.neelesh.order.utils.KeycloakUtility;
import in.neelesh.order.utils.ProductUtility;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final KeycloakUtility keycloakUtility;
	private final UserRepository userRepository;
	private final CartRepository cartRepository;
	private final AddressRepository addressRepository;
	private final ProductUtility productUtility;

	@Override
	public OrderResponseDto checkoutOrder(String userId, String shippingAddressId, String realm) {
		KeycloakuserDto userDto = keycloakUtility.getUserbyId(userId, realm);
		if (userDto == null) {
			throw new IllegalArgumentException("User not found in Keycloak");
		}
		Optional<User> optionalUser = userRepository.findByUserId(userId);
		User keycloakUser = new User();
		if (!optionalUser.isPresent()) {
			keycloakUser.setUserId(userId);
			keycloakUser.setName(userDto.getFirstName() + " " + userDto.getLastName());
			keycloakUser.setEmail(userDto.getEmail());

			String phoneNumber = null;
			if (userDto.getAttributes() != null && userDto.getAttributes().containsKey("phoneNumber")) {
				List<String> phoneList = userDto.getAttributes().get("phoneNumber");
				if (phoneList != null && !phoneList.isEmpty()) {
					phoneNumber = phoneList.get(0);
				}
			}
			keycloakUser.setPhone(phoneNumber);
			keycloakUser = userRepository.save(keycloakUser);
		} else {
			keycloakUser = optionalUser.get();
		}
		Cart cart = cartRepository.findByUser_UserId(keycloakUser.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("No active cart found"));
		if (cart.getCartItems().isEmpty()) {
			throw new IllegalArgumentException("Cart is empty, cannot create order");
		}
		Address shippingAddress = addressRepository.findById(shippingAddressId)
				.orElseThrow(() -> new IllegalArgumentException("Shipping address not found"));

		Order order = new Order();
		order.setOrderNumber(generateOrderNumber());
		order.setUser(keycloakUser);
		order.setOrderStatus(OrderStatus.PENDING);
		order.setPaymentStatus(PaymentStatus.PENDING);
		order.setShippingAddress(shippingAddress);

//		BigDecimal totalAmount = BigDecimal.ZERO;

		List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {
			BigDecimal price = productUtility.getProductPrice(cartItem.getProductId());
			BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

//			totalAmount = totalAmount.add(totalPrice);

			return OrderItem.builder().order(order).productId(cartItem.getProductId()).quantity(cartItem.getQuantity())
					.price(price).totalPrice(totalPrice).build();
		}).collect(Collectors.toList());

		BigDecimal totalAmount = orderItems.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		order.setOrderItems(orderItems);
		order.setTotalAmount(totalAmount);

		orderRepository.save(order);
		cart.getCartItems().clear();
		cartRepository.save(cart);

		return mapToDto(order);
	}

	private OrderResponseDto mapToDto(Order order) {
		List<OrderItemDto> items = order
				.getOrderItems().stream().map(oi -> OrderItemDto.builder().productId(oi.getProductId())
						.quantity(oi.getQuantity()).price(oi.getPrice()).totalPrice(oi.getTotalPrice()).build())
				.collect(Collectors.toList());
		return OrderResponseDto.builder().orderId(order.getId()).orderNumber(order.getOrderNumber())
				.userId(order.getUser().getUserId()).orderStatus(order.getOrderStatus())
				.paymentStatus(order.getPaymentStatus()).totalAmount(order.getTotalAmount())
				.shippingAddressId(order.getShippingAddress().getId()).orderItems(items).build();
	}

	private String generateOrderNumber() {
		return "ORD-" + System.currentTimeMillis(); // Simple order number generation
	}

	@Override
	public OrderResponseDto getOrderByNumber(String userId, String orderNumber) {
		Order order = orderRepository.findByOrderNumber(orderNumber)
				.orElseThrow(() -> new IllegalArgumentException("Order not found"));
		if (!order.getUser().getUserId().equals(userId)) {
			throw new IllegalArgumentException("Order does not belong to this user");
		}
		return mapToDto(order);
	}

	public List<OrderResponseDto> getOrdersByUser(String userId) {
		List<Order> orders = orderRepository.findByUser_UserId(userId);
		if (orders.isEmpty()) {
			return List.of();
		}
		return orders.stream().map(this::mapToDto).collect(Collectors.toList());
	}

	public OrderResponseDto updateOrderStatus(String orderNumber, String status) {
		Order order = orderRepository.findByOrderNumber(orderNumber)
				.orElseThrow(() -> new IllegalArgumentException("Order not found with orderNumber: " + orderNumber));
		try {
			order.setOrderStatus(OrderStatus.valueOf(status.toUpperCase()));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid order status: " + status);
		}
		Order updatedOrder = orderRepository.save(order);
		return mapToDto(updatedOrder);
	}

	public OrderResponseDto updatePaymentStatus(String orderNumber, String paymentStatus, String paymentId) {
		Order order = orderRepository.findByOrderNumber(orderNumber)
				.orElseThrow(() -> new IllegalArgumentException("Order not found"));
		order.setPaymentStatus(PaymentStatus.valueOf(paymentStatus.toUpperCase()));
		order.setPaymentId(paymentId);
		return mapToDto(order);
	}

	public List<OrderResponseDto> getAllOrders() {
		return orderRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
	}
}
