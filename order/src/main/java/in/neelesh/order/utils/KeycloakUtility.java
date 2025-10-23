package in.neelesh.order.utils;

import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import in.neelesh.common.dto.KeycloakuserDto;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KeycloakUtility {

	RestTemplate restTemplate = new RestTemplate();

	public KeycloakuserDto getUserbyId(String userId, String realm) {
		String urlEndPoint = "http://localhost:1234/CartVault/user/" + userId;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

		ResponseEntity<KeycloakuserDto> response = restTemplate.exchange(urlEndPoint, HttpMethod.GET, requestEntity,
				KeycloakuserDto.class);

		if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
			return response.getBody();
		} else {
			throw new IllegalStateException("Failed to fetch user from internal service: " + response.getStatusCode());
		}
	}
}
