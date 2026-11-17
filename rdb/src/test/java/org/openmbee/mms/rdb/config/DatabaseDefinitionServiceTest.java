package org.openmbee.mms.rdb.config;

import org.junit.Before;
import org.junit.Test;
import org.openmbee.mms.core.config.ContextObject;
import org.openmbee.mms.rdb.datasources.CrudDataSources;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DatabaseDefinitionServiceTest {

    private static final String TERMINATE_SQL =
        "SELECT pid, pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = ?";

    private Environment env;
    private Connection connection;
    private Statement statement;
    private PreparedStatement terminate;
    private DatabaseDefinitionService service;

    @Before
    public void setUp() throws SQLException {
        env = mock(Environment.class);
        connection = mock(Connection.class);
        statement = mock(Statement.class);
        terminate = mock(PreparedStatement.class);
        DataSource dataSource = mock(DataSource.class);
        CrudDataSources crudDataSources = mock(CrudDataSources.class);

        when(env.getProperty("rdb.project.prefix", "")).thenReturn("mms");
        when(crudDataSources.getDataSource(ContextObject.DEFAULT_PROJECT)).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(connection.prepareStatement(TERMINATE_SQL)).thenReturn(terminate);
        when(connection.nativeSQL(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        service = new DatabaseDefinitionService();
        service.setEnv(env);
        service.setCrudDataSources(crudDataSources);
    }

    @Test
    public void dropsTheSameDatabaseNameAsBeforeForAValidProjectId() throws SQLException {
        service.deleteProjectDatabase("my-project_1");

        verify(statement).executeUpdate("DROP DATABASE \"mms_my-project_1\"");
        verify(connection, never()).prepareStatement(anyString());
    }

    @Test
    public void bindsTheDatabaseNameWhenTerminatingPostgresBackends() throws SQLException {
        when(env.getProperty("spring.datasource.driver-class-name")).thenReturn("org.postgresql.Driver");

        service.deleteProjectDatabase("p1");

        verify(terminate).setString(1, "\"mms_p1\"");
        verify(terminate).execute();
        verify(statement).executeUpdate("DROP DATABASE \"mms_p1\"");
    }

    @Test
    public void aQuoteInTheProjectIdCannotCloseTheIdentifier() throws SQLException {
        service.deleteProjectDatabase("p1\"; DROP DATABASE \"postgres");

        verify(statement).executeUpdate("DROP DATABASE \"mms_p1\"\"; DROP DATABASE \"\"postgres\"");
    }
}
