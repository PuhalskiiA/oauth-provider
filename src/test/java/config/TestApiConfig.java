package config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import ru.sbrf.pprbts.oauth.config.properties.OAuthServiceProperties;
import utils.TestApi;


@EnableAsync
@TestConfiguration(proxyBeanMethods = false)
@RequiredArgsConstructor
@Slf4j
public class TestApiConfig {

    private final TestRestTemplate testRestTemplate;

    @PostConstruct
    public void init() {
        RestTemplate restTemplate = testRestTemplate.getRestTemplate();

        // Для отключения ошибок ретрая на 401 ошибке
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @Bean
    public TestApi getTestApi(
            TestRestTemplate testRestTemplate,
            OAuthServiceProperties notificationServiceProperties
    ) {
        return new TestApi(testRestTemplate, notificationServiceProperties);
    }
}
