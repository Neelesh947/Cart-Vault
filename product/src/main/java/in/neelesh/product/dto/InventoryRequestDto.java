package in.neelesh.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRequestDto {

	@NotNull(message = "Product ID is required")
	private String productId;

	@NotNull(message = "Stock is required")
	private Integer stock;

	private String warehouse;
}
