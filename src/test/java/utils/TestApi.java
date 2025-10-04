package utils;


import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import ru.sbrf.pprbts.oauth.config.exception.type.ErrorResponseDto;
import ru.sbrf.pprbts.oauth.config.properties.OAuthServiceProperties;

import java.time.Duration;
import java.util.concurrent.Callable;


@Slf4j
@SuppressWarnings("unused")
public class TestApi {

    public static final String USER_CN = "00CA0001CUFSufssrusr";

    @SuppressWarnings("all")
    private static final String TEST_CERTIFICATE_HEADER = new StringBuilder()
            .append("Hash=2198737e74510dbeeed09d80e6d98392609feb8c82ba7b66fe3501ca9a3a5679;")
            .append("Cert=\"-----BEGIN%20CERTIFICATE-----%0A\".......\"-----END%20CERTIFICATE-----%0A\";")
            .append("Subject=\"CN=%s,OU=00CA,O=Sberbank of Russia,C=RU\";".formatted(USER_CN))
            .append("URI=,By=spiffe://cluster.local/ns/ci05891758-t-services-ift/sa/default;")
            .append("Hash=841681a3ca83277cd24ca6bfc2d02cc65bc94fd6550ed240cea9566fdd046e34;Subject=\"\";")
            .append("URI=spiffe://cluster.local/ns/ci05891758-t-services-ift/sa/default")
            .toString();

    private static final ThreadLocal<HttpHeaders> headers = ThreadLocal.withInitial(HttpHeaders::new);

    private static long totalApiRequestNanos;

    private final TestApiExchanger apiExchanger;

    private final OAuthServiceProperties oAuthServiceProperties;

    public TestApi(
            TestRestTemplate testRestTemplate,
            OAuthServiceProperties oAuthServiceProperties
    ) {
        this.oAuthServiceProperties = oAuthServiceProperties;
        this.apiExchanger = new TestApiExchanger(testRestTemplate);
    }

    private static HttpHeaders getHeaders() {
        return headers.get();
    }

    public void reset() {
        log.info("Total API request time: {} ms.", Duration.ofNanos(totalApiRequestNanos).toMillis());
    }

    public TestApi unauthorized() {
        return this;
    }

    @NonNull
    public <DTO> DTO get(String url, ParameterizedTypeReference<DTO> responseType) {
        return executeRequest(() -> apiExchanger.get(getHeaders(), url, responseType));
    }

    @NonNull
    public <DTO> ResponseEntity<DTO> getForEntity(String url, ParameterizedTypeReference<DTO> responseType) {
        return executeRequest(() -> apiExchanger.getExchange(getHeaders(), url, responseType));
    }

    public ResponseEntity<ErrorResponseDto> getWithError(String url) {
        return executeRequest(() -> apiExchanger.getWithError(getHeaders(), url));
    }

    @NonNull
    public <DTO> ResponseEntity<DTO> getWithError(String url, ParameterizedTypeReference<DTO> responseType) {
        return executeRequest(() -> apiExchanger.getWithError(getHeaders(), url, responseType));
    }

    @NonNull
    public <DTO> DTO post(Object requestDto, String url, ParameterizedTypeReference<DTO> responseType) {
        return executeRequest(() -> apiExchanger.post(getHeaders(), requestDto, url, responseType));
    }

    @NonNull
    public <DTO> ResponseEntity<DTO> postForEntity(Object requestDto, String url, ParameterizedTypeReference<DTO> responseType) {
        return executeRequest(() -> apiExchanger.postExchange(getHeaders(), requestDto, url, responseType));
    }

    public ResponseEntity<ErrorResponseDto> postWithError(Object requestDto, String url) {
        return executeRequest(() -> apiExchanger.postWithError(getHeaders(), requestDto, url));
    }

    @NonNull
    public <DTO> ResponseEntity<DTO> postWithError(Object requestDto, String url, ParameterizedTypeReference<DTO> responseType) {
        return executeRequest(() -> apiExchanger.postWithError(getHeaders(), requestDto, url, responseType));
    }

    @NonNull
    public <DTO> DTO put(Object requestDto, String url, ParameterizedTypeReference<DTO> responseType) {
        return executeRequest(() -> apiExchanger.put(getHeaders(), requestDto, url, responseType));
    }

    @NonNull
    public <DTO> ResponseEntity<DTO> putForEntity(Object requestDto, String url, ParameterizedTypeReference<DTO> responseType) {
        return executeRequest(() -> apiExchanger.putExchange(getHeaders(), requestDto, url, responseType));
    }

    public ResponseEntity<ErrorResponseDto> putWithError(Object requestDto, String url) {
        return executeRequest(() -> apiExchanger.putWithError(getHeaders(), requestDto, url));
    }

    @NonNull
    public <DTO> ResponseEntity<DTO> putWithError(Object requestDto, String url, ParameterizedTypeReference<DTO> responseType) {
        return executeRequest(() -> apiExchanger.putWithError(getHeaders(), requestDto, url, responseType));
    }

    @NonNull
    public <DTO> DTO delete(Object requestDto, String url, ParameterizedTypeReference<DTO> responseType) {
        return executeRequest(() -> apiExchanger.delete(getHeaders(), requestDto, url, responseType));
    }

    @NonNull
    public <DTO> ResponseEntity<DTO> deleteForEntity(Object requestDto, String url, ParameterizedTypeReference<DTO> responseType) {
        return executeRequest(() -> apiExchanger.deleteExchange(getHeaders(), requestDto, url, responseType));
    }

    public ResponseEntity<ErrorResponseDto> deleteWithError(Object requestDto, String url) {
        return executeRequest(() -> apiExchanger.deleteWithError(getHeaders(), requestDto, url));
    }

    @NonNull
    public <DTO> ResponseEntity<DTO> deleteWithError(Object requestDto, String url, ParameterizedTypeReference<DTO> responseType) {
        return executeRequest(() -> apiExchanger.deleteWithError(getHeaders(), requestDto, url, responseType));
    }

    @SneakyThrows
    private <T> T executeRequest(Callable<T> callable) {
        long t0 = System.nanoTime();
        try {
            return callable.call();
        } finally {
            totalApiRequestNanos += System.nanoTime() - t0;
        }
    }
}
