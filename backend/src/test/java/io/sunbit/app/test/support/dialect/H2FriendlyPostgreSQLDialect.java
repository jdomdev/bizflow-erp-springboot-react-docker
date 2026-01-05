package io.sunbit.app.test.support.dialect;

import java.util.Arrays;
import java.util.Map;

import org.hibernate.dialect.PostgreSQLDialect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Custom PostgreSQL dialect that skips the "set client_min_messages" statement.
 * H2 does not understand that Postgres-specific command even when running in
 * PostgreSQL compatibility mode, so we simply avoid emitting it before drops.
 */
public class H2FriendlyPostgreSQLDialect extends PostgreSQLDialect {

    @Override
    public String getBeforeDropStatement() {
        return null;
    }

    /**
     * Registers the custom dialect automatically for the {@code test} profile so
     * we avoid touching ignored property files.
     */
    public static class TestDialectEnvironmentPostProcessor
            implements EnvironmentPostProcessor, Ordered {

        private static final String DIALECT_PROPERTY = "spring.jpa.database-platform";
        private static final String PROPERTY_SOURCE_NAME = "testDialectOverrides";

        @Override
        public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
            if (Arrays.stream(environment.getActiveProfiles()).noneMatch("test"::equals)) {
                return;
            }

            if (environment.containsProperty(DIALECT_PROPERTY)) {
                return;
            }

            Map<String, Object> properties = Map.of(DIALECT_PROPERTY, H2FriendlyPostgreSQLDialect.class.getName());
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }
    }
}
