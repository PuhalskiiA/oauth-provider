package utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.core.ParameterizedTypeReference;
import ru.sbrf.pprbts.oauth.config.exception.type.ErrorResponseDto;

import static org.springframework.core.ParameterizedTypeReference.forType;


@UtilityClass
public class TestUtils {

    @SuppressWarnings("unused")
    public final ParameterizedTypeReference<Void> VOID_TYPE = forType(Void.class);

    public final ParameterizedTypeReference<ErrorResponseDto> ERROR_RESPONSE_TYPE = forType(ErrorResponseDto.class);

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build()
            .findAndRegisterModules()
            .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);

    @SneakyThrows
    public String toJson(Object response) {
        return objectMapper.writeValueAsString(response);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> T fromJson(String json, ParameterizedTypeReference<T> clazz) {
        return objectMapper.readValue(json, (Class<T>) clazz.getType());
    }
}
