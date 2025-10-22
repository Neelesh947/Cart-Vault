package in.neelesh.product.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.neelesh.product.dto.BrandRequest;
import in.neelesh.product.dto.BrandResponse;
import in.neelesh.product.services.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/{realm}/brands")
@RequiredArgsConstructor
public class BrandController {

	private final BrandService brandService;

	@PostMapping
	@PreAuthorize("hasRole('super_admin')")
	public ResponseEntity<BrandResponse> create(@Valid @RequestBody BrandRequest brandRequest) {
		return ResponseEntity.ok(brandService.createBrand(brandRequest));
	}

	@GetMapping("/{id}")
	public ResponseEntity<BrandResponse> getById(@PathVariable String id) {
		return ResponseEntity.ok(brandService.getBrandById(id));
	}

	@GetMapping
	public ResponseEntity<List<BrandResponse>> getAll() {
		return ResponseEntity.ok(brandService.getAllBrands());
	}

	@PutMapping("/{id}")
	public ResponseEntity<BrandResponse> update(@PathVariable String id, @Valid @RequestBody BrandRequest request) {
		return ResponseEntity.ok(brandService.updateBrand(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable String id) {
		brandService.deleteBrand(id);
		return ResponseEntity.noContent().build();
	}
}
