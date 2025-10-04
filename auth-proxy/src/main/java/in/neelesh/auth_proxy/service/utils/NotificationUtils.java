package in.neelesh.auth_proxy.service.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import in.neelesh.common.dto.KeycloakuserDto;
import in.neelesh.common.enums.EventType;
import in.neelesh.common.enums.NotificationChannel;

@Component
public class NotificationUtils {

	private final RestTemplate restTemplate;

	private static final String URL = "http://localhost:1235/CartVault/notification";

	public NotificationUtils(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Async
	public void sendValidationEmail(KeycloakuserDto keycloakuserDto, String otp) {
		Map<String, Object> requestBody = prepareRequestBody(keycloakuserDto, otp);
		sendNotificationRequest(requestBody);
	}

	private void sendNotificationRequest(Map<String, Object> requestBody) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(URL, request, String.class);
			System.out.println("Notification sent. Status: " + response.getStatusCode());
		} catch (Exception e) {
			System.err.println("Failed to send notification: " + e.getMessage());
		}
	}

	private Map<String, Object> prepareRequestBody(KeycloakuserDto keycloakuserDto, String otp) {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put(Constants.NOTIFICATION_CLIENT_ID, keycloakuserDto.getUserId());
		requestBody.put(Constants.RECIPIENT, keycloakuserDto.getEmail());
		requestBody.put(Constants.SUBJECT, "Activate Your Account");
		requestBody.put(Constants.TEMPLATE_NAME, "AccountCreation.html");
		requestBody.put(Constants.EVENT_TYPE, EventType.ACCOUNT_ACTIVATION);
		requestBody.put(Constants.CHANNEL, NotificationChannel.EMAIL);

		String name = keycloakuserDto.getFirstName() + " " + keycloakuserDto.getLastName();

		Map<String, Object> templateModel = new HashMap<>();
		templateModel.put(Constants.CUSTOMER_NAME, name);
		templateModel.put(Constants.OTP, otp);

		requestBody.put(Constants.TEMPLATE_MODEL, templateModel);
		return requestBody;
	}

}
