package io.sunbit.app.security.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

	private Long id;
	private String email;
	private String accessToken;
	private Long roleId;
	private String roleName;
}
