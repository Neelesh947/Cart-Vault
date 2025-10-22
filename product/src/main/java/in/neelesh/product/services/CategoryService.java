package in.neelesh.product.services;

import java.util.List;

import in.neelesh.product.dto.CategoryRequest;
import in.neelesh.product.dto.CategoryResponse;

public interface CategoryService {

	CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse getCategoryById(String id);

    List<CategoryResponse> getAllCategories();

    CategoryResponse updateCategory(String id, CategoryRequest request);

    void deleteCategory(String id);
}
