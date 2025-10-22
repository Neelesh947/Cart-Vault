package in.neelesh.product.services;

import in.neelesh.common.entity.InventoryItem;
import in.neelesh.product.dto.InventoryRequestDto;

public interface InventoryService {

	InventoryItem createInventoryItems(InventoryRequestDto dto);

	InventoryItem getByProductId(String id);

	public void save(InventoryItem inventory);
}
