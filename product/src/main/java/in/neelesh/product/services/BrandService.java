package in.neelesh.product.services;

import java.util.List;

import in.neelesh.product.dto.BrandRequest;
import in.neelesh.product.dto.BrandResponse;

public interface BrandService {

	BrandResponse createBrand(BrandRequest brandRequest);

	BrandResponse getBrandById(String id);

	List<BrandResponse> getAllBrands();

	BrandResponse updateBrand(String id, BrandRequest brandRequest);

	void deleteBrand(String id);
}
