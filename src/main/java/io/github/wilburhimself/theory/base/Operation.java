package io.github.wilburhimself.theory.base;

import io.github.wilburhimself.theory.interfaces.IOperation;
import io.github.wilburhimself.theory.helpers.ModelMapper;

import java.util.HashMap;
import java.util.Map;

public abstract class Operation implements IOperation {
    public ModelMapper modelMapper = new ModelMapper();
    protected Map<String, Object> fields = new HashMap<String, Object>();
    protected Map<String, String> tableInfo;
    protected String into;

    public void reset() {
        fields.clear();
        into = null;
        this.modelMapper.reset();
    }

    public Operation field(String key, Object value) {
        fields.put(key, value);
        return this;
    }

    public Operation setFields(Map<String, Object> fields) {
        this.fields = fields;
        return this;
    }

    public Operation setTable(String into) {
        this.into = into;
        return this;
    }

    public Operation setEntity(Model entity) {
        tableInfo = modelMapper.getTableinfo(entity.getClass());
        fields = entity.serialize();
        return this;
    }

    public String toString() {
        modelMapper.sqlStringBuffer.setLength(0);
        compileString();
        String output = modelMapper.sqlStringBuffer.toString();
        modelMapper.reset();

        return output;
    }
}
