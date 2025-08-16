package io.github.wilburhimself.theory.example;

import io.github.wilburhimself.theory.base.Repository;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

public class UserRepository extends Repository<User> {
    public UserRepository(DataSource dataSource) {
        this.setEntity(User.class);
        this.setMapper((RowMapper<User>) (rs, rowNum) -> (User) new User().map(rs, rowNum));
        this.setTableName("users");
        this.setPrimaryKeyName("id");
        setDataSource(dataSource);
    }
}
