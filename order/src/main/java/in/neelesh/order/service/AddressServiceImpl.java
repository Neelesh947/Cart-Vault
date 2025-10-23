package in.neelesh.order.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import in.neelesh.common.dto.KeycloakuserDto;
import in.neelesh.common.entity.Address;
import in.neelesh.common.entity.User;
import in.neelesh.common.enums.AddressType;
import in.neelesh.order.dto.AddressRequestDto;
import in.neelesh.order.dto.AddressResponseDto;
import in.neelesh.order.repository.AddressRepository;
import in.neelesh.order.repository.UserRepository;
import in.neelesh.order.utils.KeycloakUtility;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

	private final AddressRepository addressRepository;
	private final UserRepository userRepository;
	private final KeycloakUtility keycloakUtility;

	@Override
	public AddressResponseDto createAddress(AddressRequestDto request, String userId, String realm) {
		KeycloakuserDto userDto = keycloakUtility.getUserbyId(userId, realm);
		if (userDto == null) {
			throw new IllegalArgumentException("User not found in Keycloak");
		}
		Optional<User> optionalUser = userRepository.findByUserId(userId);
		User keycloakUser = new User();
		if (!optionalUser.isPresent()) {
			keycloakUser.setUserId(userId);
			keycloakUser.setName(userDto.getFirstName() + " " + userDto.getLastName());
			keycloakUser.setEmail(userDto.getEmail());

			String phoneNumber = null;
			if (userDto.getAttributes() != null && userDto.getAttributes().containsKey("phoneNumber")) {
				List<String> phoneList = userDto.getAttributes().get("phoneNumber");
				if (phoneList != null && !phoneList.isEmpty()) {
					phoneNumber = phoneList.get(0);
				}
			}
			keycloakUser.setPhone(phoneNumber);
			keycloakUser = userRepository.save(keycloakUser);
		} else {
			keycloakUser = optionalUser.get();
		}
		AddressType addressType;
		try {
			addressType = AddressType.valueOf(request.getType().toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new IllegalArgumentException("Invalid address type: " + request.getType());
		}

		Optional<Address> existingAddress = addressRepository.findByUserAndType(keycloakUser, addressType);
		if (existingAddress.isPresent()) {
			throw new IllegalArgumentException("Address of type '" + addressType + "' already exists for this user.");
		}

		Address address = Address.builder().user(keycloakUser).fullName(request.getFullName()).line1(request.getLine1())
				.line2(request.getLine2()).city(request.getCity()).state(request.getState())
				.country(request.getCountry()).postalCode(request.getPostalCode()).phone(request.getPhone())
				.isDefault(request.getIsDefault()).type(addressType).build();

		Address saved = addressRepository.save(address);

		return mapToDto(saved);
	}

	private AddressResponseDto mapToDto(Address address) {
		return AddressResponseDto.builder().id(address.getId()).userId(address.getUser().getUserId())
				.fullName(address.getFullName()).line1(address.getLine1()).line2(address.getLine2())
				.city(address.getCity()).state(address.getState()).country(address.getCountry())
				.postalCode(address.getPostalCode()).phone(address.getPhone()).isDefault(address.getIsDefault())
				.type(address.getType().name()).build();
	}

	@Override
	public List<AddressResponseDto> getAllAddresses(String userId) {
		Optional<User> userOptional = userRepository.findByUserId(userId);
		if (!userOptional.isPresent()) {
			throw new IllegalArgumentException(
					"No user found in the database with the provided user ID. Please create the user or save an address for the user first.");
		}
		User user = userOptional.get();
		return addressRepository.findByUser(user).stream().map(this::mapToDto).collect(Collectors.toList());
	}

	@Override
	public AddressResponseDto getAddressById(String id, String userId) {
		Address address = addressRepository.findById(id).filter(a -> a.getUser().getUserId().equals(userId))
				.orElseThrow(() -> new EntityNotFoundException("Address not found or unauthorized"));
		return mapToDto(address);
	}

	public void deleteAddress(String addressId, String userId) {
		Address address = addressRepository.findById(addressId).filter(a -> a.getUser().getUserId().equals(userId))
				.orElseThrow(() -> new EntityNotFoundException("Address not found or unauthorized"));
		addressRepository.delete(address);
	}

	@Override
	public AddressResponseDto updateAddress(String addressId, AddressRequestDto request, String userId) {
		Address address = addressRepository.findById(addressId).filter(a -> a.getUser().getUserId().equals(userId))
				.orElseThrow(() -> new EntityNotFoundException("Address not found or unauthorized"));
		address.setFullName(request.getFullName());
		address.setLine1(request.getLine1());
		address.setLine2(request.getLine2());
		address.setCity(request.getCity());
		address.setState(request.getState());
		address.setCountry(request.getCountry());
		address.setPostalCode(request.getPostalCode());
		address.setPhone(request.getPhone());
		address.setIsDefault(request.getIsDefault());
		address.setType(AddressType.valueOf(request.getType().toUpperCase()));

		Address updated = addressRepository.save(address);
		return mapToDto(updated);
	}

	@Override
	public void setDefaultAddress(String addressId, String userId) {
		Optional<User> userOptional = userRepository.findByUserId(userId);
		if (!userOptional.isPresent()) {
			throw new IllegalArgumentException(
					"No user found in the database with the provided user ID. Please create the user or save an address for the user first.");
		}
		User user = userOptional.get();
		List<Address> userAddress = addressRepository.findByUser(user);
		for (Address address : userAddress) {
	        if (address.getIsDefault()) {
	            address.setIsDefault(false);
	            addressRepository.save(address);
	        }
	    }
		Address address = addressRepository.findById(addressId).filter(a -> a.getUser().getUserId().equals(userId))
				.orElseThrow(() -> new EntityNotFoundException("Address not found or unauthorized"));
		address.setIsDefault(true);
	    addressRepository.save(address);
	}
}
