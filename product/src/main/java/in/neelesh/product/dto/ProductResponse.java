package in.neelesh.product.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
	
	private String id;
	private String name;
	private String createdBy;
	private String description;
	private BigDecimal price;
	private String sku;
	private String imageUrl;
	private Float rating;
	private String status;

	private String categoryId;
	private String categoryName;

	private String brandId;
	private String brandName;
	private Integer productStock;
	private String warehouseName;
	
}
