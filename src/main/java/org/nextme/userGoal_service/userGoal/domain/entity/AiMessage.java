package org.nextme.userGoal_service.userGoal.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nextme.userGoal_service.userGoal.application.service.AiResultProducer;
import org.nextme.userGoal_service.userGoal.infrastructure.kafka.dto.MessageTpl;

import java.util.UUID;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiMessage implements MessageTpl {
    private UUID orderId;
}
