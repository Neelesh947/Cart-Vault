package in.neelesh.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.neelesh.common.entity.Product;
import in.neelesh.common.enums.ProductStatus;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>{

	List<Product> findByStatus(ProductStatus status);
    List<Product> findByNameContainingIgnoreCase(String keyword);

    boolean existsBySku(String sku);
    List<Product> findByBrand_Id(String brandId);
    List<Product> findByCategory_Id(String categoryId);
}
