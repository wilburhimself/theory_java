# Theory (Java)

[![CI](https://github.com/OWNER/REPO/actions/workflows/ci.yml/badge.svg)](https://github.com/OWNER/REPO/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.retrorock/theory.svg)](https://central.sonatype.com/artifact/io.retrorock/theory)

Note: Replace `OWNER/REPO` with your GitHub org/repo, and adjust Maven Central coordinates when published.

A small, annotation-driven data access library built on Spring JDBC. It provides:

- Model mapping via annotations (`@Table`, `@Column`, `@PrimaryKey`, `@BelongsTo`) on POJOs.
- A lightweight `Repository<T>` base with common query and persistence helpers.
- A simple SQL builder through `Query` and `ModelMapper` to generate SELECTs with joins and limits.

This is intended for pragmatic, minimal data access without a heavy ORM.

## Requirements

- Java 8+
- Maven 3+
- Spring JDBC 4.2.x (pulled via `pom.xml`)

## Project Structure

- `pom.xml` — Maven build and dependencies
- `src/main/java/io/retrorock/theory/` — library source
  - `annotations/` — `@Table`, `@Column`, `@PrimaryKey`, `@BelongsTo`
  - `base/` — `Model`, `Repository`, `Operation`
  - `components/` — SQL building blocks (e.g., `From`, `Join`)
  - `helpers/` — mappers and utilities (e.g., `ModelMapper`, `RowMapperHelper`)
  - `interfaces/` — `IRepository`, `IOperation`
  - `operations/` — `Query`, `Insert`, `Update`, `Delete`

## Installation

Build and install to your local Maven repository:

```bash
mvn clean install
```

Then, in a separate project, add the dependency (adjust version if needed):

```xml
<dependency>
  <groupId>io.retrorock</groupId>
  <artifactId>theory</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

## Quick Start

### 1) Define a Model

Annotate your entity with table/column mapping. Extend `Model` to get helpers like `serialize()` and JDBC `RowMapper` behavior.

```java
import io.retrorock.theory.annotations.*;
import io.retrorock.theory.base.Model;

@Table(name = "users", alias = "u")
public class User extends Model {
  @PrimaryKey
  @Column(name = "id", alias = "u_id")
  private Integer id;

  @Column(name = "email", alias = "u_email")
  private String email;

  @Column(name = "name", alias = "u_name")
  private String name;

  // getters/setters ...
}
```

Relationships can be declared with `@BelongsTo` on a field referencing another `Model`.

### 2) Create a Repository

Extend `Repository<T>` and wire a `JdbcTemplate` (via Spring or manually through `JdbcDaoSupport`).

```java
import io.retrorock.theory.base.Repository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class UserRepository extends Repository<User> {
  public UserRepository() {
    this.setEntity(User.class);
    this.setMapper((RowMapper<User>) new BeanPropertyRowMapper<>(User.class));
    this.setTableName("users");
    this.setPrimaryKeyName("id");
  }
}
```

Inject a `DataSource` so `JdbcDaoSupport` can provide `JdbcTemplate`:

```java
import javax.sql.DataSource;

public class UserRepository extends Repository<User> {
  public UserRepository(DataSource dataSource) {
    this(); // calls default ctor to set entity/mapper
    setDataSource(dataSource);
  }
}
```

### 3) Querying

Use the built-in `Query` builder. `Repository#list()` and `Repository#find(id)` leverage `Query` and your mapper.

```java
UserRepository repo = new UserRepository(dataSource);

// Find by primary key
User u = repo.find(1);

// List all
List<User> users = repo.list();

// Custom select with Query
var q = new io.retrorock.theory.operations.Query();
q.db.from(User.class)
    .fields(User.class)
    .where("u.email = '%s'", "alice@example.com")
    .limit(10);
List<User> result = repo.select(q, repo.mapper);
```

Note: `Query#selectString()` prints the generated SQL and resets internal state after building.

### 4) Insert/Update/Delete

`Repository` exposes `persist` helpers that accept an `Operation`:

```java
User user = new User();
user.setEmail("bob@example.com");
user.setName("Bob");

// Insert and return generated key
Integer id = repo.persist(user, new io.retrorock.theory.operations.Insert());
user.identify(id);

// Update example
var update = new io.retrorock.theory.operations.Update();
repo.persist(user, update);

// Raw SQL
repo.persist("DELETE FROM users WHERE id = 123");
```

## Minimal Runnable Example

This repo includes a minimal example using H2 in-memory DB under `src/test/java/io/retrorock/theory/example/`:

- `User` model: `src/test/java/io/retrorock/theory/example/User.java`
- `UserRepository`: `src/test/java/io/retrorock/theory/example/UserRepository.java`
- Test: `src/test/java/io/retrorock/theory/example/UserRepositoryTest.java`

Run just the example test:

```bash
mvn -Dtest=io.retrorock.theory.example.UserRepositoryTest test
```

## Spring Configuration

In XML or Java config, provide a `DataSource` and wire your repository. Example (Java config):

```java
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class AppConfig {
  @Bean
  public DataSource dataSource() {
    DriverManagerDataSource ds = new DriverManagerDataSource();
    ds.setDriverClassName("org.postgresql.Driver");
    ds.setUrl("jdbc:postgresql://localhost:5432/app");
    ds.setUsername("app");
    ds.setPassword("secret");
    return ds;
  }

  @Bean
  public UserRepository userRepository(DataSource ds) {
    return new UserRepository(ds);
  }
}
```

## Development

- Build: `mvn clean package`
- Tests: `mvn test`

### TDD Flow (suggested)

1. Write a failing test under `src/test/java/` that describes desired behavior.
2. Run tests: `mvn test` and watch it fail.
3. Implement the minimal code in `src/main/java/` to make it pass.
4. Refactor ruthlessly while keeping tests green.
5. Repeat.

## Notes

- This library targets Spring 4.2.x APIs as specified in `pom.xml`.
- Some operations rely on column aliases matching the `@Column(alias=...)` convention during mapping.

## License

MIT — see `LICENSE` for details.
