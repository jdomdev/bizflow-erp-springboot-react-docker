## Seguridad y uso de spring-security-crypto

### Contexto
La aplicación utiliza la dependencia `spring-security-crypto` únicamente para el hash seguro de contraseñas de usuario mediante `BCryptPasswordEncoder`. No se emplean los componentes de cifrado simétrico (como `AesBytesEncryptor` o `TextEncryptor`) que pueden estar afectados por vulnerabilidades reportadas en la librería.

### Auditoría de uso
- Se ha revisado todo el código fuente y **solo se utiliza `BCryptPasswordEncoder`** para el registro y autenticación de usuarios.
- No se usan cifradores simétricos ni funciones de encriptación de datos sensibles de la librería.

### Mitigación de vulnerabilidad
- Las vulnerabilidades reportadas para `spring-security-crypto` 6.x afectan principalmente a los cifradores simétricos, no a `BCryptPasswordEncoder`.
- El hash de contraseñas con BCrypt sigue siendo seguro y recomendado por la comunidad de seguridad.
- Se recomienda mantener la dependencia actualizada y monitorizar los avisos de seguridad oficiales.

### Recomendaciones
- **No almacenar datos sensibles usando cifrado simétrico de esta librería.**
- **Usar únicamente `BCryptPasswordEncoder` para contraseñas.**
- Documentar este uso en el README y en auditorías de seguridad.

### Referencias
- [Spring Security Crypto Documentation](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html)
- [Spring Security CVE List](https://spring.io/security/cve)
- [BCryptPasswordEncoder Best Practices](https://www.baeldung.com/spring-security-registration-password-encoding-bcrypt)

---

**Resumen:**
La dependencia `spring-security-crypto` es segura en este proyecto porque solo se usa para el hash de contraseñas con BCrypt. No se emplean los componentes afectados por vulnerabilidades. Se recomienda mantener la dependencia actualizada y monitorizar los avisos de seguridad.

---

> Última auditoría: 8 diciembre 2025
> Responsable: GitHub Copilot
