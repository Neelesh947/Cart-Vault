package in.neelesh.auth_proxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class KeycloakConfig {

	@Value("${keycloak.token-url}")
	private String tokenUrl;
	
	@Value("${keycloak.resource}")
	private String keycloakResource;

	@Value("${keycloak.admin-client}")
	private String adminClient;

	@Value("${keycloak.admin-credentials.username}")
	private String adminUsername;

	@Value("${keycloak.admin-credentials.password}")
	private String adminPassword;
	
	@Value("${keycloak.credentials.secret}")
	private String getCredentialsSecret;
	
	@Value("${keycloak.create-user-url}")
	private String createUserURL;
	
	@Value("${keycloak.get-userid-by-username-url}")
	private String userIdByUsernameURL;
	
	@Value("${keycloak.get-role-by-roleName-url}")
	private String roleByRoleNameURL;
	
	@Value("${keycloak.assign-role-url}")
	private String assignRoleToUserURL;
	
	@Value("${keycloak.enable-keycloak-user-url}")
	private String enableUserURL;

	@Value("${keycloak.logout-user-url}")
	private String logoutUserURL;
	
	@Value("${keycloak.get-user-by-id}")
	private String KeycloakUserById;
}
