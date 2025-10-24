package in.neelesh.product.services;

import java.math.BigDecimal;
import java.util.List;

import in.neelesh.product.dto.ProductRequest;
import in.neelesh.product.dto.ProductResponse;

public interface ProductService {

	ProductResponse createProduct(ProductRequest request, String createdby);

	ProductResponse getProductById(String id);

	List<ProductResponse> getAllProducts();

	ProductResponse updateProduct(String id, ProductRequest request, String userId);

	void deleteProduct(String id, String userId);

	List<ProductResponse> searchProducts(String keyword);

	List<ProductResponse> getProductsByStatus(String status);

	List<ProductResponse> getProductsByCategory(String categoryId);

	List<ProductResponse> getProductsByBrand(String brandId);

	void updateProductPrice(String id, java.math.BigDecimal newPrice, String userId);

	void updateProductStock(String productId, Integer quantityChange, String userId);

	BigDecimal getProductPriceByProductId(String productId);
}
