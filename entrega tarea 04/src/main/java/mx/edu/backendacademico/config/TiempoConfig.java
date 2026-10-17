package mx.edu.backendacademico.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class TiempoConfig {
    @Bean
    public Clock reloj() {
        return Clock.systemUTC();
    }
}
