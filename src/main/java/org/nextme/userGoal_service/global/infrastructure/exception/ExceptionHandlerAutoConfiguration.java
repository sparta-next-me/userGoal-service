package org.nextme.userGoal_service.global.infrastructure.exception;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.nextme.account_server.global.infrastructure.exception.handler")
public class ExceptionHandlerAutoConfiguration {
}