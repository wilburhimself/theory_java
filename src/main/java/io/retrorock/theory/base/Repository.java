package io.retrorock.theory.base;

import io.retrorock.theory.interfaces.IRepository;
import io.retrorock.theory.operations.Query;
import io.retrorock.theory.operations.Delete;
import io.retrorock.theory.operations.Insert;
import io.retrorock.theory.operations.Update;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

abstract public class Repository<T> extends JdbcDaoSupport implements IRepository<T> {
    protected Query query = new Query();
    protected Operation insert = new Insert();
    protected Operation update = new Update();
    protected Operation delete = new Delete();
    protected RowMapper<T> mapper;
    protected Class<T> entity;
    protected Boolean loadRelated = true;

    protected String tableName;
    protected String primaryKeyName;

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setPrimaryKeyName(String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
    }

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setLoadRelated(Boolean loadRelated) {
        this.loadRelated = loadRelated;
    }

    public void setEntity(Class<T> entity) {
        this.entity = entity;
    }

    public void setMapper(RowMapper<T> mapper) {
        this.mapper = mapper;
    }

    public Boolean shouldLoadRelated() {
        return loadRelated;
    }

    public T find(Integer id) {
        query.db.from(this.entity).pk(id);
        return loadRelated(getJdbcTemplate().queryForObject(query.toString(), mapper));
    }

    public T find(Query query, RowMapper<T> mapper) {
        return loadRelated(getJdbcTemplate().queryForObject(query.selectString(), mapper));
    }

    public List<T> list() {
        query.db.from(this.entity);
        return select(query, mapper);
    }

    public List<T> select(Query query, RowMapper mapper) {
        return loadRelated(getJdbcTemplate().query(query.selectString(), mapper));
    }

    public T save(T t) {
        return null;
    }

    public void persist(String sql) {
        getJdbcTemplate().update(sql);
    }

    public void persist(Operation operation) {
        persist(operation.toString());
    }

    public Integer persist(final String sql, final String generatedKey) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                return connection.prepareStatement(sql, new String[]{generatedKey});
            }
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void persist(String tableName, Map values, Operation operation) {
        operation.setTable(tableName);
        operation.setFields(values);
        persist(operation.toString());
    }

    public Integer persist(String tableName, Map values, final String generatedKey) {
        insert.setTable(tableName);
        insert.setFields(values);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                return connection.prepareStatement(insert.toString(), new String[]{generatedKey});
            }
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public Integer persist(Model entity, Operation operation) {
        operation.setEntity(entity);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = operation.toString();
        System.out.println(sql);
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                return connection.prepareStatement(sql, new String[]{primaryKeyName});
            }
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public T update(T t) {
        return null;
    }

    public void remove(T t) {}

    public T loadRelated(T t) {
        return t;
    }

    public List<T> loadRelated(List<T> t) {
        List<T> output = new ArrayList<T>();
        for (T item : t) {
            output.add(loadRelated(item));
        }
        return output;
    }
}
