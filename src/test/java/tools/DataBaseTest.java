package tools;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

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
        String reset = "DROP schema public CASCADE";
        String recreate = "CREATE schema public";

        try {
            PreparedStatement ps = connection.prepareStatement(reset);
            ps.executeUpdate();

            ps = connection.prepareStatement(recreate);
            ps.executeUpdate();

        }catch (Exception e){

        }


        connection.close();
    }
}