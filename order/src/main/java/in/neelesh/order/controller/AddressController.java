package in.neelesh.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.neelesh.order.dto.AddressRequestDto;
import in.neelesh.order.dto.AddressResponseDto;
import in.neelesh.order.service.AddressService;
import in.neelesh.order.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;

@RequestMapping("/{realm}/address")
@RestController
@RequiredArgsConstructor
public class AddressController {

	private final AddressService addressService;

	@PostMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<AddressResponseDto> createAddress(@RequestBody AddressRequestDto request,
			@PathVariable String realm) {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		AddressResponseDto response = addressService.createAddress(request, userId, realm);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<AddressResponseDto>> getAllAddresses(@PathVariable String realm) {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		List<AddressResponseDto> response = addressService.getAllAddresses(userId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<AddressResponseDto> getAddressById(@PathVariable String id, @PathVariable String realm) {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		AddressResponseDto response = addressService.getAddressById(id, userId);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAddress(@PathVariable String id) {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		addressService.deleteAddress(id, userId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<AddressResponseDto> updateAddress(@PathVariable String id,
			@RequestBody AddressRequestDto request) {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		AddressResponseDto address = addressService.updateAddress(id, request, userId);
		return ResponseEntity.ok(address);
	}

	@PatchMapping("/{id}/default")
	public ResponseEntity<Void> setDefaultAddress(@PathVariable String id) {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		addressService.setDefaultAddress(id, userId);
		return ResponseEntity.ok().build();
	}
}
