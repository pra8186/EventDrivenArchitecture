package com.eventdriven.config;

import com.eventdriven.event.EventEntry;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.EmbeddedKafkaKraftBroker;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration using an {@link EmbeddedKafkaBroker} — no external
 * Kafka server needed. The broker starts in-process at application startup.
 *
 * <p>Configures producer (JSON serializer), consumer (JSON deserializer),
 * and auto-creates the topic.
 */
@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topic:tax-events}")
    private String topicName;

    /**
     * Starts an in-process Kafka broker (KRaft mode, no ZooKeeper).
     * Spring manages its lifecycle.
     */
    @Bean
    public EmbeddedKafkaBroker embeddedKafkaBroker() {
        EmbeddedKafkaKraftBroker broker = new EmbeddedKafkaKraftBroker(1, 1, topicName);
        broker.kafkaPorts(0);  // random port
        return broker;
    }

    /**
     * Auto-create the topic on startup.
     */
    @Bean
    public NewTopic taxEventsTopic() {
        return TopicBuilder.name(topicName)
                .partitions(1)
                .replicas(1)
                .build();
    }

    // --- Producer ---

    @Bean
    public ProducerFactory<String, EventEntry> producerFactory(EmbeddedKafkaBroker broker) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker.getBrokersAsString());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, EventEntry> kafkaTemplate(ProducerFactory<String, EventEntry> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    // --- Consumer ---

    @Bean
    public ConsumerFactory<String, EventEntry> consumerFactory(EmbeddedKafkaBroker broker) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, broker.getBrokersAsString());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "eventdriven-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.eventdriven.event");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, EventEntry.class.getName());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EventEntry> kafkaListenerContainerFactory(
            ConsumerFactory<String, EventEntry> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, EventEntry> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
