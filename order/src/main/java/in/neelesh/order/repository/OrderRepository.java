package in.neelesh.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.neelesh.common.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

	Optional<Order> findByOrderNumber(String orderNumber);

	List<Order> findByUser_UserId(String userId);

}
