package ru.sbrf.pprbts.oauth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.Consumer;


@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final ObjectMapper objectMapper;

    private static ClientHttpRequestFactory prepareRequestFactory() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        TrustStrategy acceptingTrustStrategy = (ignore, ignored) -> true;

        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                sslContext, NoopHostnameVerifier.INSTANCE
        );

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                createRegistry(socketFactory)
        );

        HttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    private static Registry<ConnectionSocketFactory> createRegistry(SSLConnectionSocketFactory socketFactory) {
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", socketFactory)
                .build();
    }

    @Bean
    @SneakyThrows({
            NoSuchAlgorithmException.class,
            KeyStoreException.class,
            KeyManagementException.class
    })
    public RestClient restClient() {
        return RestClient.builder()
                .requestFactory(prepareRequestFactory())
                .messageConverters(prepareConverters())
                .build();
    }

    private Consumer<List<HttpMessageConverter<?>>> prepareConverters() {
        return converters -> {
            MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
            jacksonConverter.setObjectMapper(objectMapper);

            converters.removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);
            converters.add(jacksonConverter);
        };
    }
}
