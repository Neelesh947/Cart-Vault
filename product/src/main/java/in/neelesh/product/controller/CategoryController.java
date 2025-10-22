package in.neelesh.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.neelesh.product.dto.CategoryRequest;
import in.neelesh.product.dto.CategoryResponse;
import in.neelesh.product.services.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/{realm}/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	@PostMapping
	public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
		return ResponseEntity.ok(categoryService.createCategory(request));
	}

	@GetMapping("/{id}")
	public ResponseEntity<CategoryResponse> getById(@PathVariable String id) {
		return ResponseEntity.ok(categoryService.getCategoryById(id));
	}

	@GetMapping
	public ResponseEntity<List<CategoryResponse>> getAll() {
		return ResponseEntity.ok(categoryService.getAllCategories());
	}

	@PutMapping("/{id}")
	public ResponseEntity<CategoryResponse> update(@PathVariable String id,
			@Valid @RequestBody CategoryRequest request) {
		return ResponseEntity.ok(categoryService.updateCategory(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable String id) {
		categoryService.deleteCategory(id);
		return ResponseEntity.noContent().build();
	}
}
