package org.nextme.userGoal_service.userGoal.infrastructure.rebbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class UpdatePublisher {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public void sendUpdateEvent(UpdateUserGoalEvent event) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
    }
}
