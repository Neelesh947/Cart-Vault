package in.neelesh.order.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponseDto {

	private String id;
	private String userId;
	private String fullName;
	private String line1;
	private String line2;
	private String city;
	private String state;
	private String country;
	private String postalCode;
	private String phone;
	private Boolean isDefault;
	private String type;
}
