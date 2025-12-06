package io.sunbit.app.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import io.jsonwebtoken.Claims;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.entity.Role;

@SpringBootTest
@TestPropertySource(properties = {
    "app.jwt.secret=testSecretKeyForJwtAuthenticationThatIsLongEnough1234567890"
})
public class JwtAuthenticationUtilTest {

    @Autowired
    private JwtAuthenticationUtil jwtAuthUtil;

    private ExpenseUser testUser;
    private String validToken;

    @BeforeEach
    public void setUp() {
        // Create test user with roles
        Collection<Role> roles = new ArrayList<>();
        roles.add(new Role(1L, "ROLE_USER"));
        roles.add(new Role(2L, "ROLE_ADMIN"));

        testUser = new ExpenseUser(
            1L,
            "John",
            "Doe",
            "john.doe@test.com",
            "password123",
            roles
        );

        // Generate a valid token
        validToken = jwtAuthUtil.generateAccessToken(testUser);
    }

    @Test
    public void testGenerateAccessToken_ShouldReturnValidToken() {
        assertThat(validToken).isNotNull();
        assertThat(validToken).isNotEmpty();
    }

    @Test
    public void testValidateAccessToken_WithValidToken_ShouldReturnTrue() {
        boolean isValid = jwtAuthUtil.validateAccessToken(validToken);
        assertTrue(isValid, "Token should be valid");
    }

    @Test
    public void testValidateAccessToken_WithInvalidToken_ShouldReturnFalse() {
        String invalidToken = "invalid.token.here";
        boolean isValid = jwtAuthUtil.validateAccessToken(invalidToken);
        assertFalse(isValid, "Invalid token should return false");
    }

    @Test
    public void testValidateAccessToken_WithNullToken_ShouldReturnFalse() {
        boolean isValid = jwtAuthUtil.validateAccessToken(null);
        assertFalse(isValid, "Null token should return false");
    }

    @Test
    public void testGetSubject_ShouldReturnUserIdAndEmail() {
        String subject = jwtAuthUtil.getSubject(validToken);
        assertThat(subject).isNotNull();
        assertThat(subject).contains("1");
        assertThat(subject).contains("john.doe@test.com");
    }

    @Test
    public void testParseClaims_ShouldReturnValidClaims() {
        Claims claims = jwtAuthUtil.parseClaims(validToken);
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).contains("john.doe@test.com");
        assertThat(claims.get("roles")).isNotNull();
    }

    @Test
    public void testIsAdminTokenUser_WithAdminRole_ShouldReturnTrue() {
        Boolean isAdmin = jwtAuthUtil.isAdminTokenUser(validToken);
        assertTrue(isAdmin, "User with ROLE_ADMIN should be identified as admin");
    }

    @Test
    public void testIsAdminTokenUser_WithoutAdminRole_ShouldReturnFalse() {
        // Create user without admin role
        Collection<Role> userRoles = new ArrayList<>();
        userRoles.add(new Role(1L, "ROLE_USER"));
        
        ExpenseUser normalUser = new ExpenseUser(
            2L,
            "Jane",
            "Smith",
            "jane.smith@test.com",
            "password123",
            userRoles
        );
        
        String userToken = jwtAuthUtil.generateAccessToken(normalUser);
        Boolean isAdmin = jwtAuthUtil.isAdminTokenUser(userToken);
        assertFalse(isAdmin, "User without ROLE_ADMIN should not be identified as admin");
    }

    @Test
    public void testExtractTokenUserId_ShouldReturnCorrectId() {
        Integer userId = jwtAuthUtil.extractTokenUserId(validToken);
        assertThat(userId).isNotNull();
        assertThat(userId).isEqualTo(1);
    }

    @Test
    public void testExtractTokenUserEmail_ShouldReturnCorrectEmail() {
        String email = jwtAuthUtil.extractTokenUserEmail(validToken);
        assertThat(email).isNotNull();
        assertThat(email).isEqualTo("john.doe@test.com");
    }

    @Test
    public void testExtractTokenUserId_WithInvalidToken_ShouldReturnNull() {
        Integer userId = jwtAuthUtil.extractTokenUserId("invalid.token");
        assertThat(userId).isNull();
    }

    @Test
    public void testExtractTokenUserEmail_WithInvalidToken_ShouldReturnNull() {
        String email = jwtAuthUtil.extractTokenUserEmail("invalid.token");
        assertThat(email).isNull();
    }
}
