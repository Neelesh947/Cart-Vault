package in.neelesh.order.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.neelesh.order.dto.CartResponseDto;
import in.neelesh.order.service.CartService;
import in.neelesh.order.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/{realm}/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	@PostMapping("/{userId}/add")
	public ResponseEntity<CartResponseDto> addToCart(@RequestParam String productId, @RequestParam Integer quantity,
			@PathVariable String realm) {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		CartResponseDto cart = cartService.addProductToCart(userId, productId, quantity, realm);
		return ResponseEntity.status(HttpStatus.CREATED).body(cart);
	}

	@GetMapping
	public ResponseEntity<CartResponseDto> getListOfCartByUser() {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		CartResponseDto cart = cartService.getActiveCartForUser(userId);
		return ResponseEntity.ok(cart);
	}

	@DeleteMapping("/{productId}/remove")
	public ResponseEntity<CartResponseDto> removeFromCart(@PathVariable String realm, @PathVariable String productId) {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		CartResponseDto response = cartService.removeProductFromCart(userId, productId);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping
	public ResponseEntity<CartResponseDto> clearCart() {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		CartResponseDto response = cartService.clearCart(userId);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{productId}/update")
	public ResponseEntity<CartResponseDto> updateProductQuantity(@PathVariable String realm,
			@PathVariable String productId, @RequestParam Integer quantity) {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		CartResponseDto response = cartService.updateProductQuantity(userId, productId, quantity);
		return ResponseEntity.ok(response);
	}
}
