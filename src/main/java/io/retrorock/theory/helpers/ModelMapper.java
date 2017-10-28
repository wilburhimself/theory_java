package io.retrorock.theory.helpers;

import io.retrorock.theory.components.*;
import io.retrorock.theory.annotations.BelongsTo;
import io.retrorock.theory.annotations.Column;
import io.retrorock.theory.annotations.PrimaryKey;
import io.retrorock.theory.annotations.Table;
import io.retrorock.theory.components.*;

import java.lang.reflect.Field;
import java.util.*;

public class ModelMapper {
    private String tableAlias = "";
    private String tableName = "";
    private String primaryKey = "";
    private String primaryKeyName = "";
    private String namePrefix = "";
    private ArrayList<TableField> tableFields = new ArrayList<TableField>();
    private ArrayList<Join> joins = new ArrayList<Join>();
    private ArrayList<Where> wheres = new ArrayList<Where>();
    private From from;
    private Integer limit;
    private String where = "";
    public StringBuffer sqlStringBuffer = new StringBuffer();
    private ArrayList<String> readedEntities = new ArrayList<String>();

    public ModelMapper limit(Integer limitNumber) {
        this.limit = limitNumber;
        return this;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public ModelMapper setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public String getWhere() {
        return where;
    }

    public ModelMapper setWhere(String where) {
        this.where = where;
        return this;
    }

    public From getFrom() {
        return from;
    }

    public ModelMapper setFrom(From from) {
        this.from = from;
        return this;
    }

    public ModelMapper removeField(String fieldName) {
        Iterator<TableField> fields = this.tableFields.iterator();
        while (fields.hasNext()) {
            TableField field = fields.next();
            if (field.getName().equals(fieldName)) {
                this.tableFields.remove(field);
                return this;
            }
        }
        return this;
    }

    public ArrayList<Join> getJoins() {
        return joins;
    }

    public void setJoins(ArrayList<Join> joins) {
        this.joins = joins;
    }

    public ArrayList<TableField> getTableFields() {
        return tableFields;
    }

    public void setTableFields(ArrayList<TableField> tableFields) {
        this.tableFields = tableFields;
    }

    public ModelMapper setPrimaryKeyName(String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
        return this;
    }

    public ModelMapper pk(Integer id) {
        ArrayList<String> fields = new ArrayList<String>();
        fields.add(tableAlias+"."+primaryKey+" =  " + id);
        this.where(fields);
        return this;
    }

    public ModelMapper pk(String id) {
        ArrayList<String> fields = new ArrayList<String>();
        fields.add(tableAlias+"."+primaryKey+" =  '" + id + "'");
        this.where(fields);
        return this;
    }

    public ModelMapper innerJoin(String tableName, String identifier, String conditional) {
        this.joins.add(0, new Join(tableName, identifier, conditional, JoinType.INNER));
        return this;
    }

    public ModelMapper leftJoin(String tableName, String identifier, String conditional) {
        this.joins.add(0, new Join(tableName, identifier, conditional, JoinType.LEFT));
        return this;
    }

    public ModelMapper rightJoin(String tableName, String identifier, String conditional) {
        this.joins.add(0, new Join(tableName, identifier, conditional, JoinType.RIGHT));
        return this;
    }

    public ModelMapper fullJoin(String tableName, String identifier, String conditional) {
        this.joins.add(0, new Join(tableName, identifier, conditional, JoinType.RIGHT));
        return this;
    }

    public ModelMapper join(String tableName, String identifier, String conditional, JoinType joinType) {
        this.joins.add(0, new Join(tableName, identifier, conditional, joinType));
        return this;
    }

    public ModelMapper where(String conditional) {
        this.where = " WHERE " + conditional;
        return this;
    }

    public ModelMapper where(String conditional, Object... params) {
        this.where = String.format(" WHERE " + conditional, params);
        return this;
    }

    public ModelMapper where(ArrayList<String> fields) {
        if (fields.size() > 0) {
            this.where = " WHERE ";
            for (String field : fields) {
                this.where += field;
            }
        }
        return this;
    }

    public ModelMapper reset() {
        this.tableAlias = "";
        this.primaryKey = "";
        this.tableName = "";
        this.tableFields.clear();
        this.joins.clear();
        this.wheres.clear();
        this.where = "";
        this.from = null;
        this.readedEntities = new ArrayList<String>();
        this.sqlStringBuffer.setLength(0);
        return new ModelMapper();
    }

    public Map<String, String> getTableinfo(Class entity) {
        Map<String, String> tableInfo = new HashMap<String, String>();
        if (entity.isAnnotationPresent(Table.class)) {
            Table table = (Table) entity.getAnnotation(Table.class);
            tableInfo.put("name", table.name());
            tableInfo.put("tableAlias", table.alias());
        }
        return tableInfo;
    }

    private void setTableInfo(Class entity) {
        Map<String, String> tableInfo = getTableinfo(entity);
        this.tableName = tableInfo.get("name");
        this.tableAlias = tableInfo.get("tableAlias");
        String tableAlias = tableInfo.get("tableAlias");
    }

    private String getTableAlias(Class entity, String alias) {
        Map<String, String> tableInfo = getTableinfo(entity);
        return alias == null ? tableInfo.get("tableAlias") : alias;
    }

    public ModelMapper from(String tableName, String identifier) {
        this.from = new From(tableName, identifier);
        this.tableName = tableName;
        this.tableAlias = identifier;
        return this;
    }

    public ModelMapper from(String tableName, String identifier, String primaryKeyName) {
        this.from = new From(tableName, identifier);
        this.tableName = tableName;
        this.tableAlias = identifier;
        this.primaryKeyName = primaryKeyName;
        return this;
    }

    public ModelMapper from(Class entity) {
        this.setTableInfo(entity);
        this.fields(entity);
        this.from(tableName, tableAlias);
        return this;
    }

    public ModelMapper fields(Class entity, String alias) {
        String tableAlias = getTableAlias(entity, alias);

        addToRead(tableAlias);
        readFields(entity, alias);

        this.namePrefix = null;
        return this;
    }

    public ModelMapper fields(Class entity) {
        Map<String, String> tableInfo = getTableinfo(entity);
        this.fields(entity, tableInfo.get(tableAlias));
        return this;
    }

    /**
     * add a tableField to the queryBuilder stack
     * @param name:String
     * @return QueryBuilder
     */
    public ModelMapper field(String name) {
        this.tableFields.add(new TableField(name));
        return this;
    }

    /**
     * @param prefix:String
     * @param fieldName:String
     * @return QueryBuilder
     */
    public ModelMapper field(String prefix, String fieldName) {
        this.tableFields.add(new TableField(prefix, fieldName));
        return this;
    }

    /**
     * @param prefix:String
     * @param fieldName:String
     * @param identifier:String
     * @return QueryBuilder
     */
    public ModelMapper field(String prefix, String fieldName, String identifier) {
        this.tableFields.add(new TableField(prefix, fieldName, identifier));
        return this;
    }

    public ModelMapper field(String prefix, String namePrefix, String fieldName, String identifier) {
        this.tableFields.add(new TableField(prefix, namePrefix, fieldName, identifier));
        return this;
    }

    public StringBuffer addSqlPart(String part) {
        this.sqlStringBuffer.append(part);
        return this.sqlStringBuffer;
    }

    private Integer getFrequency(String tableAlias) {
        return Collections.frequency(this.readedEntities, tableAlias);
    }
    private void addToRead(String iTableAlias) {
        this.readedEntities.add(iTableAlias);
    }

    private void readFields(Class entity, String alias) {
        String tableAlias = getTableAlias(entity, alias);
        for (Field field : entity.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Column fieldAnnotation =
                        field.getAnnotation(Column.class);
                this.field(tableAlias, this.namePrefix, fieldAnnotation.name(),
                        fieldAnnotation.alias());
                if (field.isAnnotationPresent(PrimaryKey.class) && primaryKey.equals("")) {
                    this.primaryKey = fieldAnnotation.alias();
                    this.primaryKeyName = fieldAnnotation.name();
                }
            }

            if (field.isAnnotationPresent(BelongsTo.class)) {
                BelongsTo joinField = field.getAnnotation(BelongsTo.class);

                Class joinedEntity = joinField.entity();
                Map<String, String> joinedTableInfo = getTableinfo(joinedEntity);

                String joinFieldName;
                String joinFieldIdentifier;
                if (joinField.fieldName().equals("[unassigned]")) {
                    joinFieldName = joinField.name();
                } else {
                    joinFieldName = joinField.fieldName();
                }

                addToRead(joinedTableInfo.get("tableAlias"));

                if (joinField.identifier().equals("[unassigned]")) {
                    joinFieldIdentifier = joinedTableInfo.get("tableAlias");
                } else {
                    joinFieldIdentifier = joinField.identifier();
                    addToRead(joinFieldIdentifier);
                }

                Integer frequency = getFrequency(joinFieldIdentifier);
                joinFieldIdentifier = joinFieldIdentifier + frequency;

                if (!joinField.namePrefix().equals("[unassigned]")) {
                    this.namePrefix = joinField.namePrefix();
                }

                leftJoin(joinedTableInfo.get("name"),
                        joinFieldIdentifier,
                        tableAlias + "." + joinField.name() + "=" +
                                joinFieldIdentifier + "" +
                                "." + joinFieldName);

                this.readFields(joinedEntity, joinFieldIdentifier);
                this.namePrefix = null;
            }
        }
    }
}
