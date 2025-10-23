package in.neelesh.auth_proxy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.neelesh.auth_proxy.service.KeycloakService;
import in.neelesh.common.dto.KeycloakTokenResponseDto;
import in.neelesh.common.dto.KeycloakuserDto;
import in.neelesh.common.dto.UserCredentialsDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/{realm}")
@Tag(name = "keycloak")
public class KeycloakController {

	private final KeycloakService keycloakService;

	@PostMapping("/login")
	public KeycloakTokenResponseDto userLogin(@RequestBody UserCredentialsDto userCredentialsDto,
			@PathVariable String realm) {
		userCredentialsDto.setRealm(realm);
		KeycloakTokenResponseDto response = keycloakService.loginUser(userCredentialsDto, realm);
		return response;
	}

	@PostMapping("/create-user/{role}")
	public ResponseEntity<?> createUserInKeycloak(@RequestBody KeycloakuserDto keycloakuserDto,
			@PathVariable String realm, @PathVariable String role) {
		KeycloakuserDto users = keycloakService.createKeycloakUsersAndAssignRoles(keycloakuserDto, realm, role);
		return ResponseEntity.status(HttpStatus.CREATED).body(users);
	}

	@PostMapping("/{userId}/verify-otp")
	public ResponseEntity<String> verifyOTPandEnableUser(@PathVariable String userId, @RequestParam String otp,
			@PathVariable String realm) {
		try {
			keycloakService.verifyOTPandEnableUser(otp, userId, realm);
			return ResponseEntity.ok("User enabled successfully after OTP verification");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/users/{userId}/logout")
	public ResponseEntity<String> logoutUser(@PathVariable String realm, @PathVariable String userId) {
		try {
			keycloakService.logoutUser(userId, realm);
			return ResponseEntity.ok("User logged out successfully");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to logout user: " + e.getMessage());
		}
	}
	
	@GetMapping("/user/{userId}")
	public ResponseEntity<?> getKeycloakUserById(@NotBlank(message = "User Id must not be emtpty or blank") @PathVariable String userId,
			@NotBlank(message = "Realm must not be empty or blank") @PathVariable String realm){
		try {
			KeycloakuserDto getUser = keycloakService.getKeycloakUserById(userId, realm);
			return ResponseEntity.status(HttpStatus.OK).body(getUser);
		} catch (Exception e) {
			throw e;
		}
	}
}
