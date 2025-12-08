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

# Directivas para el Agente Gemini - Perfil de Juan Domingo

## 1. Perfil del Usuario y Objetivos

- **Rol Actual:** Data Engineering/Analysis & Fullstack Developer con 2 años de experiencia profesional (Java, Python, Spring Boot, React, API, ETL, DevOps).
- **Formación avanzada:** Bootcamp presencial de Inteligencia Artificial finalizado (1.250h, Madrid, enero-octubre 2025).
- **Especialidad:** Machine Learning, Deep Learning, NLP, Computer Vision, ETL, DevOps, Backend y Frontend.
- **Objetivo profesional:** Liderar y construir soluciones inteligentes y basadas en datos, ya sea en ingeniería, análisis, o desarrollo fullstack.
- **Github relevante:** [https://github.com/jdomdev/bizflow-springboot-microservices-react](https://github.com/jdomdev/bizflow-springboot-microservices-react)

### Resumen background laboral

- Experiencia real en entornos empresariales con Java (J2EE, Spring Boot, patrones), Python para backend y Ciencia de Datos, React y Node.js en frontend, APIs REST, Docker/K8s, testing robusto, integración de ETL, Flujos CI/CD, seguridad OAuth2/JWT, Gitflow, SCRUM, Trello/Jira.
- Proyectos destacados en microservicios, arquitecturas en capas, automatización y despliegue cloud/híbrido, manejo de bases SQL y NoSQL.

### Resumen formativo avanzado

- Python, OOP, scripting y testing (pytest, unittest, JUnit, Postman).
- ML/datos: Pandas, NumPy, Scikit-learn, TensorFlow, Keras, embeddings, Transformers (BERT, GPT).
- Computer Vision: Clasificación, segmentación y reconocimiento visual.
- Docker y Cloud: GCP, AWS, Azure (priorizando soluciones serverless y gestionadas).
- Práctica intensiva en metodologías ágiles (Scrum y Kanban), trabajo colaborativo multi-rol.

***

## 2. Stack Tecnológico y Preferencias

El agente debe priorizar estos stacks y herramientas, adaptando si el contexto del proyecto lo requiere.

- **Lenguajes:** Python, Java, SQL, JavaScript/TypeScript, Shell.
- **Frameworks:** Spring Boot, Django, Flask, React, Express/NestJS.
- **Data & ML:** Pandas, NumPy, Polars, Scikit-learn, TensorFlow, Keras, PyTorch, HuggingFace, spaCy.
- **ETL y Data Eng:** Talend, Apache Airflow, dbt, Apache Spark.
- **Bases de datos:** PostgreSQL, MySQL, SQLServer, MongoDB, Redis.
- **DevOps:** Docker, Docker Compose, Kubernetes, Jenkins, GitHub Actions.
- **Testing:** JUnit, pytest, unittest, Jest, React Testing Library.
- **Workflow:** Git, Gitflow, Trello, Jira, SCRUM/Kanban, documentación exhaustiva.

***

## 3. Principios de Trabajo y Directivas para el Agente

- Antes de actuar, analiza el contexto y todos los archivos relevantes (`requirements.txt`, `package.json`, scripts, infra). Nunca asumas: verifica, pregunta, y documenta.
- El código debe ser limpio, modular, POO en Python y Java, acorde a SOLID y Clean Code, con comentarios centrados en el *porqué* de la lógica compleja.
- Siempre incluir tests con todo lo nuevo; tests en pipelines CI/CD, manteniendo cobertura mínima del 80%.
- Machine Learning/Data: Resuelve E2E, desde ingesta de datos hasta despliegue automatizado del modelo/servicio, documentando cada etapa crítica.
- Documentación extensa; decisiones, arquitectura y lógica narradas en README y Notebooks con storytelling visual.
- Comunicación SIEMPRE en español técnico claro.

***

## 4. Flujos de Trabajo Comunes

- **EDA:** Explorar datasets en profundidad, visualizaciones claras (distribuciones, correlaciones), resumen de insights principales.
- **Pipeline ML:** Proponer pipelines automáticos, preprocesamiento exhaustivo, entrenamiento y evaluación reproducibles.
- **APIs y despliegue:** Desarrollar APIs de backend modernas (FastAPI, Spring Boot), endpoints claros, validaciones robustas, Dockerfile y CI automatizado.
- **Refactorización:** Identificar y aislar lógica, funciones, patrones; migrar scripts a estructuras modulares con main/funciones/unittest.
- **Revisión y colaboración:** Pull Requests descriptivos, squash & merge, seguimiento de issues y ramas temáticas sincronizadas.

***

## 5. Exclusiones y Anti-Patrones

- No usar librerías poco conocidas o sin soporte para problemas productivos.
- Evitar sobre-ingeniería: no propondrás arquitecturas complejas (Docker/K8s) para scripts simples a menos que se exija explícitamente.
- No usar `apply` ni bucles para DataFrames de Pandas: priorizar siempre operaciones vectorizadas y eficientes.

***

## 6. Plantilla de Proyecto Multidisciplinar

**Título del Proyecto:** [Nombre conciso]  
**Rol y stack:** [Ej. Backend/Fullstack, ML/Data Eng.]  
**Descripción general:** Problema, objetivo y valor.  
**Contexto y entorno:** SO, IDE, dependencias clave.  
**Tecnologías:** Backend, Frontend, DB, procesado de datos, DevOps.  
**Objetivo personal y profesional.**

***

#### ✅ Requisitos Técnicos y de Buenas Prácticas

- **Arquitectura:** Modular, por capas, microservicios (cuando aplique).
- **Diseño:** SOLID, Clean Code, patrones apropiados al caso.
- **Datos:** ETL, validaciones robustas, logging y auditado de flujos.
- **Testing:** Unitarios, integración, E2E, que cubran todos los casos críticos.
- **Errores:** Manejo centralizado, códigos HTTP claros, mensajes de error útiles y documentados.
- **Infra y despliegue:** Docker, Compose, K8s, cloud preferentemente serverless.
- **Colaboración:** Gitflow, ramas feature de forma consistente, commits frecuentes y justificados, mensajes y PR explicativos.
- **Seguridad:** OAuth2, JWT, buenas prácticas en dependencias.
- **CI/CD:** Pipelines automáticos con linters, tests y despliegues, usando GitHub Actions/Jenkins.
- **Observabilidad:** Logs estructurados, métricas, tracing.
- **Escalabilidad:** Uso de cachés, carga distribuida, configuración externalizada.
- **Storytelling:** Documentar razonamientos, toma de decisiones, visualizaciones y contexto.

***

#### 🚀 Workflow Desarrollo y Colaboración

1. **Inicio:** Clonado y setup inicial reproducible (Docker, makefile, etc.)
2. **Implementación:** Funcionalidades en pequeños incrementos, siempre acompañadas de tests y documentación.
3. **Versionado:** Ramas por funcionalidad, merges tras PR revisadas y squash.
4. **Despliegue:** Guía para entornos local/staging/prod.
5. **Dependencias:** Añade SOLO la dependencia principal en requirements.txt (no usar pip freeze para mantenerlo limpio).
6. **Presentación:** Storytelling técnico y visual, dashboards y resumen ético-legal.

## 5. Gestión de Errores
*   Si algo falla, analizar logs, corregir y volver a probar.
*   No asumir que funciona sin pruebas.
