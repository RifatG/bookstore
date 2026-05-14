package org.example.analyticsservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class AnalyticsServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsServiceApplication.class);

    @Value("${spring.kafka.bootstrap-servers:not-set}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:not-set}")
    private String groupId;

    @Value("${spring.kafka.consumer.key-deserializer:not-set}")
    private String keyDeserializer;

    @Value("${spring.kafka.consumer.value-deserializer:not-set}")
    private String valueDeserializer;

    @Value("${spring.kafka.properties.security.protocol:not-set}")
    private String securityProtocol;

    @Value("${spring.kafka.properties.sasl.mechanism:not-set}")
    private String saslMechanism;

    @Value("${spring.kafka.properties.sasl.jaas.config:not-set}")
    private String jaasConfig;

    public static void main(String[] args) {
        SpringApplication.run(AnalyticsServiceApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logConfiguration() {
        log.info("================== ANALYTICS SERVICE STARTED ==================");
        log.info("Kafka configuration:");
        log.info("  bootstrap.servers: {}", maskPassword(bootstrapServers));
        log.info("  consumer.group-id: {}", groupId);
        log.info("  consumer.key-deserializer: {}", keyDeserializer);
        log.info("  consumer.value-deserializer: {}", valueDeserializer);
        log.info("  security.protocol: {}", securityProtocol);
        log.info("  sasl.mechanism: {}", saslMechanism);
        log.info("  sasl.jaas.config: {}", maskPassword(jaasConfig));
        log.info("===============================================================");
    }

    private String maskPassword(String config) {
        if (config == null) return "null";
        if (config.contains("password")) {
            return config.replaceAll("password=\"[^\"]*\"", "password=\"***\"");
        }
        return config;
    }
}
