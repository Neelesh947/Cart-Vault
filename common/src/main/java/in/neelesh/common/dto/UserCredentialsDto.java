package in.neelesh.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserCredentialsDto {

	@NotBlank(message = "userName is required")
	@JsonProperty("userName")
	private String userName;

	@Pattern(regexp = "\\S+", message = "Password must not be blank if present")
	@JsonProperty("password")
	private String password;

	private String realm;

}
