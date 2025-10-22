package in.neelesh.product.controller;

import in.neelesh.common.entity.InventoryItem;
import in.neelesh.product.dto.InventoryRequestDto;
import in.neelesh.product.services.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/{realm}/inventory")
@RequiredArgsConstructor
public class InventoryController {

	private final InventoryService inventoryService;

	@PostMapping
	public ResponseEntity<InventoryItem> createInventory(@Valid @RequestBody InventoryRequestDto request) {
		InventoryItem created = inventoryService.createInventoryItems(request);
		return ResponseEntity.ok(created);
	}
}
