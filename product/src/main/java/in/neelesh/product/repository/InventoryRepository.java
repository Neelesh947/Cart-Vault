package in.neelesh.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.neelesh.common.entity.InventoryItem;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, String>{

	Optional<InventoryItem> findByProductId(String productId);

}
