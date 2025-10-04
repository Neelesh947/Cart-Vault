package in.neelesh.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.neelesh.common.dto.NotificationRequest;
import in.neelesh.notification.services.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/{realm}/notification")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification")
public class NotificationController {

	private final NotificationService notificationService;

	@PostMapping
	public ResponseEntity<String> processNotification(@RequestBody NotificationRequest notificationRequest,
			@PathVariable String realm) {
		log.info("Received notification request: {}", notificationRequest);

		try {
			notificationService.processNotification(notificationRequest);
			return ResponseEntity.ok("Notification is being processed");
		} catch (Exception e) {
			log.error("Error processing notification", e);
			return ResponseEntity.status(500).body("Failed to process notification");
		}
	}

}
