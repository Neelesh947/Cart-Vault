package in.neelesh.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.neelesh.common.entity.Cart;
import in.neelesh.common.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {

	Optional<CartItem> findByCartAndProductId(Cart cart, String productId);

	List<CartItem> findByCart(Cart cart);

}
