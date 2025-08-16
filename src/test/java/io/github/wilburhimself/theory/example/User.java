package io.github.wilburhimself.theory.example;

import io.github.wilburhimself.theory.annotations.Column;
import io.github.wilburhimself.theory.annotations.PrimaryKey;
import io.github.wilburhimself.theory.annotations.Table;
import io.github.wilburhimself.theory.base.Model;

@Table(name = "users", alias = "u")
public class User extends Model {
    @PrimaryKey
    @Column(name = "id", alias = "u_id")
    private Integer id;

    @Column(name = "email", alias = "u_email")
    private String email;

    @Column(name = "name", alias = "u_name")
    private String name;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
