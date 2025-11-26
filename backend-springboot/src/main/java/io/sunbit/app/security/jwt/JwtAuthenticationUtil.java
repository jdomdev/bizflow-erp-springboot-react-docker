package io.sunbit.app.security.jwt;

import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.sunbit.app.security.entity.ExpenseUser;

/**
 * Utilidad para gestionar tokens JWT de forma segura y eficiente.
 * Maneja la generación, validación y extracción de información de tokens.
 * 
 * @version 2.0
 * @author ExpenseNoteApp Team
 */
@Component
public class JwtAuthenticationUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationUtil.class);
	private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 horas

	@Value("${app.jwt.secret}")
	private String secretKey;

	/**
	 * Obtiene la clave secreta como SecretKey.
	 * 
	 * @return SecretKey para firmar y verificar tokens
	 */
	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	/**
	 * Genera un token JWT válido para el usuario autenticado.
	 * 
	 * @param user Usuario autenticado
	 * @return Token JWT firmado
	 */
	public String generateAccessToken(ExpenseUser user) {
		SecretKey key = getSigningKey();

		return Jwts.builder()
				.subject(user.getId() + "," + user.getEmail())
				.claim("roles", user.getRoles().toString())
				.issuer("ExpenseNoteApp")
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
				.signWith(key)
				.compact();
	}

	/**
	 * Valida la integridad y expiración del token JWT.
	 * 
	 * @param token Token a validar
	 * @return true si el token es válido, false en caso contrario
	 */
	public boolean validateAccessToken(String token) {
		try {
			SecretKey key = getSigningKey();
			Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (ExpiredJwtException ex) {
			LOGGER.warn("Token JWT expirado", ex);
		} catch (JwtException ex) {
			LOGGER.warn("Token JWT inválido: {}", ex.getMessage());
		} catch (IllegalArgumentException ex) {
			LOGGER.warn("Token es nulo, vacío o contiene solo espacios en blanco");
		} catch (Exception ex) {
			LOGGER.warn("Error validando token: {}", ex.getMessage());
		}
		return false;
	}

	/**
	 * Extrae el sujeto (subject) del token.
	 * 
	 * @param token Token JWT
	 * @return Subject del token
	 */
	public String getSubject(String token) {
		return parseClaims(token).getSubject();
	}

	/**
	 * Parsea y retorna los claims del token.
	 * 
	 * @param token Token JWT
	 * @return Claims del token
	 */
	public Claims parseClaims(String token) {
		SecretKey key = getSigningKey();
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	/**
	 * Verifica si el usuario en el token tiene rol de administrador.
	 * 
	 * @param token Token JWT
	 * @return true si el usuario es admin, false en caso contrario
	 */
	public Boolean isAdminTokenUser(String token) {
		try {
			Claims claims = parseClaims(token);
			String claimRoles = (String) claims.get("roles");
			
			if (claimRoles == null) {
				return false;
			}
			
			claimRoles = claimRoles.replace("[", "").replace("]", "");
			String[] roleNames = claimRoles.split(",");
			
			for (String role : roleNames) {
				if (role.trim().equalsIgnoreCase("ROLE_ADMIN")) {
					return true;
				}
			}
		} catch (JwtException ex) {
			LOGGER.warn("Error validando rol admin del token: {}", ex.getMessage());
		}
		return false;
	}

	/**
	 * Extrae el ID del usuario del token JWT.
	 * 
	 * @param token Token JWT
	 * @return ID del usuario
	 */
	public Integer extractTokenUserId(String token) {
		try {
			Claims claims = parseClaims(token);
			String subject = claims.getSubject();
			if (subject != null && !subject.isEmpty()) {
				String[] subjectArray = subject.split(",");
				return Integer.parseInt(subjectArray[0].trim());
			}
		} catch (NumberFormatException | JwtException | ArrayIndexOutOfBoundsException ex) {
			LOGGER.warn("Error extrayendo ID del usuario del token: {}", ex.getMessage());
		}
		return null;
	}

	/**
	 * Extrae el email del usuario del token JWT.
	 * 
	 * @param token Token JWT
	 * @return Email del usuario
	 */
	public String extractTokenUserEmail(String token) {
		try {
			Claims claims = parseClaims(token);
			String subject = claims.getSubject();
			if (subject != null && !subject.isEmpty()) {
				String[] subjectArray = subject.split(",");
				if (subjectArray.length > 1) {
					return subjectArray[1].trim();
				}
			}
		} catch (JwtException | ArrayIndexOutOfBoundsException ex) {
			LOGGER.warn("Error extrayendo email del usuario del token: {}", ex.getMessage());
		}
		return null;
	}
}
