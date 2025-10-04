package in.neelesh.notification.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import in.neelesh.common.dto.NotificationRequest;
import in.neelesh.common.entity.Notification;
import in.neelesh.common.enums.NotificationStatus;
import in.neelesh.notification.repository.NotificationRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

	@Value("${sender.information.name}")
	private String senderInformation;

	private final NotificationRepository notificationRepository;
	private final JavaMailSender javaMailSender;
	private final TemplateEngine templateEngine;

	private static final int MAX_EMAIL_RETRY_ATTEMPTS = 3;

	@Async
	public void processNotification(NotificationRequest notificationRequest) {
		log.info("Sending notification with the body: {}", notificationRequest);

		try {
			String emailContent = prepareAndSendEmail(notificationRequest);

			log.info("Notification sent successfully to {}", notificationRequest.getRecipient());

			// Save notification with SENT status and retryCount = 0
			createOrUpdateNotification(notificationRequest, emailContent, NotificationStatus.DELIVERED, 0);

		} catch (MessagingException e) {
			log.error("Failed to send notification", e);

			// Handle retry on failure
			handleFailedNotification(notificationRequest);
		}
	}

	private String prepareAndSendEmail(NotificationRequest notificationRequest) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

		helper.setTo(notificationRequest.getRecipient());
		helper.setFrom(senderInformation);
		helper.setSubject(notificationRequest.getSubject());

		Context context = new Context();
		context.setVariables(notificationRequest.getTemplateModel());

		String emailContent = templateEngine.process(notificationRequest.getTemplateName(), context);

		helper.setText(emailContent, true);

		javaMailSender.send(mimeMessage);

		return emailContent;
	}

	private void createOrUpdateNotification(NotificationRequest notificationRequest, String emailContent,
			NotificationStatus status, int retryCount) {
		Notification notification = notificationRepository
				.findByCustomerIdAndEventTypeAndRecipient(notificationRequest.getClientId(),
						notificationRequest.getEventType(), notificationRequest.getRecipient())
				.orElse(new Notification());

		notification.setCustomerId(notificationRequest.getClientId());
		notification.setEventType(notificationRequest.getEventType());
		notification.setChannel(notificationRequest.getChannel());
		notification.setRecipient(notificationRequest.getRecipient());
		notification.setStatus(status);
		notification.setMessage(emailContent);
		notification.setRetryCount(retryCount);

		notificationRepository.save(notification);
	}

	private void handleFailedNotification(NotificationRequest notificationRequest) {
		notificationRepository
				.findByCustomerIdAndEventTypeAndRecipient(notificationRequest.getClientId(),
						notificationRequest.getEventType(), notificationRequest.getRecipient())
				.ifPresentOrElse(notification -> {
					int currentRetry = notification.getRetryCount();

					if (currentRetry < MAX_EMAIL_RETRY_ATTEMPTS) {
						notification.setRetryCount(currentRetry + 1);
						notification.setStatus(NotificationStatus.RETRYING);
						notificationRepository.save(notification);

						log.info("Retry attempt {} for notification to {}", currentRetry + 1,
								notificationRequest.getRecipient());

						try {
							String emailContent = prepareAndSendEmail(notificationRequest);
							log.info("Retry successful for notification to {}", notificationRequest.getRecipient());

							notification.setStatus(NotificationStatus.SENT);
							notification.setRetryCount(0);
							notification.setMessage(emailContent);
							notificationRepository.save(notification);
						} catch (MessagingException e) {
							log.error("Retry attempt {} failed for notification to {}", currentRetry + 1,
									notificationRequest.getRecipient(), e);

							// Mark as retrying; actual retry control is managed by retry count
							notification.setStatus(NotificationStatus.RETRYING);
							notificationRepository.save(notification);
						}

					} else {
						notification.setStatus(NotificationStatus.FAILED);
						notificationRepository.save(notification);

						log.warn("Max retry attempts reached. Notification to {} marked as FAILED",
								notificationRequest.getRecipient());
					}
				}, () -> {
					log.info("Saving new failed notification for {}", notificationRequest.getRecipient());
					createOrUpdateNotification(notificationRequest, null, NotificationStatus.FAILED, 0);
				});
	}
}