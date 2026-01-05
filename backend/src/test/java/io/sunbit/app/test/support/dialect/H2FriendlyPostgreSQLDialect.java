package io.sunbit.app.test.support.dialect;

import org.hibernate.dialect.PostgreSQLDialect;

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
}
