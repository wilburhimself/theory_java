package io.github.wilburhimself.theory.operations;

import io.github.wilburhimself.theory.base.Operation;

import java.util.Map;

public class Delete extends Operation {
    public void compileString() {
        modelMapper.addSqlPart("DELETE FROM ");
        modelMapper.addSqlPart(this.into);
        modelMapper.addSqlPart(" AS TABLE ");
        modelMapper.addSqlPart(modelMapper.getWhere());
    }

    public Map<String, Object> auditValues(Map<String, Object> values, Integer userId) {
        return null;
    }
}
