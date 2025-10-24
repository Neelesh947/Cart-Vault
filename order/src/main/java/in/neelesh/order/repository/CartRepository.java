package in.neelesh.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.neelesh.common.entity.Cart;
import in.neelesh.common.entity.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

	Optional<Cart> findByUser(User user);

	Optional<Cart> findByUser_UserId(String userId);

}
