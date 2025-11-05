package com.snapshot.lumina.businesslogic.domain.event.eventpublisher;

import com.snapshot.lumina.businesslogic.domain.event.DomainEvent;

public interface DomainEventPublisher {
    <T extends DomainEvent> void publish(T event);

    default void publishAll(DomainEvent... events) {
        for (DomainEvent event : events) {
            publish(event);
        }
    }
}
