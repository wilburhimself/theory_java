package io.github.wilburhimself.theory.components;

public class TableField {
    private String prefix;
    private String namePrefix;
    private String name;
    private String identifier;

    public TableField(String prefix, String name, String identifier) {
        this.prefix = prefix;
        this.name = name;
        this.identifier = identifier;
    }

    public TableField(String prefix, String name) {
        this.prefix = prefix;
        this.name = name;
        this.identifier = null;
    }

    public TableField(String prefix, String namePrefix, String name, String identifier) {
        this.prefix = prefix;
        this.namePrefix = namePrefix;
        this.name = name;
        this.identifier = identifier;
    }

    public TableField(String name) {
        this.name = name;
        this.prefix = null;
        this.identifier = null;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        if (!(this.prefix == null) && !this.prefix.isEmpty()) {
            output.append(this.prefix).append(".");
        }

        output.append(this.name);
        if (!(this.identifier == null) && !this.identifier.isEmpty()) {
            if (!(this.getNamePrefix() == null) && !this.getNamePrefix().isEmpty()) {
                this.identifier = this.getNamePrefix() + this.identifier;
            }

            output.append(" AS ").append(this.identifier);
        }
        return output.toString();
    }
}
