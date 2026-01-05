package io.sunbit.app.test.support.config;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.sunbit.app.test.support.dialect.H2FriendlyPostgreSQLDialect;

/**
 * Overrides the Hibernate dialect only for the test profile so H2 skips
 * PostgreSQL-specific DDL statements emitted before table drops.
 */
@Configuration
@Profile("test")
public class TestHibernateConfig {

    @Bean
    HibernatePropertiesCustomizer testDialectCustomizer() {
        return hibernateProperties -> hibernateProperties.put(
                "hibernate.dialect",
                H2FriendlyPostgreSQLDialect.class.getName());
    }
}
