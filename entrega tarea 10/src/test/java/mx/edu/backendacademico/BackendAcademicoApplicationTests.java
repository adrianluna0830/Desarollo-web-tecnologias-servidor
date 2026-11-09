package mx.edu.backendacademico;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:contexto;mode=postgresql;db_close_delay=-1",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"
})
class BackendAcademicoApplicationTests {
    @Test void cargaElContextoConBaseEfimera() { }
}
