package org.nextme.userGoal_service.userGoal.infrastructure.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        //BOOTSTRAP_SERVERS_CONFIG : Producer가 처음으로 연결할 Kafka 브로커의 위치를 설정
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "34.22.87.75:9092,34.22.87.75:9093,34.22.87.75:9094");

        //KEY_SERIALIZER_CLASS_CONFIG & VALUE_SERIALIZER_CLASS_CONFIG
        // -Producer가 Key와 Value 값의 데이터를 Kafka 브로커로 전송하기 전에 데이터를 byte array로 변환하는 데 사용하는 직렬화 메커니즘을 설정합니다.
        // Kafka는 네트워크를 통해 데이터를 전송하기 때문에, 객체를 byte array로 변환하는 직렬화 과정이 필요합니다.
        // 따라서, StringSerializer를 사용해 직렬화했습니다
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);

    }

    @Bean
    // Spring Kafka에서 제공하는 Kafka Producer를 Wrapping 한 클래스
//  // Kafka에 메시지를 보내는 여러 메서드를 제공하며, 이 메서드를 사용하여 브로커로 메시지를 보내기 위해 직접
//  Kafka Producer API를 사용하는 대신, send와 같은 메서드를 통해 더 편리하고 간결한 코드로 메시지를 보낼 수 있음
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
