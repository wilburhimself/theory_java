package io.retrorock.theory.components;

public class Join {
    private JoinType joinType;
    private String tableName = "";
    private String identifier = "";
    private String conditional = "";

    public Join(String tableName, String identifier, String conditional) {
        this.conditional = conditional;
        this.identifier = identifier;
        this.tableName = tableName;
        this.joinType = JoinType.LEFT;
    }

    public Join(String tableName, String identifier, String conditional, JoinType joinType) {
        this.conditional = conditional;
        this.identifier = identifier;
        this.joinType = joinType;
        this.tableName = tableName;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getConditional() {
        return conditional;
    }

    public void setConditional(String conditional) {
        this.conditional = conditional;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (!(this.joinType.toString().equals("NONE"))) {
            buffer.append(this.joinType).append(" ");
        }
        buffer.append("JOIN ").append(this.tableName);
        if (!this.identifier.equals("")) {
            buffer.append(" AS ").append(this.identifier);
        }
        buffer.append(" ON ").append(this.conditional);

        return buffer.toString();
    }
}
