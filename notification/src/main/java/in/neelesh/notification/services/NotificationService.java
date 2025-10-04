package in.neelesh.notification.services;

import in.neelesh.common.dto.NotificationRequest;

public interface NotificationService {

	public void processNotification(NotificationRequest notificationRequest);
}
