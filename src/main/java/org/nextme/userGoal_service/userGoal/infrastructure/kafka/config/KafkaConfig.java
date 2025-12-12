package org.nextme.userGoal_service.userGoal.infrastructure.kafka.config;


import org.nextme.userGoal_service.userGoal.infrastructure.kafka.dto.MessageTpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {

    @Bean
    // 메세지 발행을 위한 빈 생성
    public KafkaTemplate<String, MessageTpl> kafkaTemplate(ProducerFactory<String, MessageTpl> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
