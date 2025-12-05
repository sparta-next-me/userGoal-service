package org.nextme.userGoal_service.global.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
//@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableJpaAuditing
public class JpaConfig {
//    @Bean
//    public AuditorAware<String> auditorProvider(){
//        return new AuditorAwareImpl();
//    }
}