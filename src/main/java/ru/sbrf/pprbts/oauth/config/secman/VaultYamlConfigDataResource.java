package ru.sbrf.pprbts.oauth.config.secman;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.config.ConfigDataResource;


/**
 * @see VaultYamlConfigDataLoader
 */
@Getter
@RequiredArgsConstructor
public class VaultYamlConfigDataResource extends ConfigDataResource {

    private final String path;

    @Override
    public boolean equals(Object o) {
        return (this == o) ||
                (o instanceof VaultYamlConfigDataResource r && StringUtils.equals(path, r.path));
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
}
