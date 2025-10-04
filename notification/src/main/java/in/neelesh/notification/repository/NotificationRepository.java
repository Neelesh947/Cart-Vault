package in.neelesh.notification.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.neelesh.common.entity.Notification;
import in.neelesh.common.enums.EventType;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

	Optional<Notification> findByCustomerIdAndEventTypeAndRecipient(String customerId, EventType eventType,
			String recipient);
}
