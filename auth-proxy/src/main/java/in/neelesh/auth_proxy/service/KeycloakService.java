package in.neelesh.auth_proxy.service;

import in.neelesh.common.dto.KeycloakTokenResponseDto;
import in.neelesh.common.dto.KeycloakuserDto;
import in.neelesh.common.dto.UserCredentialsDto;

public interface KeycloakService {

	KeycloakTokenResponseDto loginUser(UserCredentialsDto userCredentialsDto, String realm);

	KeycloakuserDto createKeycloakUsersAndAssignRoles(KeycloakuserDto keycloakuserDto, String realm, String role);
	
	public void verifyOTPandEnableUser(String otp, String userId, String realm);
	
	public void logoutUser(String userId, String realm);

}
