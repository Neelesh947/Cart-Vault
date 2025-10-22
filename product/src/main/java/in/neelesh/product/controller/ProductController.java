package in.neelesh.product.controller;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.neelesh.product.dto.ProductRequest;
import in.neelesh.product.dto.ProductResponse;
import in.neelesh.product.services.ProductService;
import in.neelesh.product.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/{realm}/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@PostMapping
	@PreAuthorize("hasRole('VENDOR')")
	public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		ProductResponse created = productService.createProduct(productRequest, userId);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ProductResponse> getProductById(@PathVariable String productId) {
		ProductResponse response = productService.getProductById(productId);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<List<ProductResponse>> getAllProducts() {
		List<ProductResponse> response = productService.getAllProducts();
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProductResponse> updateProduct(@PathVariable String id,
			@Valid @RequestBody ProductRequest request) {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		ProductResponse updated = productService.updateProduct(id, request, userId);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		productService.deleteProduct(id, userId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/search")
	public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam("q") String keyword) {
		List<ProductResponse> found = productService.searchProducts(keyword);
		return ResponseEntity.ok(found);
	}

	@GetMapping("/status/{status}")
	public ResponseEntity<List<ProductResponse>> getByStatus(@PathVariable String status) {
		List<ProductResponse> list = productService.getProductsByStatus(status);
		return ResponseEntity.ok(list);
	}

	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<ProductResponse>> getByCategory(@PathVariable String categoryId) {
		List<ProductResponse> list = productService.getProductsByCategory(categoryId);
		return ResponseEntity.ok(list);
	}

	@GetMapping("/brand/{brandId}")
	public ResponseEntity<List<ProductResponse>> getByBrand(@PathVariable String brandId) {
		List<ProductResponse> list = productService.getProductsByBrand(brandId);
		return ResponseEntity.ok(list);
	}

	@PatchMapping("/{id}/price")
	public ResponseEntity<Void> updatePrice(@PathVariable String id, @RequestParam("price") BigDecimal newPrice) {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		productService.updateProductPrice(id, newPrice, userId);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}/stock")
	public ResponseEntity<Void> updateStock(@PathVariable String id,
			@RequestParam("quantityChange") Integer quantityChange) {
		String userId = SecurityUtils.getCurrentUserIdSupplier.get();
		productService.updateProductStock(id, quantityChange, userId);
		return ResponseEntity.noContent().build();
	}
}
