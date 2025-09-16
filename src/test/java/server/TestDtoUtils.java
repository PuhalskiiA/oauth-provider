package server;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;


@UtilityClass
public class TestDtoUtils {

    public static final String PERSONAL_SUBJECT = "personal-subject";

    public boolean isMultipartRequestDto(String method) {
        return !StringUtils.contains(method, "lite")
                && !StringUtils.equals(method, "/service/job/clean");
    }

}

