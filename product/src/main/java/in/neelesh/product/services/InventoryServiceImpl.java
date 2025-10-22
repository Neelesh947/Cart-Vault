package in.neelesh.product.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.neelesh.common.entity.InventoryItem;
import in.neelesh.common.entity.Product;
import in.neelesh.product.dto.InventoryRequestDto;
import in.neelesh.product.repository.InventoryRepository;
import in.neelesh.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

	private final InventoryRepository inventoryRepository;
	private final ProductRepository productRepository;

	@Override
	@Transactional
	public InventoryItem createInventoryItems(InventoryRequestDto dto) {
		Product product = productRepository.findById(dto.getProductId())
				.orElseThrow(() -> new EntityNotFoundException("Product not found: " + dto.getProductId()));
		InventoryItem inventory = InventoryItem.builder().product(product).stock(dto.getStock())
				.warehouse(dto.getWarehouse() != null ? dto.getWarehouse() : "Default Warehouse").build();
		return inventoryRepository.save(inventory);
	}

	@Override
	public InventoryItem getByProductId(String id) {
		return inventoryRepository.findByProductId(id).orElse(null);
	}

	@Override
	public void save(InventoryItem inventory) {
		inventoryRepository.save(inventory);
	}
}
