package in.neelesh.order.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import in.neelesh.common.dto.KeycloakuserDto;
import in.neelesh.common.entity.Cart;
import in.neelesh.common.entity.CartItem;
import in.neelesh.common.entity.Product;
import in.neelesh.common.entity.User;
import in.neelesh.order.dto.CartResponseDto;
import in.neelesh.order.dto.ProductResponseDto;
import in.neelesh.order.repository.CartItemRepository;
import in.neelesh.order.repository.CartRepository;
import in.neelesh.order.repository.UserRepository;
import in.neelesh.order.utils.KeycloakUtility;
import in.neelesh.order.utils.ProductUtility;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final UserRepository userRepository;
	private final KeycloakUtility keycloakUtility;
	private final ProductUtility productUtility;

	@Override
	public CartResponseDto addProductToCart(String userId, String productId, Integer quantity, String realm) {
		User user = findOrCreateUser(userId, realm);
		Cart cart = findOrCreateCart(user);
		Product product = fetchProduct(productId, realm);
		addOrUpdateCartItem(cart, product, quantity);
		Cart carts = getUpdatedCart(cart.getId());
		return mapToDto(carts);
	}

	private User findOrCreateUser(String userId, String realm) {
		KeycloakuserDto userDto = keycloakUtility.getUserbyId(userId, realm);
		if (userDto == null) {
			throw new IllegalArgumentException("User not found in Keycloak");
		}

		return userRepository.findByUserId(userId)
				.orElseGet(() -> userRepository.save(mapToUserEntity(userDto, userId)));
	}

	private User mapToUserEntity(KeycloakuserDto userDto, String userId) {
		User user = new User();
		user.setUserId(userId);
		user.setName(userDto.getFirstName() + " " + userDto.getLastName());
		user.setEmail(userDto.getEmail());

		String phone = extractPhoneNumber(userDto);
		user.setPhone(phone);
		return user;
	}

	private String extractPhoneNumber(KeycloakuserDto userDto) {
		if (userDto.getAttributes() == null)
			return null;
		List<String> phoneList = userDto.getAttributes().get("phoneNumber");
		return (phoneList != null && !phoneList.isEmpty()) ? phoneList.get(0) : null;
	}

	private Cart findOrCreateCart(User user) {
		Optional<Cart> existingCart = cartRepository.findByUser(user);
		if (existingCart.isPresent()) {
			return existingCart.get();
		}
		Cart cart = new Cart();
		cart.setUser(user);
		return cartRepository.save(cart);
	}

	private Product fetchProduct(String productId, String realm) {
		ProductResponseDto productDto = productUtility.getProductDetails(productId, realm);
		if (productDto == null) {
			throw new IllegalArgumentException("Product not found in Product microservice");
		}

		Product product = new Product();
		product.setId(productDto.getId());
		return product;
	}

	private void addOrUpdateCartItem(Cart cart, Product product, Integer quantity) {
		Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndProductId(cart, product.getId());
		CartItem cartItem;

		if (existingItemOpt.isPresent()) {
			cartItem = existingItemOpt.get();
			cartItem.setQuantity(cartItem.getQuantity() + quantity);
		} else {
			cartItem = CartItem.builder().cart(cart).productId(product.getId()).quantity(quantity).build();
		}
		cartItemRepository.save(cartItem);
	}

	private Cart getUpdatedCart(String cartId) {
		return cartRepository.findById(cartId)
				.orElseThrow(() -> new IllegalArgumentException("Cart not found after update"));
	}

	private CartResponseDto mapToDto(Cart cart) {
		return CartResponseDto.builder().cartId(cart.getId().toString()).userId(cart.getUser().getUserId())
				.cartItems(cart.getCartItems().stream()
						.map(item -> CartResponseDto.CartItemDto.builder().cartItemId(item.getId().toString())
								.productId(item.getProductId()).quantity(item.getQuantity()).productName(null)
								.productPrice(null).build())
						.collect(Collectors.toList()))
				.build();
	}

	public CartResponseDto getActiveCartForUser(String userId) {
		Optional<Cart> optionalCart = cartRepository.findByUser_UserId(userId);
		if (optionalCart.isEmpty()) {
			throw new IllegalArgumentException("No active cart found for userId: " + userId);
		}
		Cart cart = optionalCart.get();
		List<CartItem> item = cartItemRepository.findByCart(cart);
		cart.setCartItems(item);
		return mapToDto(cart);
	}

	public CartResponseDto removeProductFromCart(String userId, String productId) {
		Optional<Cart> optionalCart = cartRepository.findByUser_UserId(userId);
		if (optionalCart.isEmpty()) {
			throw new IllegalArgumentException("No active cart found for userId: " + userId);
		}
		Cart cart = optionalCart.get();
		Optional<CartItem> cartItemOpt = cartItemRepository.findByCartAndProductId(cart, productId);
		if (cartItemOpt.isEmpty()) {
			throw new IllegalArgumentException("Product not found in cart for productId: " + productId);
		}
		cartItemRepository.delete(cartItemOpt.get());
		cart.getCartItems().remove(cartItemOpt.get());
		return mapToDto(cart);
	}

	public CartResponseDto clearCart(String userId) {
		Optional<Cart> optionalCart = cartRepository.findByUser_UserId(userId);
		if (optionalCart.isEmpty()) {
			throw new IllegalArgumentException("No active cart found for userId: " + userId);
		}
		Cart cart = optionalCart.get();
		cart.getCartItems().clear();
		cartRepository.save(cart);
		return mapToDto(cart);
	}

	public CartResponseDto updateProductQuantity(String userId, String productId, Integer quantity) {
		Optional<Cart> optionalCart = cartRepository.findByUser_UserId(userId);
		if (optionalCart.isEmpty()) {
			throw new IllegalArgumentException("No active cart found for userId: " + userId);
		}
		Cart cart = optionalCart.get();
		CartItem cartItem = cartItemRepository.findByCartAndProductId(cart, productId)
				.orElseThrow(() -> new EntityNotFoundException("Product not found in cart"));
		cartItem.setQuantity(quantity);
		cartItemRepository.save(cartItem);
		cart.getCartItems().forEach(item -> {
			if (item.getProductId().equals(productId)) {
				item.setQuantity(quantity);
			}
		});
		return mapToDto(cart);
	}
}