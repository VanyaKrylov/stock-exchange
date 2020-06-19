package ru.spbstu.hsisct.stockmarket.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.util.ErrorHandler;

import javax.jms.ConnectionFactory;

@Slf4j
@Configuration
public class JmsConfig {

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(@Qualifier("nonXaJmsConnectionFactory") ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();
        defaultJmsListenerContainerFactory.setConnectionFactory(connectionFactory);
        defaultJmsListenerContainerFactory.setSessionTransacted(true);
        defaultJmsListenerContainerFactory.setErrorHandler(errorHandler());
        return defaultJmsListenerContainerFactory;
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new CustomErrorHandler();
    }

    private class CustomErrorHandler implements ErrorHandler {

        @Override
        public void handleError(Throwable throwable) {
            log.error(throwable.getMessage());
        }
    }
}
