package ru.sbrf.pprbts.oauth.config.secman.properties;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.vault.core.env.LeaseAwareVaultPropertySource;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseEvent;
import org.springframework.vault.core.util.PropertyTransformer;
import ru.sbrf.pprbts.oauth.config.secman.VaultYamlConfigDataLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Этот PropertySource позволяет парсить yaml-файлы из секретов.
 * Один секрет - один yaml файл.
 * Он берёт из секретов Vault все секреты, что оканчиваются на yaml или yml, и парсит их в свойства.
 *
 * @see VaultYamlConfigDataLoader
 */
public class LeaseAwareVaultYamlPropertySource extends LeaseAwareVaultPropertySource {

    private static final YamlPropertySourceLoader YAML_PROPERTY_SOURCE_LOADER = new YamlPropertySourceLoader();

    public LeaseAwareVaultYamlPropertySource(
            String name,
            SecretLeaseContainer secretLeaseContainer,
            RequestedSecret requestedSecret,
            PropertyTransformer propertyTransformer,
            boolean ignoreSecretNotFound
    ) {
        super(
                name,
                secretLeaseContainer,
                requestedSecret,
                propertyTransformer,
                ignoreSecretNotFound
        );
    }

    public static void convertYamlKeysToProperties(Map<String, Object> properties) {
        Map<String, Object> propertiesCopy = new HashMap<>(properties);

        for (var entry : propertiesCopy.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            boolean isYamlKey = key.endsWith(".yaml") || key.endsWith(".yml");
            if (!isYamlKey || !(value instanceof String yamlContent)) {
                continue;
            }
            try {
                var parsedPropertySource = parseYamlSource(yamlContent, key);
                var flattenedProperties = convertSourceToProperties(parsedPropertySource);

                properties.putAll(flattenedProperties);
                properties.remove(key);
            } catch (Exception e) {
                // log здесь не инициализирован, поэтому логгирование напрямую в консоль
                e.printStackTrace();
                throw new IllegalArgumentException("Error when parsing YAML for the key: %s".formatted(key), e);
            }

        }
    }

    private static Map<String, Object> convertSourceToProperties(
            OriginTrackedMapPropertySource parsedPropertySource
    ) {
        Map<String, Object> flattenedProperties = new java.util.HashMap<>();

        // getSource() здесь не подойдёт, т.к. он возвращает ещё OriginTrackedValue в мапах
        // и их надо будет отдельно обрабатывать с конвертерами
        for (var propertyName : parsedPropertySource.getPropertyNames()) {
            String propertyValue = java.util.Objects.requireNonNull(
                    parsedPropertySource.getProperty(propertyName)
            ).toString();
            flattenedProperties.put(propertyName, propertyValue);
        }
        return flattenedProperties;
    }

    @SneakyThrows({IOException.class})
    private static OriginTrackedMapPropertySource parseYamlSource(
            String yamlContent,
            String key
    ) {
        byte[] yamlContentBytes = yamlContent.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(yamlContentBytes);
        var parsedYaml = YAML_PROPERTY_SOURCE_LOADER.load(key, resource);

        return (OriginTrackedMapPropertySource) parsedYaml.getFirst();
    }

    /**
     * Хэндлер получения новых секретов из Vault.
     *
     * @param leaseEvent - событие о получении нового секрета
     * @param properties - свойства, которые уже были получены
     */
    @Override
    protected void handleLeaseEvent(
            @NonNull SecretLeaseEvent leaseEvent,
            @NonNull Map<String, Object> properties
    ) {
        super.handleLeaseEvent(leaseEvent, properties);
        convertYamlKeysToProperties(properties);
    }
}
