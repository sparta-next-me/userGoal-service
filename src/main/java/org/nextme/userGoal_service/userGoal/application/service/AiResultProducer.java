package org.nextme.userGoal_service.userGoal.application.service;

import org.nextme.userGoal_service.userGoal.domain.entity.ChatMessage;
import org.nextme.userGoal_service.userGoal.infrastructure.kafka.dto.AiMessageResponse;
import org.nextme.userGoal_service.userGoal.infrastructure.kafka.dto.MessageTpl;

import java.util.UUID;

public interface AiResultProducer {
    AiMessageResponse send(ChatMessage chatMessage);
}
