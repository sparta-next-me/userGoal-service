package org.nextme.userGoal_service.userGoal.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nextme.userGoal_service.userGoal.application.service.AiResultProducer;
import org.nextme.userGoal_service.userGoal.infrastructure.kafka.dto.MessageTpl;

import java.util.UUID;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage implements MessageTpl {
    private UUID roomId;
    private String roomType;
    private String content;
    private String sessionId;
}
