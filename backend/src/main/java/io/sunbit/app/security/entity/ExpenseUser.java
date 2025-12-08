package io.sunbit.app.security.entity;

import java.util.ArrayList;
import java.util.Collection;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.sunbit.app.entity.Employee;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "expense_user", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
// @Builder

public class ExpenseUser implements UserDetails {
	@Override
	public String getUsername() {
		// Usamos el email como identificador único para Spring Security
		return this.email;
	}
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "name", nullable = false)
	@Length(min = 2, max = 50)
	@Getter(AccessLevel.NONE)
	@NonNull
	private String name;
	@Column(name = "surname", nullable = false)
	@Length(min = 2, max = 128)
	@NonNull
	private String surname;
	@Column(name = "email", nullable = false)
	@Length(min = 3, max = 50)
	@NonNull
	private String email;
	@Column(name = "password", nullable = false)
	@Length(min = 4, max = 64)
	@NonNull
	private String password;
	@OneToOne
	@JoinColumn(name = "employee_id", nullable = true)
	private Employee employee;

	// Constructor without ID.
	public ExpenseUser(
			String name,
			String surname,
			String email,
			String password,
			Collection<Role> roles) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.roles = roles;
	}

	// Constructor with ID.
	public ExpenseUser(
			Long id,
			String name,
			String surname,
			String email,
			String password,
			Collection<Role> roles) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.roles = roles;
	}

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	@com.fasterxml.jackson.annotation.JsonManagedReference
	private Collection<Role> roles = new ArrayList<>();

	public void addRole(Role role) {
		this.roles.add(role);
	}

	public void removeRole(Role role) {
		this.roles.remove(role);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		   for (Role role : this.roles) {
			   String roleName = role.getName();
			   if (!roleName.startsWith("ROLE_")) {
				   roleName = "ROLE_" + roleName;
			   }
			   authorities.add(new SimpleGrantedAuthority(roleName));
		   }
		return authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	// Eliminado getUsername(). Spring Security usará getEmail() como identificador único.

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public String getName() {
		return this.name;
	}
	public String getEmail() {
		return this.email;
	}

	public static Object withDefaultPasswordEncoder() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'withDefaultPasswordEncoder'");
	}
}
