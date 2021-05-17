package net.chrisrichardson.ftgo.orderservice.domain;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.eventuate.tram.spring.consumer.jdbc.TramConsumerJdbcAutoConfiguration;

@Configuration
@EnableJpaRepositories
@EnableAutoConfiguration(exclude = TramConsumerJdbcAutoConfiguration.class)
public class OrderJpaTestConfiguration {
}
