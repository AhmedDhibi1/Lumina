package com.snapshot.lumina.businesslogic.infrastructure.event.publisher;

import com.snapshot.lumina.businesslogic.domain.event.DomainEvent;
import com.snapshot.lumina.businesslogic.domain.event.eventpublisher.DomainEventPublisher;
import com.snapshot.lumina.businesslogic.domain.event.workspace.WorkspaceCreatedEvent;
import com.snapshot.lumina.businesslogic.infrastructure.event.dto.WorkspaceCreatedEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class KafkaDomainEventPublisher implements DomainEventPublisher {

    private static final String WORKSPACE_CREATED_TOPIC = "workspace.created";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaDomainEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public <T extends DomainEvent> void publish(T event) {
        log.debug("Publishing domain event: {} with ID: {}",
                event.getEventType(), event.getEventId());

        try {
            if (event instanceof WorkspaceCreatedEvent workspaceCreatedEvent) {
                publishWorkspaceCreated(workspaceCreatedEvent);
            } else {
                log.warn("Unsupported event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Failed to publish domain event: {}", event.getEventType(), e);
            throw new RuntimeException("Failed to publish domain event", e);
        }
    }

    private void publishWorkspaceCreated(WorkspaceCreatedEvent event) {
        WorkspaceCreatedEventDto dto = WorkspaceCreatedEventDto.builder()
                .eventId(event.getEventId())
                .workspaceId(event.getWorkspaceId().getValue())
                .workspaceName(event.getWorkspaceName().getValue())
                .createdBy(event.getCreatedBy().getValue())
                .createdAt(event.getCreatedAt().getValue())
                .occurredAt(event.getOccurredAt().getValue())
                .eventType(event.getEventType())
                .eventVersion(event.getEventVersion())
                .build();

        String key = event.getWorkspaceId().getValue().toString();

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(WORKSPACE_CREATED_TOPIC, key, dto);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully published WorkspaceCreatedEvent to Kafka: workspace={}, partition={}, offset={}",
                        event.getWorkspaceId().getValue(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish WorkspaceCreatedEvent to Kafka: workspace={}",
                        event.getWorkspaceId().getValue(), ex);
            }
        });
    }
}