package io.github.wilburhimself.theory.components;

public class From {
    private String tableName;
    private String identifier;

    public From(String tableName, String identifier) {
        this.tableName = tableName;
        this.identifier = identifier;
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

    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(this.tableName);
        if (!(this.identifier == null) && !this.identifier.isEmpty()) {
            output.append(" AS ").append(this.identifier);
        }
        return output.toString();
    }
}
