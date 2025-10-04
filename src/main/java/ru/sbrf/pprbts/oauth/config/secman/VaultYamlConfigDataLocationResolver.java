package ru.sbrf.pprbts.oauth.config.secman;

import lombok.SneakyThrows;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataLocationNotFoundException;
import org.springframework.boot.context.config.ConfigDataLocationResolver;
import org.springframework.boot.context.config.ConfigDataLocationResolverContext;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.cloud.vault.config.VaultProperties;
import org.springframework.core.Ordered;

import java.util.List;


/**
 * Стандартный резолвер для Vault Yaml загрузчика.
 *
 * @see VaultYamlConfigDataLoader
 */
public class VaultYamlConfigDataLocationResolver
        implements ConfigDataLocationResolver<VaultYamlConfigDataResource>, Ordered {

    private static final String PREFIX = "vault-yaml://";

    /**
     * Взято из оригинального VaultConfigDataLocationResolver. Нужно для регистрации VaultProperties.
     *
     * @param context контекст
     */
    private static void registerVaultProperties(ConfigDataLocationResolverContext context) {
        context.getBootstrapContext()
                .registerIfAbsent(VaultProperties.class, ignore -> {
                    VaultProperties vaultProperties = context.getBinder()
                            .bindOrCreate(VaultProperties.PREFIX, VaultProperties.class);

                    String appName = context.getBinder()
                            .bind("spring.application.name", String.class)
                            .orElse(vaultProperties.getApplicationName());

                    vaultProperties.setApplicationName(appName);

                    return vaultProperties;
                });
    }

    @Override
    public int getOrder() {
        // чтобы наш резолвер шёл раньше штатного vault://
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public boolean isResolvable(ConfigDataLocationResolverContext context, ConfigDataLocation location) {
        return location.hasPrefix(PREFIX);
    }

    @Override
    @SneakyThrows({
            ConfigDataLocationNotFoundException.class,
            ConfigDataResourceNotFoundException.class
    })
    public List<VaultYamlConfigDataResource> resolve(
            ConfigDataLocationResolverContext context,
            ConfigDataLocation location
    ) {
        String path = location.getNonPrefixedValue(PREFIX);
        registerVaultProperties(context);

        return List.of(new VaultYamlConfigDataResource(path));
    }
}
