package io.sunbit.app.security.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "role")
// @ToString(includeFieldNames = false)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Role implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "name", nullable = false, unique = true)
	// Minimum length 6 to accommodate "ROLE_" prefix plus at least one character
	@Length(min = 6, max = 25)
	private String name;

	// Constructor without id.
	public Role(String name) {
		this.name = name;
	}

	// Constructor with id.
	public Role(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	@ManyToMany(mappedBy = "roles")
	@JsonIgnore
	private List<ExpenseUser> users;

	@Override
	public String toString() {
		return this.name;
	}

}
