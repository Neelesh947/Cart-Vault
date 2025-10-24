package in.neelesh.order.utils;

import java.math.BigDecimal;
import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import in.neelesh.order.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductUtility {

	RestTemplate restTemplate = new RestTemplate();

	public ProductResponseDto getProductDetails(String productId, String realm) {
		String urlEndPoint = "http://localhost:1236/" + realm + "/products/" + productId;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<ProductResponseDto> response = restTemplate.exchange(urlEndPoint, HttpMethod.GET, requestEntity,
				ProductResponseDto.class);
		if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
			return response.getBody();
		} else {
			throw new IllegalStateException(
					"Failed to fetch product from internal service: " + response.getStatusCode());
		}
	}

	public BigDecimal getProductPrice(String productId) {
		String urlEndpoint = "http://localhost:1236/cartvault/products/getProductPrice/" + productId;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<BigDecimal> response = restTemplate.exchange(urlEndpoint, HttpMethod.GET, requestEntity,
				BigDecimal.class);
		if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
			return response.getBody();
		} else {
			throw new IllegalStateException(
					"Failed to fetch product from internal service: " + response.getStatusCode());
		}
	}
}
