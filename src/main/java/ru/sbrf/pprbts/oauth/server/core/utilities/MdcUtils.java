package ru.sbrf.pprbts.oauth.server.core.utilities;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;


@UtilityClass
public class MdcUtils {

    public void setValue(@NonNull String key, @Nullable String value) {
        MDC.put(key, value);
    }

    public String getValue(@NonNull String key) {
        return MDC.get(key);
    }
}
