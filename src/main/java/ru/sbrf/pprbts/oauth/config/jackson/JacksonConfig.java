package ru.sbrf.pprbts.oauth.config.jackson;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sbrf.pprbts.oauth.config.jackson.deserializers.TrimmingStringDeserializer;


@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonMapperCustomizer() {
        return builder -> {
            builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            builder.deserializerByType(String.class, new TrimmingStringDeserializer());
            builder.failOnUnknownProperties(true);
        };
    }
}
