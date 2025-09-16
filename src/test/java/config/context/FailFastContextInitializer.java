package config.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
public class FailFastContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final AtomicBoolean flag = new AtomicBoolean();

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        boolean error = flag.getAndSet(true);
        if (error) {
            log.error("""
                              Second application context start attempt.
                              ####################################################
                              #     SECOND APPLICATION CONTEXT START ATTEMPT.    #
                              #     -----------------------------------------    #
                              #  Please look upper for the reason why Spring is  #
                              #  trying to recreate test context.  This can be   #
                              #  caused by adding an @Import/@MockBean and some  #
                              #  other annotations to the test class.            #
                              #                                                  #
                              #  This process will now exit immediately.         #
                              ####################################################
                              """);
            System.exit(-1);
        }
    }
}
