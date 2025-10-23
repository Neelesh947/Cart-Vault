package in.neelesh.auth_proxy.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.util.InternalException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import in.neelesh.auth_proxy.config.KeycloakConfig;
import in.neelesh.auth_proxy.service.utils.Constants;
import in.neelesh.auth_proxy.service.utils.NotificationUtils;
import in.neelesh.common.dto.Credentials;
import in.neelesh.common.dto.KeycloakTokenResponseDto;
import in.neelesh.common.dto.KeycloakuserDto;
import in.neelesh.common.dto.UserCredentialsDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

	private final KeycloakConfig keycloakConfig;
	private final RestTemplate restTemplate;
	private final OTPService otpService;
	private final NotificationUtils notificationUtils;

	@Override
	public KeycloakTokenResponseDto loginUser(UserCredentialsDto userCredentialsDto, String realm) {
		String tokenUrl = keycloakConfig.getTokenUrl().replace("{0}", realm);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add(Constants.CLIENT_SECRET, keycloakConfig.getGetCredentialsSecret());
		body.add(Constants.CLIENT_ID, keycloakConfig.getKeycloakResource());
		body.add(Constants.GRANT_TYPE, Constants.PASSWORD);
		body.add(Constants.USERNAME, userCredentialsDto.getUserName());
		body.add(Constants.PASSWORD, userCredentialsDto.getPassword());

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
		try {
			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request,
					new ParameterizedTypeReference<Map<String, Object>>() {
					});
			Map<String, Object> responseBody = response.getBody();
			KeycloakTokenResponseDto tokenResponse = mapToTokenResponse(responseBody);
			return tokenResponse;
		} catch (Exception e) {
			throw new RuntimeException("Failed to authenticate with Keycloak: " + e.getMessage(), e);
		}
	}

	private KeycloakTokenResponseDto mapToTokenResponse(Map<String, Object> responseBody) {
		if (responseBody == null) {
			throw new RuntimeException("Empty or invalid response from Keycloak");
		}
		KeycloakTokenResponseDto tokenResponse = new KeycloakTokenResponseDto();
		tokenResponse.setAccessToken((String) responseBody.get("access_token"));
		tokenResponse.setExpiresIn((Integer) responseBody.get("expires_in"));
		tokenResponse.setRefreshExpiresIn((Integer) responseBody.get("refresh_expires_in"));
		tokenResponse.setRefreshToken((String) responseBody.get("refresh_token"));
		tokenResponse.setTokenType((String) responseBody.get("token_type"));
		tokenResponse.setNotBeforePolicy((Integer) responseBody.get("not-before-policy"));
		tokenResponse.setSessionState((String) responseBody.get("session_state"));
		tokenResponse.setScope((String) responseBody.get("scope"));
		return tokenResponse;
	}

	private ResponseEntity<KeycloakTokenResponseDto> getAdminTokenFromkeycloak() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add(Constants.USERNAME, keycloakConfig.getAdminUsername());
		map.add(Constants.PASSWORD, keycloakConfig.getAdminPassword());
		map.add(Constants.CLIENT_ID, keycloakConfig.getAdminClient());
		map.add(Constants.GRANT_TYPE, Constants.PASSWORD);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
		String tokenUrl = keycloakConfig.getTokenUrl().replace("{0}", "master");
		return restTemplate.postForEntity(tokenUrl, request, KeycloakTokenResponseDto.class);
	}

	@Override
	public KeycloakuserDto createKeycloakUsersAndAssignRoles(KeycloakuserDto keycloakuserDto, String realm,
			String roleName) {
		ResponseEntity<KeycloakTokenResponseDto> accessTokenResponse = getAdminTokenFromkeycloak();
		if (!accessTokenResponse.getStatusCode().is2xxSuccessful() || accessTokenResponse.getBody() == null) {
			throw new RuntimeException("Failed to get admin token from Keycloak");
		}
		String accessToken = accessTokenResponse.getBody().getAccessToken();
		String url = MessageFormat.format(keycloakConfig.getCreateUserURL(), realm);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(accessToken);

		Map<String, Object> userPayload = buildUserPayload(keycloakuserDto);
		HttpEntity<Map<String, Object>> createUserRequest = new HttpEntity<>(userPayload, headers);
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.postForEntity(url, createUserRequest, String.class);
		} catch (HttpClientErrorException e) {
			throw new RuntimeException(e.getMessage());
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong");
		}

		if (response.getStatusCode() == HttpStatus.CREATED) {
			ResponseEntity<?> userIdResponse = getUserIdByUserName(keycloakuserDto.getUsername(), accessToken, realm);
			String userId = userIdResponse.getBody().toString();
			Map<String, Object> roles = getRole(realm, roleName);
			String fetchedRoleName = (String) roles.get(Constants.NAME);
			assignRoleToUser(realm, userId, fetchedRoleName);
			keycloakuserDto.setUserId(userId);
			String otp = otpService.generateOtp(userId);
			notificationUtils.sendValidationEmail(keycloakuserDto, otp);
			System.out.println("OTP to enable user: - " + otp);
			return keycloakuserDto;
		} else {
			throw new RuntimeException("Failed to create user: " + response.getStatusCode());
		}
	}

	private Map<String, Object> buildUserPayload(KeycloakuserDto keycloakuserDto) {
		Map<String, Object> userPayload = new HashMap<>();
		userPayload.put(Constants.USERNAME, keycloakuserDto.getUsername());
		userPayload.put(Constants.EMAIL, keycloakuserDto.getEmail());
		userPayload.put(Constants.EMAIL_VERIFIED,
				Optional.ofNullable(keycloakuserDto.getEmailVerified()).orElse(false));
		userPayload.put(Constants.ENABLED, false);
		userPayload.put(Constants.FIRST_NAME, keycloakuserDto.getFirstName());
		userPayload.put(Constants.LAST_NAME, keycloakuserDto.getLastName());

		if (keycloakuserDto.getAttributes() != null && !keycloakuserDto.getAttributes().isEmpty()) {
			userPayload.put(Constants.ATTRIBUTES, keycloakuserDto.getAttributes());
		}

		if (keycloakuserDto.getCredentials() != null && !keycloakuserDto.getCredentials().isEmpty()) {
			List<Map<String, Object>> credentialList = new ArrayList<>();
			for (Credentials cred : keycloakuserDto.getCredentials()) {
				if (cred.getValue() != null && !cred.getValue().isEmpty()) {
					Map<String, Object> credentialMap = new HashMap<>();
					credentialMap.put(Constants.TYPE, Optional.ofNullable(cred.getType()).orElse(Constants.PASSWORD));
					credentialMap.put(Constants.VALUE, cred.getValue());
					credentialMap.put(Constants.TEMPORARY, Optional.ofNullable(cred.getTemporary()).orElse(false));
					credentialList.add(credentialMap);
				}
			}
			if (!credentialList.isEmpty()) {
				userPayload.put(Constants.CREDENTIALS, credentialList);
			}
		}

		return userPayload;
	}

	private ResponseEntity<?> getUserIdByUserName(String username, String token, String realm) {
		String url = MessageFormat.format(keycloakConfig.getUserIdByUsernameURL(), realm, username);
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Void> request = new HttpEntity<>(headers);
		try {
			ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(url, HttpMethod.GET, request,
					new ParameterizedTypeReference<List<Map<String, Object>>>() {
					});
			if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null
					|| response.getBody().isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
			}

			String userId = (String) response.getBody().get(0).get(Constants.ID);
			return ResponseEntity.ok(userId);
		} catch (HttpClientErrorException e) {
			return ResponseEntity.status(e.getStatusCode()).body("Failed to fetch user ID: " + e.getMessage());
		}
	}

	public Map<String, Object> getRole(String realm, String roleName) {
		ResponseEntity<KeycloakTokenResponseDto> accessTokenResponse = getAdminTokenFromkeycloak();
		if (!accessTokenResponse.getStatusCode().is2xxSuccessful() || accessTokenResponse.getBody() == null) {
			throw new IllegalStateException("Unable to retrieve admin token from Keycloak");
		}

		KeycloakTokenResponseDto token = accessTokenResponse.getBody();
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token.getAccessToken());
		headers.setContentType(MediaType.APPLICATION_JSON);

		String url = MessageFormat.format(keycloakConfig.getRoleByRoleNameURL(), realm, roleName);

		ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET,
				new HttpEntity<>(headers), new ParameterizedTypeReference<Map<String, Object>>() {
				});

		if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
			throw new IllegalStateException("Failed to fetch role: " + roleName);
		}
		return response.getBody();
	}

	private void assignRoleToUser(String realm, String userId, String fetchedRoleName) {
		try {
			ResponseEntity<KeycloakTokenResponseDto> accessTokenResponse = getAdminTokenFromkeycloak();
			if (!accessTokenResponse.getStatusCode().is2xxSuccessful() || accessTokenResponse.getBody() == null) {
				throw new IllegalStateException("Unable to retrieve admin token from Keycloak");
			}

			KeycloakTokenResponseDto token = accessTokenResponse.getBody();

			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(token.getAccessToken());
			headers.setContentType(MediaType.APPLICATION_JSON);

			String assignRoleUrl = MessageFormat.format(keycloakConfig.getAssignRoleToUserURL(), realm, userId);

			Map<String, Object> role = getRole(realm, fetchedRoleName);
			if (role == null) {
				throw new IllegalStateException("Role not found: " + fetchedRoleName);
			}

			List<Map<String, Object>> rolesToAssign = Collections.singletonList(role);
			HttpEntity<List<Map<String, Object>>> request = new HttpEntity<>(rolesToAssign, headers);

			restTemplate.exchange(assignRoleUrl, HttpMethod.POST, request, Void.class);

		} catch (Exception e) {
			throw new RuntimeException("Role assignment failed for userId: " + userId + " in realm: " + realm, e);
		}
	}

	public void verifyOTPandEnableUser(String otp, String userId, String realm) {
		boolean isValidOtp = otpService.verifyOtp(userId, otp);
		if (!isValidOtp) {
			throw new RuntimeException("Invalid OTP");
		}
		enableUserAfterOTPVerification(realm, userId);
	}

	private void enableUserAfterOTPVerification(String realm, String userId) {
		ResponseEntity<KeycloakTokenResponseDto> accessTokenResponse = getAdminTokenFromkeycloak();
		if (!accessTokenResponse.getStatusCode().is2xxSuccessful() || accessTokenResponse.getBody() == null) {
			throw new IllegalStateException("Unable to retrieve admin token from Keycloak");
		}

		String token = accessTokenResponse.getBody().getAccessToken();

		String url = MessageFormat.format(keycloakConfig.getEnableUserURL(), realm, userId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(token);

		Map<String, Object> payload = new HashMap<>();
		payload.put(Constants.ENABLED, true);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

		try {
			restTemplate.put(url, request);
			System.out.println("User " + userId + " enabled successfully.");
		} catch (HttpClientErrorException e) {
			throw new RuntimeException("Failed to enable user: " + e.getMessage());
		}
	}

	public void logoutUser(String userId, String realm) {
		ResponseEntity<KeycloakTokenResponseDto> accessTokenResponse = getAdminTokenFromkeycloak();
		if (!accessTokenResponse.getStatusCode().is2xxSuccessful() || accessTokenResponse.getBody() == null) {
			throw new IllegalStateException("Unable to retrieve admin token from Keycloak");
		}

		String token = accessTokenResponse.getBody().getAccessToken();
		String url = MessageFormat.format(keycloakConfig.getLogoutUserURL(), realm, userId);
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Void> request = new HttpEntity<>(headers);
		try {
			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, request, Void.class);
			if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
				throw new RuntimeException("Failed to logout user, status: " + response.getStatusCode());
			}
		} catch (HttpClientErrorException e) {
			throw new RuntimeException("Error during logout user: " + e.getResponseBodyAsString(), e);
		} catch (Exception e) {
			throw new RuntimeException("Unexpected error during logout user", e);
		}
	}

	public KeycloakuserDto getKeycloakUserById(String userId, String realm) {
		ResponseEntity<KeycloakTokenResponseDto> accessTokenResponse = getAdminTokenFromkeycloak();
		if (!accessTokenResponse.getStatusCode().is2xxSuccessful() || accessTokenResponse.getBody() == null) {
			throw new IllegalStateException("Unable to retrieve admin token from Keycloak");
		}

		String token = accessTokenResponse.getBody().getAccessToken();
		String url = MessageFormat.format(keycloakConfig.getKeycloakUserById(), realm, userId);
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Void> request = new HttpEntity<>(headers);

		try {
			ResponseEntity<KeycloakuserDto> response = restTemplate.exchange(url, HttpMethod.GET, request,
					KeycloakuserDto.class);
			return response.getBody();
		} catch (Exception e) {
			throw new InternalException("Some exception occured");
		}
	}
}
