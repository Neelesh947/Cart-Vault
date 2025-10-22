package in.neelesh.product.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

	@NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @NotBlank(message = "SKU is required")
    private String sku;
    
    @NotNull(message = "Product stock is required")
    @Min(value = 1, message = "Product stock must be at least 1")
    private Integer productStock;
    
    private String warehouseName;

    private String imageUrl;

    @NotNull(message = "Status is required")
    private String status;  // You can use ProductStatus enum string

    @NotNull(message = "Category ID is required")
    private String categoryId;

    @NotNull(message = "Brand ID is required")
    private String brandId;
}
