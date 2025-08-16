package io.github.wilburhimself.theory.base;

import io.github.wilburhimself.theory.annotations.BelongsTo;
import io.github.wilburhimself.theory.annotations.Column;
import io.github.wilburhimself.theory.annotations.PrimaryKey;
import io.github.wilburhimself.theory.helpers.RowMapperHelper;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class Model implements RowMapper<Model> {
    public Model() {}

    public Integer identify() {
        Integer output = 0;
        for (Field field: this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                Object value = null;
                try {
                    value = PropertyUtils.getProperty(this, field.getName());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                if (value != null) {
                    output = (Integer) value;
                }
            }
        }
        return output;
    }

    public void identify(Integer id) {
        for (Field field: this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                try {
                    PropertyUtils.setProperty(this, field.getName(), id);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Model map(ResultSet rs, int rowNum, String prefix) throws SQLException {
        RowMapperHelper rowMapperHelper = new RowMapperHelper(rs);
        for (Field field: this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Column fieldAnnotation = field.getAnnotation(Column.class);
                String fieldName = prefix + fieldAnnotation.alias();
                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    if (!rowMapperHelper.fieldExists(fieldName)) {
                        continue;
                    }
                }

                try {
                    if (rowMapperHelper.fieldExists(fieldName)) {
                        Object o = rs.getObject(fieldName);
                        PropertyUtils.setProperty(this, field.getName(), o);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }

            if (field.isAnnotationPresent(BelongsTo.class)) {
                BelongsTo belongsToAnnotation = field.getAnnotation(BelongsTo.class);
                try {
                    prefix = (belongsToAnnotation.namePrefix().equals("[unassigned]")) ? "" : belongsToAnnotation.namePrefix();
                    Model instance = (Model) belongsToAnnotation.entity().newInstance();
                    instance = instance.map(rs, rowNum, prefix);
                    PropertyUtils.setProperty(this, field.getName(), instance.map(rs, rowNum, prefix));
                    prefix = "";
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }

    public Model map(ResultSet rs, int rowNum) throws SQLException {
        return this.map(rs, rowNum, "");
    }

    public Map<String, Object> serialize() {
        Model entity = this;
        Map<String, Object> fields = new HashMap<String, Object>();
        try {
            for (Field field : entity.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Column.class)) {
                    Object value = PropertyUtils.getProperty(entity, field.getName());
                    Column fieldAnnotation = field.getAnnotation(Column.class);
                    if (field.isAnnotationPresent(PrimaryKey.class)) {
                        continue;
                    }

                    field.setAccessible(true);
                    if (value != null) {
                        fields.put(fieldAnnotation.name(), value);
                    }

                }

                if (field.isAnnotationPresent(BelongsTo.class)) {
                    field.setAccessible(true);
                    Object sentity = field.get(entity);
                    if(sentity != null) {
                        for (Field sfield : sentity.getClass().getDeclaredFields()) {
                            if (sfield.isAnnotationPresent(PrimaryKey.class)) {
                                Column sfieldAnnotation = sfield.getAnnotation(Column.class);
                                Object value = PropertyUtils.getProperty(sentity, sfield.getName());
                                if (value.equals(0)) {
                                    continue;
                                }
                                fields.put(sfieldAnnotation.name(), value);
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return fields;
    }

    public Model mapRow(ResultSet rs, int rowNum) throws SQLException {
        return this.map(rs, rowNum);
    }
}
