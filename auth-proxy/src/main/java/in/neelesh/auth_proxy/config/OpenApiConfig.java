package in.neelesh.auth_proxy.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
		info = @Info(
				contact = @Contact(
						name = "Neelesh",
						email = "neeleshkumarm98@gmail.com",
						url = "https://www.linkedin.com/feed/?trk=guest_homepage-basic_nav-header-signin"
						),
				description = "OpenApi documentation for Spring Security",
				title = "Open Api Specification - Neelesh",
				version = "1.0",
				license = @License(
						name = "Neelesh License",
						url = "https://www.linkedin.com/feed/?trk=guest_homepage-basic_nav-header-signin"
						),
				termsOfService = "Terms of service"
				),
		servers = {
				@Server(
						description = "Local ENV",
						url = "http://localhost:1234"
						),
				@Server(
						description = "Production ENV",
						url = "http://neelesh.kumar.com/"
						),
		},
		security = {
				@SecurityRequirement(
						name = "bearerAuth"
						)
		}
		)
	@SecurityScheme(
			name = "bearerAuth",
			description = "JWT auth description",
	        scheme = "bearer",
	        type = SecuritySchemeType.OAUTH2
//	        flows = @OAuthFlows(
//	        		clientCredentials =
//	        		@OAuthFlow(
//	        				authorizationUrl = "http://localhost:9090/realms/book-social-network/protocol/openid-connect/auth"
//	        				)
//	        		),
//	        bearerFormat = "JWT",
//	        in = SecuritySchemeIn.HEADER
			)
public class OpenApiConfig {

}
