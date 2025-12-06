package io.sunbit.app.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI expenseNoteAppOpenAPI() {
        // Define JWT security scheme
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("JWT token for authentication. Use format: Bearer {token}");

        // Define security requirement
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Authentication");

        // Contact information
        Contact contact = new Contact()
                .name("ExpenseNoteApp Team")
                .email("support@expensenoteapp.com")
                .url("https://github.com/yourusername/ExpenseNoteApp");

        // License information
        License license = new License()
                .name("GNU General Public License v3.0")
                .url("https://www.gnu.org/licenses/gpl-3.0.html");

        // API Info
        Info info = new Info()
                .title("ExpenseNoteApp REST API")
                .version("1.1.0")
                .description("""
                        **ExpenseNoteApp** es una API REST completa para la gestión de gastos empresariales.
                        
                        ## Características principales:
                        - 🔐 Autenticación JWT con roles (ADMIN, USER)
                        - 💰 Gestión completa de gastos (CRUD)
                        - 👥 Gestión de empleados y nóminas
                        - 📊 Reportes y estadísticas
                        - 🔒 Seguridad robusta con Spring Security 6.3.3
                        
                        ## Autenticación
                        La mayoría de endpoints requieren autenticación JWT. Para obtener un token:
                        1. Usa el endpoint POST /api/v1/auth/login con tus credenciales
                        2. Copia el token JWT de la respuesta
                        3. Click en el botón 'Authorize' arriba y pega el token (sin "Bearer")
                        4. Ahora puedes probar todos los endpoints protegidos
                        
                        ## Roles y Permisos
                        - **ROLE_ADMIN**: Acceso completo al sistema
                        - **ROLE_USER**: Acceso limitado a recursos propios
                        
                        ## Credenciales de prueba
                        - Email: admin@example.com
                        - Password: admin123
                        """)
                .contact(contact)
                .license(license);

        // Server configuration
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Servidor de desarrollo local");

        Server prodServer = new Server()
                .url("https://api.tudominio.com")
                .description("Servidor de producción");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, prodServer))
                .addSecurityItem(securityRequirement)
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", securityScheme));
    }
}
