package io.sunbit.app.security.jwt;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.entity.Role;

/**
 * Filtro JWT para autenticación y autorización en Spring Security.
 * Valida tokens JWT en cada petición y establece el contexto de seguridad.
 * 
 * @version 2.0
 * @author ExpenseNoteApp Team
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	private static final String BEARER_PREFIX = "Bearer ";
	private static final String AUTHORIZATION_HEADER = "Authorization";

	@Autowired
	private JwtAuthenticationUtil jwtAuthUtil;

	/**
	 * Procesa cada petición HTTP para validar el token JWT.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain)
			throws ServletException, IOException {
		
		try {
			// Verificar que el header de autorización exista
			if (!hasAuthorizationHeader(request)) {
				LOGGER.debug("Petición sin header de autorización: {}", request.getRequestURI());
				filterChain.doFilter(request, response);
				return;
			}

			// Extraer el token
			String accessToken = getAccessToken(request);
			
			// Validar el token
			if (!jwtAuthUtil.validateAccessToken(accessToken)) {
				LOGGER.warn("Token JWT no válido para URI: {}", request.getRequestURI());
				filterChain.doFilter(request, response);
				return;
			}

			// Establecer contexto de seguridad
			setAuthenticationContext(accessToken, request);
			
		} catch (Exception ex) {
			LOGGER.error("Error en procesamiento de autenticación JWT", ex);
			// Continuar sin autenticación en caso de error
		}

		filterChain.doFilter(request, response);
	}

	/**
	 * Verifica si la petición contiene un header de autorización válido.
	 * 
	 * @param request Petición HTTP
	 * @return true si el header existe y comienza con "Bearer"
	 */
	private boolean hasAuthorizationHeader(HttpServletRequest request) {
		String header = request.getHeader(AUTHORIZATION_HEADER);
		return !ObjectUtils.isEmpty(header) && header.startsWith(BEARER_PREFIX);
	}

	/**
	 * Extrae el token JWT del header de autorización.
	 * 
	 * @param request Petición HTTP
	 * @return Token JWT sin el prefijo "Bearer "
	 */
	private String getAccessToken(HttpServletRequest request) {
		String header = request.getHeader(AUTHORIZATION_HEADER);
		return header.substring(BEARER_PREFIX.length()).trim();
	}

	/**
	 * Establece el contexto de seguridad de Spring Security.
	 * 
	 * @param accessToken Token JWT validado
	 * @param request Petición HTTP
	 */
	private void setAuthenticationContext(String accessToken, HttpServletRequest request) {
		UserDetails userDetails = getUserDetails(accessToken);
		
		UsernamePasswordAuthenticationToken authentication = 
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		
		authentication.setDetails(
			new WebAuthenticationDetailsSource().buildDetails(request));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		LOGGER.debug("Autenticación JWT establecida para usuario: {}", userDetails.getUsername());
	}

	/**
	 * Extrae la información del usuario desde el token JWT.
	 * 
	 * @param accessToken Token JWT
	 * @return UserDetails con la información del usuario
	 */
	private UserDetails getUserDetails(String accessToken) {
		ExpenseUser tokenUserDetails = new ExpenseUser();
		
		try {
			Claims claims = jwtAuthUtil.parseClaims(accessToken);
			
			// Extraer roles
			String claimRoles = (String) claims.get("roles");
			if (claimRoles != null) {
				claimRoles = claimRoles.replace("[", "").replace("]", "").trim();
				String[] roleNames = claimRoles.split(",");
				for (String roleName : roleNames) {
					tokenUserDetails.addRole(new Role(roleName.trim()));
				}
			}

			// Extraer información del usuario (ID y Email)
			String subject = claims.getSubject();
			if (subject != null && !subject.isEmpty()) {
				String[] subjectArray = subject.split(",");
				if (subjectArray.length >= 2) {
					tokenUserDetails.setId(Long.parseLong(subjectArray[0].trim()));
					tokenUserDetails.setEmail(subjectArray[1].trim());
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Error extrayendo información del token JWT", ex);
		}

		return tokenUserDetails;
	}
}
