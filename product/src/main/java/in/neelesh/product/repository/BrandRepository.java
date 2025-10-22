package in.neelesh.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.neelesh.common.entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String> {

	Optional<Brand> findByNameIgnoreCase(String name);

	boolean existsByNameIgnoreCase(String name);
}
