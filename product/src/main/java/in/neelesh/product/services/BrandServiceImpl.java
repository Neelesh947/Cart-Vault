package in.neelesh.product.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import in.neelesh.common.entity.Brand;
import in.neelesh.product.dto.BrandRequest;
import in.neelesh.product.dto.BrandResponse;
import in.neelesh.product.repository.BrandRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

	private final BrandRepository brandRepository;

	@Override
	public BrandResponse createBrand(BrandRequest brandRequest) {
		if (brandRepository.existsByNameIgnoreCase(brandRequest.getName())) {
			throw new IllegalArgumentException("Brnad with the smae name already exists");
		}
		Brand brand = Brand.builder().name(brandRequest.getName()).build();
		Brand saved = brandRepository.save(brand);
		return mapToResponse(saved);
	}

	@Override
	public BrandResponse getBrandById(String id) {
		Brand brand = brandRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Brand not found with ID: " + id));
		return mapToResponse(brand);
	}

	@Override
	public List<BrandResponse> getAllBrands() {
		return brandRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	public BrandResponse updateBrand(String id, BrandRequest brandRequest) {
		Brand brand = brandRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Brand not found with ID: " + id));
		brand.setName(brandRequest.getName());
		Brand updated = brandRepository.save(brand);
		return mapToResponse(updated);
	}

	@Override
	public void deleteBrand(String id) {
		if (!brandRepository.existsById(id)) {
			throw new EntityNotFoundException("Brand not found with ID: " + id);
		}
		brandRepository.deleteById(id);
	}

	private BrandResponse mapToResponse(Brand brand) {
		return BrandResponse.builder().id(brand.getId()).name(brand.getName()).build();
	}

}
