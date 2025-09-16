package utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.sbrf.pprbts.oauth.config.exception.type.ErrorResponseDto;
import server.TestDtoUtils;

import java.net.URI;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


/**
 * Реализация GET, POST и остальных вызовов к контроллерам приложения.
 */
@Getter
@RequiredArgsConstructor
@Slf4j
public class TestApiExchanger {

    private final TestRestTemplate testRestTemplate;

    @NonNull
    private static <DTO> ResponseEntity<DTO> convertToEntity(
            @NonNull ResponseEntity<String> responseEntity,
            DTO responseBody
    ) {
        return new ResponseEntity<>(responseBody,
                                    responseEntity.getHeaders(),
                                    responseEntity.getStatusCode());
    }

    @NonNull
    private static <DTO> DTO assertNormalResponseEntity(ResponseEntity<DTO> response) {
        assertThat(response)
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        DTO body = response.getBody();

        assertThat(body)
                .overridingErrorMessage("Ожидается, что тело ответа не пустое.")
                .isNotNull();

        return body;
    }

    @NonNull
    private static ResponseEntity<ErrorResponseDto> assertErrorResponseEntity(ResponseEntity<ErrorResponseDto> response) {
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK);

        ErrorResponseDto body = response.getBody();
        assertThat(body)
                .overridingErrorMessage("Ожидается, что тело ответа не пустое.")
                .isNotNull();
        assertThat(body.getTraceId()).isNotNull();
        assertThat(body.getMessage()).isNotNull();

        return response;
    }

    @NonNull
    private static <DTO> ResponseEntity<DTO> assertErrorResponseEntityStatus(ResponseEntity<DTO> response) {
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK);
        return response;
    }

    @NonNull
    public <DTO> DTO get(HttpHeaders headers, String method, ParameterizedTypeReference<DTO> responseType) {
        ResponseEntity<DTO> response = getExchange(headers, method, responseType);
        return assertNormalResponseEntity(response);
    }

    @NonNull
    public ResponseEntity<ErrorResponseDto> getWithError(HttpHeaders headers, String method) {
        ResponseEntity<ErrorResponseDto> response = getExchange(headers, method, TestUtils.ERROR_RESPONSE_TYPE);
        return assertErrorResponseEntity(response);
    }

    @NonNull
    public <DTO> ResponseEntity<DTO> getWithError(HttpHeaders headers, String method, ParameterizedTypeReference<DTO> responseType) {
        ResponseEntity<DTO> response = getExchange(headers, method, responseType);
        return assertErrorResponseEntityStatus(response);
    }

    @NonNull
    public <DTO> DTO post(HttpHeaders headers, Object requestDto, String method, ParameterizedTypeReference<DTO> responseType) {
        ResponseEntity<DTO> response = postExchange(headers, requestDto, method, responseType);
        return assertNormalResponseEntity(response);
    }

    @NonNull
    public ResponseEntity<ErrorResponseDto> postWithError(HttpHeaders headers, Object requestDto, String method) {
        ResponseEntity<ErrorResponseDto> response = postExchange(headers, requestDto, method, TestUtils.ERROR_RESPONSE_TYPE);
        return assertErrorResponseEntity(response);
    }

    @NonNull
    public <DTO> ResponseEntity<DTO> postWithError(HttpHeaders headers, Object requestDto, String method, ParameterizedTypeReference<DTO> responseType) {
        ResponseEntity<DTO> response = postExchange(headers, requestDto, method, responseType);
        return assertErrorResponseEntityStatus(response);
    }

    @NonNull
    public <DTO> DTO put(HttpHeaders headers, Object requestDto, String method, ParameterizedTypeReference<DTO> responseType) {
        ResponseEntity<DTO> response = putExchange(headers, requestDto, method, responseType);
        return assertNormalResponseEntity(response);
    }

    @NonNull
    public <DTO> ResponseEntity<DTO> putWithError(HttpHeaders headers, Object requestDto, String method, ParameterizedTypeReference<DTO> responseType) {
        ResponseEntity<DTO> response = putExchange(headers, requestDto, method, responseType);
        return assertErrorResponseEntityStatus(response);
    }

    @NonNull
    public ResponseEntity<ErrorResponseDto> putWithError(HttpHeaders headers, Object requestDto, String method) {
        ResponseEntity<ErrorResponseDto> response = putExchange(headers, requestDto, method, TestUtils.ERROR_RESPONSE_TYPE);
        return assertErrorResponseEntity(response);
    }

    @NonNull
    public <DTO> DTO delete(HttpHeaders headers, Object requestDto, String method, ParameterizedTypeReference<DTO> responseType) {
        ResponseEntity<DTO> response = deleteExchange(headers, requestDto, method, responseType);
        return assertNormalResponseEntity(response);
    }

    @NonNull
    public ResponseEntity<ErrorResponseDto> deleteWithError(HttpHeaders headers, Object requestDto, String method) {
        ResponseEntity<ErrorResponseDto> response = deleteExchange(headers, requestDto, method, TestUtils.ERROR_RESPONSE_TYPE);
        return assertErrorResponseEntity(response);
    }

    @NonNull
    public <DTO> ResponseEntity<DTO> deleteWithError(HttpHeaders headers, Object requestDto, String method, ParameterizedTypeReference<DTO> responseType) {
        ResponseEntity<DTO> response = deleteExchange(headers, requestDto, method, responseType);
        return assertErrorResponseEntityStatus(response);
    }

    @NonNull
    public <DTO> ResponseEntity<DTO> getExchange(HttpHeaders headers, String method, ParameterizedTypeReference<DTO> responseType) {
        return exchange(HttpMethod.GET, headers, method, null, responseType);
    }

    @NonNull
    public <DTO> ResponseEntity<DTO> postExchange(HttpHeaders headers, Object requestDto, String method, ParameterizedTypeReference<DTO> responseType) {
        return exchange(HttpMethod.POST, headers, method, requestDto, responseType);
    }

    @NonNull
    public <DTO> ResponseEntity<DTO> putExchange(HttpHeaders headers, Object requestDto, String method, ParameterizedTypeReference<DTO> responseType) {
        return exchange(HttpMethod.PUT, headers, method, requestDto, responseType);
    }

    @NonNull
    public <DTO> ResponseEntity<DTO> deleteExchange(HttpHeaders headers, Object requestDto, String method, ParameterizedTypeReference<DTO> responseType) {
        return exchange(HttpMethod.DELETE, headers, method, requestDto, responseType);
    }

    @NonNull
    private <DTO> ResponseEntity<DTO> exchange(
            HttpMethod httpMethod,
            HttpHeaders headers,
            String method,
            Object request,
            ParameterizedTypeReference<DTO> responseType
    ) {
        Object body = composeDtoForRequest(request, method);

        try {
            URI uri = URI.create(method);

            HttpHeaders httpHeaders = new HttpHeaders();
            headers.forEach((k, v) -> httpHeaders.set(k, String.join(",", v)));

            HttpEntity<Object> entity = new HttpEntity<>(body, httpHeaders);
            ResponseEntity<String> responseEntity = testRestTemplate.exchange(uri, httpMethod, entity, String.class);

            String jsonResponse = responseEntity.getBody();
            HttpStatusCode statusCode = responseEntity.getStatusCode();

            if (jsonResponse != null) {
                log.info("<== RESPONSE: {}", responseEntity.getBody());
            }

            if (!statusCode.is2xxSuccessful()) {
                log.info("<== RESPONSE STATUS {}", statusCode.value());
            }

            if (jsonResponse != null) {
                try {
                    DTO responseBody = TestUtils.fromJson(jsonResponse, responseType);
                    return convertToEntity(responseEntity, responseBody);
                } catch (Exception e) {
                    log.error("Original exception", e);
                    throw new AssertionError("Cannot convert response from testing service to DTO:\n%s\nto DTO of type:\n%s"
                                                     .formatted(jsonResponse, responseType.getType().getTypeName()));
                }
            }
            return convertToEntity(responseEntity, null);
        } finally {
            log.debug("<== REQUEST FINISHED.");
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object composeDtoForRequest(Object request, String method) {
        Object body;
        // Это в случае, когда мы шлём простой по старинке JSON, где его нужно обернуть в поле model
        if (request != null && !(request instanceof LinkedMultiValueMap)) {
            // Или если это JSON, не относящийся к multipart запросам, то оставляем в JSON.
            if (TestDtoUtils.isMultipartRequestDto(method)) {
                body = new LinkedMultiValueMap<>();
                ((LinkedMultiValueMap) body).add("model", TestUtils.toJson(request));
            } else {
                body = request;
            }
        } else if (request instanceof LinkedMultiValueMap map) {
            body = (MultiValueMap<String, Object>) map;
        } else {
            body = new LinkedMultiValueMap<>();
        }
        return body;
    }
}
