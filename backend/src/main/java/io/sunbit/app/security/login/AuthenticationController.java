package io.sunbit.app.security.login;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.jwt.*;

/***
 * @version 0.0.1
 * @author tecnicomanotas
 *
 */
@CrossOrigin(origins = "*")
@RestController
/*
 * WARNING !!!!
 * El valor de la ruta("api/v1/auth" es lo mismo con slash al principio ->
 * "/api/v1/auth"),
 * me daba '401 Unauthorized'. El valor de la ruta del Controlador
 * 'AuthorizationApi'
 * 
 * @RequestMapping("/api/v1/auth"), no coincidía con el valor de la ruta de
 * acceso
 * establecida en el método 'configure(Http request){ .antMatch("/auth/login")'
 * de la clase
 * AppSecurityConfig.
 */
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

	@Autowired
	AuthenticationManager authManager;
	@Autowired
	JwtAuthenticationUtil jwtAuthUtil;

	// @RequestMapping(path="/login",method=RequestMethod.POST,consumes="application/json",produces="application/json")
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody @Valid AuthenticationRequest authRequest) {
		try {
			Authentication authentication = authManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							authRequest.getEmail(), authRequest.getPassword()));
			ExpenseUser user = (ExpenseUser) authentication.getPrincipal();
			String accessToken = jwtAuthUtil.generateAccessToken(user);
			AuthenticationResponse authzResponse = new AuthenticationResponse(user.getEmail(), accessToken);
			return ResponseEntity.ok(authzResponse);
		} catch (BadCredentialsException ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
}
