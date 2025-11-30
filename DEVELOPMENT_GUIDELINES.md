# Guía de Desarrollo y Preferencias - BizFlow ERP

Este documento establece las directrices y preferencias de desarrollo para los proyectos conjuntos.

## 1. Idioma
*   **Comunicación:** Todas las explicaciones y respuestas deben ser en **Castellano**.
*   **Código:** Comentarios y documentación técnica pueden estar en inglés o castellano, manteniendo consistencia. Nombres de variables/clases en Inglés (estándar).

## 2. Flujo de Trabajo (Workflow)
1.  **Planificación:** Antes de implementar, explicar qué se va a hacer.
2.  **Implementación:** Escribir el código necesario.
3.  **Verificación Inmediata:**
    *   Crear y ejecutar **Tests Unitarios**.
    *   Crear y ejecutar **Tests de Integración**.
    *   **Prueba en Docker:** Levantar el entorno con Docker y verificar que funciona "en vivo".
4.  **Documentación:** Al finalizar una tarea exitosa, generar documentación en `.md` en la carpeta `docs/` correspondiente.

## 3. Nomenclatura de Contenedores Docker
Para evitar conflictos con otros proyectos (microservicios), usar prefijos específicos:
*   **Este Proyecto (Monolito/Modular):** Prefijo `bizflowerp_`
    *   Base de Datos: `bizflowerp_db`
    *   Backend: `bizflowerp_backend`
    *   Frontend: `bizflowerp_frontend`
*   **Proyecto Microservicios (Futuro):** Prefijo `bizflowmicro_`

## 4. Estándares de Código
*   **Backend:** Java (Spring Boot). Uso de DTOs, Controllers, Services, DAOs.
*   **Frontend:** React.
*   **Testing:** JUnit 5, Mockito, Spring Boot Test.

## 5. Gestión de Errores
*   Si algo falla, analizar logs, corregir y volver a probar.
*   No asumir que funciona sin pruebas.
