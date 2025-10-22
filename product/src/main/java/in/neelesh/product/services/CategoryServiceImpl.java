package in.neelesh.product.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import in.neelesh.common.entity.Category;
import in.neelesh.product.dto.CategoryRequest;
import in.neelesh.product.dto.CategoryResponse;
import in.neelesh.product.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	@Override
	public CategoryResponse createCategory(CategoryRequest request) {
		Category category = Category.builder().name(request.getName()).slug(request.getSlug()).build();

		if (request.getParentId() != null) {
			Category parent = categoryRepository.findById(request.getParentId())
					.orElseThrow(() -> new EntityNotFoundException("Parent category not found"));
			category.setParent(parent);
		}
		Category saved = categoryRepository.save(category);
		return mapToResponse(saved);
	}

	@Override
	public CategoryResponse getCategoryById(String id) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + id));
		return mapToResponse(category);
	}

	@Override
	public List<CategoryResponse> getAllCategories() {
		List<Category> categories = categoryRepository.findAll();
		return categories.stream().filter(c -> c.getParent() == null).map(this::mapToResponseWithChildren)
				.collect(Collectors.toList());
	}

	@Override
	public CategoryResponse updateCategory(String id, CategoryRequest request) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Category not found"));

		category.setName(request.getName());
		category.setSlug(request.getSlug());

		if (request.getParentId() != null) {
			Category parent = categoryRepository.findById(request.getParentId())
					.orElseThrow(() -> new EntityNotFoundException("Parent category not found"));
			category.setParent(parent);
		} else {
			category.setParent(null);
		}

		Category updated = categoryRepository.save(category);
		return mapToResponse(updated);
	}

	@Override
	public void deleteCategory(String id) {
		if (!categoryRepository.existsById(id)) {
			throw new EntityNotFoundException("Category not found");
		}
		categoryRepository.deleteById(id);
	}

	private CategoryResponse mapToResponse(Category category) {
		return CategoryResponse.builder().id(category.getId()).name(category.getName()).slug(category.getSlug())
				.parentId(category.getParent() != null ? category.getParent().getId() : null).build();
	}

	private CategoryResponse mapToResponseWithChildren(Category category) {
		List<CategoryResponse> children = category.getChildren() != null
				? category.getChildren().stream().map(this::mapToResponseWithChildren).collect(Collectors.toList())
				: List.of();
		return CategoryResponse.builder().id(category.getId()).name(category.getName()).slug(category.getSlug())
				.parentId(category.getParent() != null ? category.getParent().getId() : null).children(children)
				.build();
	}
}
