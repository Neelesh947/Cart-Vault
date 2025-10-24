package in.neelesh.product.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.neelesh.common.entity.Brand;
import in.neelesh.common.entity.Category;
import in.neelesh.common.entity.InventoryItem;
import in.neelesh.common.entity.Product;
import in.neelesh.common.enums.ProductStatus;
import in.neelesh.product.dto.InventoryRequestDto;
import in.neelesh.product.dto.ProductRequest;
import in.neelesh.product.dto.ProductResponse;
import in.neelesh.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final BrandService brandService;
	private final CategoryService categoryService;
	private final InventoryService inventoryService;

	@Override
	@Transactional
	public ProductResponse createProduct(ProductRequest request, String createdBy) {
		if (productRepository.existsBySku(request.getSku())) {
			throw new IllegalArgumentException("Product with SKU already exists: " + request.getSku());
		}
		var brandResp = brandService.getBrandById(request.getBrandId());
		var categoryResp = categoryService.getCategoryById(request.getCategoryId());

		Product product = Product.builder().name(request.getName()).createdBy(createdBy)
				.description(request.getDescription()).price(request.getPrice()).sku(request.getSku())
				.imageUrl(request.getImageUrl()).rating(0.0f)
				.status(ProductStatus.valueOf(request.getStatus().toUpperCase())).brand(new Brand(brandResp.getId()))
				.category(new Category(categoryResp.getId())).build();
		Product saved = productRepository.save(product);

		InventoryRequestDto InventoryRequest = InventoryRequestDto.builder().productId(saved.getId())
				.stock(request.getProductStock()).warehouse(request.getWarehouseName()).build();
		inventoryService.createInventoryItems(InventoryRequest);
		return mapToResponse(saved);
	}

	@Override
	public ProductResponse getProductById(String id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Product not found with id " + id));
		return mapToResponse(product);
	}

	@Override
	public List<ProductResponse> getAllProducts() {
		return productRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	public ProductResponse updateProduct(String productId, ProductRequest request, String userId) {
		validateProductAssociationWithUser(productId, userId);
		Product existing = productRepository.findById(productId)
				.orElseThrow(() -> new EntityNotFoundException("Product not found with id " + productId));
		if (!existing.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku())) {
			throw new IllegalArgumentException("Another product with SKU exists: " + request.getSku());
		}
		var brandResp = brandService.getBrandById(request.getBrandId());
		var categoryResp = categoryService.getCategoryById(request.getCategoryId());

		existing.setName(request.getName());
		existing.setDescription(request.getDescription());
		existing.setPrice(request.getPrice());
		existing.setSku(request.getSku());
		existing.setImageUrl(request.getImageUrl());
		existing.setStatus(ProductStatus.valueOf(request.getStatus().toUpperCase()));
		existing.setBrand(new Brand(brandResp.getId()));
		existing.setCategory(new Category(categoryResp.getId()));
		Product updated = productRepository.save(existing);
		return mapToResponse(updated);
	}

	@Override
	public void deleteProduct(String productId, String userId) {
		validateProductAssociationWithUser(productId, userId);
		if (!productRepository.existsById(productId)) {
			throw new EntityNotFoundException("Product not found with id " + productId);
		}
		productRepository.deleteById(productId);
	}

	@Override
	public List<ProductResponse> searchProducts(String keyword) {
		return productRepository.findByNameContainingIgnoreCase(keyword).stream().map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public List<ProductResponse> getProductsByStatus(String status) {
		ProductStatus ps;
		try {
			ps = ProductStatus.valueOf(status.toUpperCase());
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid status: " + status);
		}
		return productRepository.findByStatus(ps).stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	public List<ProductResponse> getProductsByCategory(String categoryId) {
		categoryService.getCategoryById(categoryId);
		return productRepository.findByCategory_Id(categoryId).stream().map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public List<ProductResponse> getProductsByBrand(String brandId) {
		brandService.getBrandById(brandId);
		return productRepository.findByBrand_Id(brandId).stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	public void updateProductPrice(String productId, BigDecimal newPrice, String userId) {
		validateProductAssociationWithUser(productId, userId);
		if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Price must be positive");
		}
		Product existing = productRepository.findById(productId)
				.orElseThrow(() -> new EntityNotFoundException("Product not found with id " + productId));
		existing.setPrice(newPrice);
		productRepository.save(existing);
	}

	@Override
	public void updateProductStock(String productId, Integer quantityChange, String userId) {
		if (quantityChange == null) {
			throw new IllegalArgumentException("Quantity change is required");
		}
		validateProductAssociationWithUser(productId, userId);
		InventoryItem inventory = inventoryService.getByProductId(productId);
		int updatedStock = inventory.getStock() + quantityChange;
		if (updatedStock < 0) {
			throw new IllegalArgumentException(
					"Insufficient amount to decrease stock. Current stock: -" + inventory.getStock());
		}
		inventory.setStock(updatedStock);
		inventoryService.save(inventory);
	}

	private void validateProductAssociationWithUser(String productId, String userId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new EntityNotFoundException("Product not found with id " + productId));
		if (!product.getCreatedBy().equals(userId)) {
			throw new SecurityException("User is not authotized to modify this product");
		}
	}

	private ProductResponse mapToResponse(Product product) {
		InventoryItem inventory = inventoryService.getByProductId(product.getId());
		return ProductResponse.builder().id(product.getId()).name(product.getName()).createdBy(product.getCreatedBy())
				.description(product.getDescription()).price(product.getPrice()).sku(product.getSku())
				.imageUrl(product.getImageUrl()).rating(product.getRating()).status(product.getStatus().name())
				.categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
				.categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
				.productStock(inventory != null ? inventory.getStock() : null)
				.warehouseName(inventory != null ? inventory.getWarehouse() : null).build();
	}

	@Override
	public BigDecimal getProductPriceByProductId(String productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new EntityNotFoundException("Product not found with id " + productId));
		BigDecimal price = product.getPrice();
		return price;
	}
}
