package in.neelesh.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.neelesh.order.dto.OrderResponseDto;
import in.neelesh.order.service.OrderService;
import in.neelesh.order.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/{realm}/order")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping("/checkout")
	public ResponseEntity<OrderResponseDto> checkout(@PathVariable String realm,
			@RequestParam String shippingAddressId) {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		OrderResponseDto order = orderService.checkoutOrder(userId, shippingAddressId, realm);
		return ResponseEntity.status(HttpStatus.CREATED).body(order);
	}

	@GetMapping("/{orderNumber}")
	public ResponseEntity<OrderResponseDto> getOrder(@PathVariable String realm, @PathVariable String orderNumber) {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		OrderResponseDto order = orderService.getOrderByNumber(userId, orderNumber);
		return ResponseEntity.ok(order);
	}

	@GetMapping("/user")
	public ResponseEntity<List<OrderResponseDto>> getOrderByUser() {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		List<OrderResponseDto> list = orderService.getOrdersByUser(userId);
		return ResponseEntity.ok(list);
	}

	/**
	 * Internal API call
	 * 
	 * @param orderNumber
	 * @param status
	 * @return
	 */
	@PutMapping("/{orderNumber}/status")
	public ResponseEntity<OrderResponseDto> updateOrderStatus(@PathVariable String orderNumber,
			@RequestParam String status) {
		OrderResponseDto updatedStatus = orderService.updateOrderStatus(orderNumber, status);
		return ResponseEntity.ok(updatedStatus);
	}

	/**
	 * Internal API call
	 * 
	 * @param orderNumber
	 * @param status
	 * @return
	 */
	@PutMapping("/{orderNumber}/payment")
	public ResponseEntity<OrderResponseDto> updatePaymentStatus(@PathVariable String orderNumber,
			@RequestParam String paymentStatus, @RequestParam String paymentId) {
		OrderResponseDto updatedStatus = orderService.updatePaymentStatus(orderNumber, paymentStatus, paymentId);
		return ResponseEntity.ok(updatedStatus);
	}

	@GetMapping
	public ResponseEntity<List<OrderResponseDto>> getAllOrders(@PathVariable String realm) {
		List<OrderResponseDto> orders = orderService.getAllOrders();
		return ResponseEntity.ok(orders);
	}
}
