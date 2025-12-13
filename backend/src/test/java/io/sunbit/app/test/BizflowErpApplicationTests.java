package io.sunbit.app.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class BizflowErpApplicationTests {
    /**
     * Este test verifica que el contexto de Spring Boot se carga correctamente.
     * Es útil para detectar problemas de configuración general, beans, dependencias,
     * o errores de arranque de la aplicación.
     */
    @Test
    void contextLoads() {
        // Si el contexto no carga, este test fallará.
    }
}
