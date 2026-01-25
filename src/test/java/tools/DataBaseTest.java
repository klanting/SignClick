package tools;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;

@Testcontainers
public class DataBaseTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15.1-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    public static Connection getConnection() {
        assert connection != null;
        return connection;
    }

    protected static Connection connection;

    @BeforeAll
    public static void initDb() throws Exception {
        postgres.start();
        connection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
    }

    @AfterAll
    public static void shutdown() throws Exception {
        connection.close();
    }
}