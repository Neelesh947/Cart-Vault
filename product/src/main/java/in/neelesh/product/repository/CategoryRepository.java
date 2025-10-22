package in.neelesh.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.neelesh.common.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String>{

	List<Category> findByParentId(String parentId);
	
	boolean existsByNameIgnoreCase(String name);
}
