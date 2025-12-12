package org.nextme.userGoal_service.userGoal.application.service;

import org.nextme.userGoal_service.userGoal.infrastructure.kafka.dto.MessageTpl;

import java.util.UUID;

public interface AiResultProducer {
    void send(UUID orderId);
}
