package io.retrorock.theory.example;

import io.retrorock.theory.operations.Insert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;

public class UserRepositoryTest {
    private DataSource dataSource;

    @Before
    public void setup() throws Exception {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:theory;DB_CLOSE_DELAY=-1");
        ds.setUsername("sa");
        ds.setPassword("");
        this.dataSource = ds;

        try (Connection c = dataSource.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE users (\n" +
                    "  id INTEGER PRIMARY KEY AUTO_INCREMENT,\n" +
                    "  email VARCHAR(255),\n" +
                    "  name VARCHAR(255)\n" +
                    ")");

            s.execute("INSERT INTO users (email, name) VALUES ('alice@example.com', 'Alice')");
        }
    }

    @After
    public void teardown() throws Exception {
        try (Connection c = dataSource.getConnection(); Statement s = c.createStatement()) {
            s.execute("DROP ALL OBJECTS");
        }
    }

    @Test
    public void it_inserts_and_queries_users() {
        UserRepository repo = new UserRepository(dataSource);

        // List existing (1 seeded)
        List<User> initial = repo.list();
        assertEquals(1, initial.size());
        assertEquals("Alice", initial.get(0).getName());

        // Insert a new user via Operation API and capture PK
        User bob = new User();
        bob.setEmail("bob@example.com");
        bob.setName("Bob");
        Integer id = repo.persist(bob, new Insert());
        assertNotNull(id);
        assertTrue(id > 0);

        // Find by id and assert mapping via aliases
        User found = repo.find(id);
        assertEquals("Bob", found.getName());
        assertEquals("bob@example.com", found.getEmail());

        // List all (should be 2)
        List<User> after = repo.list();
        assertEquals(2, after.size());
    }
}
