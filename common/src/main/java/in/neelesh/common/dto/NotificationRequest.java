package in.neelesh.common.dto;

import java.util.Map;

import in.neelesh.common.enums.EventType;
import in.neelesh.common.enums.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequest {

    @NotBlank(message = "Client ID is required")
    private String clientId;			// the vendor email for which the customer belongs to

    @NotNull(message = "Event type is required")
    private EventType eventType;

    @NotNull(message = "Notification channel is required")
    private NotificationChannel channel;

    @NotBlank(message = "Recipient is required")
    private String recipient; // customer email
    
    private String subject;

    private String templateName;
    
    private Map<String, Object> templateModel; //dynamic data
}
