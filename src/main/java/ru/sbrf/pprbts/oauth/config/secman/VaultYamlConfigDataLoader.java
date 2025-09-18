package ru.sbrf.pprbts.oauth.config.secman;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.context.config.ConfigDataLoader;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.cloud.vault.config.KeyValueSecretBackendMetadata;
import org.springframework.cloud.vault.config.LeasingSecretBackendMetadata;
import org.springframework.cloud.vault.config.SecretBackendMetadata;
import org.springframework.cloud.vault.config.VaultConfigDataLoader;
import org.springframework.cloud.vault.config.VaultConfigLocation;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.util.PropertyTransformers;
import ru.sbrf.pprbts.oauth.config.secman.properties.LeaseAwareVaultYamlPropertySource;

import java.util.Collections;


/**
 * Небольшой весёлый костыль, чтобы загружать серкрет из Vault через один секрет, но в котором файл yaml в значении
 * содержит несколько секретов. Это требует кастомного loader, что грузит из Vault секрет
 * и раскрывает его во множество секретов.
 * <p>
 * Да, это костыль, но мне было весело.
 *
 * @see VaultConfigLocation
 * @see VaultConfigDataLoader
 * @see VaultYamlConfigDataResource
 * @see LeaseAwareVaultYamlPropertySource
 */
@RequiredArgsConstructor
public class VaultYamlConfigDataLoader implements ConfigDataLoader<VaultYamlConfigDataResource> {

    private final DeferredLogFactory deferredLogFactory;

    @Override
    @SuppressWarnings("all")
    public ConfigData load(ConfigDataLoaderContext context, VaultYamlConfigDataResource resource) {
        var vaultConfigDataLoader = new VaultConfigDataLoader(deferredLogFactory);
        VaultConfigLocation vaultConfigLocation = new VaultConfigLocation(resource.getPath(), false);

        LeaseAwareVaultYamlPropertySource inlineSource;
        ConfigData data;

        try {
            // Инициализируем соединение и делаем первичную загрузку данных из Vault
            data = vaultConfigDataLoader.load(context, vaultConfigLocation);
            // Оверрайдим propertysource от Vault и ставим свой
            inlineSource = new LeaseAwareVaultYamlPropertySource(
                    data.getPropertySources().getFirst().getName(),
                    context.getBootstrapContext().get(SecretLeaseContainer.class),
                    getRequestedSecret(vaultConfigLocation.getSecretBackendMetadata()),
                    PropertyTransformers.noop(),
                    true
            );
        } catch (Exception e) {
            // Т.к. логирование еще не проинициализировано на этом этапе запуска приложения, пишем в консоль
            e.printStackTrace();
            throw e;
        }

        return new ConfigData(Collections.singletonList(inlineSource));
    }

    private RequestedSecret getRequestedSecret(SecretBackendMetadata accessor) {
        return switch (accessor) {
            case LeasingSecretBackendMetadata lb -> RequestedSecret.from(lb.getLeaseMode(), accessor.getPath());
            case KeyValueSecretBackendMetadata ignored -> RequestedSecret.rotating(accessor.getPath());
            default -> RequestedSecret.renewable(accessor.getPath());
        };
    }
}
