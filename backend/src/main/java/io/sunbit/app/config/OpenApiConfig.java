package io.sunbit.app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for Bizflow ERP API documentation.
 * 
 * Access the interactive documentation at:
 * - Swagger UI: /swagger-ui.html
 * - OpenAPI JSON: /v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bizflowOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
            .info(new Info()
                .title("Bizflow ERP API")
                .description("API REST para gestión de gastos empresariales, nóminas, empleados y usuarios")
                .version("2.0.0")
                .contact(new Contact()
                    .name("Bizflow ERP Team")
                    .email("support@bizflowerp.com"))
                .license(new License()
                    .name("GNU General Public License v3.0")
                    .url("https://www.gnu.org/licenses/gpl-3.0.html")))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token de autenticación. Obtener via POST /api/auth/login")));
    }
}
