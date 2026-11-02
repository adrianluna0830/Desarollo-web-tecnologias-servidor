package mx.edu.backendacademico.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CicloDeVida {
    private static final Logger log = LoggerFactory.getLogger(CicloDeVida.class);
    private final Clock reloj;

    public CicloDeVida(Clock reloj) {
        this.reloj = reloj;
    }

    @PostConstruct
    void iniciar() {
        log.info("bean inicializado en {}", reloj.instant());
    }

    @PreDestroy
    void cerrar() {
        log.info("cierre del bean de demostracion");
    }
}
