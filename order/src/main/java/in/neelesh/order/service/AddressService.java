package in.neelesh.order.service;

import java.util.List;

import in.neelesh.order.dto.AddressRequestDto;
import in.neelesh.order.dto.AddressResponseDto;

public interface AddressService {

	AddressResponseDto createAddress(AddressRequestDto request, String userId, String realm);

	List<AddressResponseDto> getAllAddresses(String userId);

	AddressResponseDto getAddressById(String id, String userId);
	
	public void deleteAddress(String addressId, String userId);

	AddressResponseDto updateAddress(String id, AddressRequestDto request, String userId);

	void setDefaultAddress(String id, String userId);

}
