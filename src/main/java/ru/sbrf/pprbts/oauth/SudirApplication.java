package ru.sbrf.pprbts.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@SpringBootApplication
@ConfigurationPropertiesScan
public class SudirApplication {

    /**
     * Точка входа.
     *
     * @param args аргументы.
     */
    public static void main(String... args) throws InterruptedException {
        Thread.sleep(5000);
        SpringApplication.run(SudirApplication.class, args);
    }
}