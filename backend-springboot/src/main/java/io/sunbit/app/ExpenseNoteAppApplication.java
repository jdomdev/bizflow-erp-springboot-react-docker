package io.sunbit.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication()
// @SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EntityScan(basePackages = { "io.sunbit.app.entity", "io.sunbit.app.security.entity",
		"io.sunbit.app.security.controller", "io.sunbit.app.controller" })
@EnableJpaRepositories(basePackages = { "io.sunbit.app.dao", "io.sunbit.app.security.dao" })
public class ExpenseNoteAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpenseNoteAppApplication.class, args);
	}

}
