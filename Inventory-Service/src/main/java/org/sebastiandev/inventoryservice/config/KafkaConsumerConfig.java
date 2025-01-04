package org.sebastiandev.inventoryservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.sebastiandev.inventoryservice.dto.event.OrderCreatedEvent; // Importe seu tipo de evento
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        // Associa a ConsumerFactory personalizada
        factory.setConsumerFactory(consumerFactory());

        // (Opcional) Personalizar o ErrorHandler para configurar DLT etc.
        // Por padrão, com o ErrorHandlingDeserializer, a maior parte das falhas de
        // desserialização serão tratadas sem matar o consumer.
        DefaultErrorHandler errorHandler = new DefaultErrorHandler();
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    @Bean
    public DefaultKafkaConsumerFactory<String, OrderCreatedEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        // Configurações básicas de bootstrap e groupId
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "inventory-group");

        /*
         * Em vez de configurar diretamente o KEY_DESERIALIZER_CLASS_CONFIG e o
         * VALUE_DESERIALIZER_CLASS_CONFIG como StringDeserializer/JsonDeserializer,
         * usamos o ErrorHandlingDeserializer como "wrapper".
         */
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        // Informa quem é o "delegado" para cada tipo de deserialização
        props.put("spring.deserializer.key.delegate.class", StringDeserializer.class);
        props.put("spring.deserializer.value.delegate.class", JsonDeserializer.class);

        // REMOVER type headers
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, true);

        // Pacotes confiáveis - deve incluir o pacote onde seu evento está
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        // Cria o DefaultKafkaConsumerFactory usando os ErrorHandlingDeserializers
        return new DefaultKafkaConsumerFactory<>(
                props,
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(OrderCreatedEvent.class))
        );
    }
}
