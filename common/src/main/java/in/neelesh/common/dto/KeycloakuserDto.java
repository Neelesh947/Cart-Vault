package in.neelesh.common.dto;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to represent Keycloak User data for creation or update.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeycloakuserDto {

	private String userId;
	private String username;
	private String email;
	private Boolean emailVerified;
	private Boolean enabled;
	private String firstName;
	private String lastName;

	// Credentials (e.g., password)
	private List<Credentials> credentials;

	private Map<String, List<String>> attributes;
	
	private String realm;
}
