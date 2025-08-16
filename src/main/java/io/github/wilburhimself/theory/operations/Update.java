package io.github.wilburhimself.theory.operations;

import io.github.wilburhimself.theory.base.Operation;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class Update extends Operation {

    @Override
    public String toString() {
        return super.toString();
    }

    public Map<String, Object> auditValues(Map<String, Object> values, Integer userId) {
        values.put("updated_date", new Date());
        values.put("updated_user_id", userId);
        return values;
    }

    public void compileString() {
        modelMapper.addSqlPart("UPDATE ");
        if (into == null) {
            modelMapper.addSqlPart(tableInfo.get("name"));
        } else {
            modelMapper.addSqlPart(this.into);
        }

        modelMapper.addSqlPart(" SET ");
        Iterator entries = fields.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            modelMapper.addSqlPart(entry.getKey() + " = '" + entry.getValue() + "'");
            if (entries.hasNext()) {
                modelMapper.addSqlPart(", ");
            } else {
                modelMapper.addSqlPart(" ");
            }
        }
        modelMapper.addSqlPart(modelMapper.getWhere());


    }
}
