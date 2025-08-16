package io.github.wilburhimself.theory.operations;

import com.google.common.base.Joiner;
import io.github.wilburhimself.theory.base.Operation;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;

public class Insert extends Operation {

    public Map<String, Object> auditValues(Map<String, Object> values, Integer userId) {
        values.put("created_date", new Date());
        values.put("updated_date", new Date());
        values.put("created_user_id", userId);
        values.put("updated_user_id", userId);
        return values;
    }

    public void compileString()  {
        modelMapper.addSqlPart("INSERT INTO ");
        if (into == null) {
            modelMapper.addSqlPart(tableInfo.get("name"));
        } else {
            modelMapper.addSqlPart(this.into);
        }
        modelMapper.addSqlPart(" (");
        modelMapper.addSqlPart(Joiner.on(",").join(fields.keySet()));
        modelMapper.addSqlPart(")");
        modelMapper.addSqlPart(" VALUES (");
        String joined = "'" + StringUtils.join(fields.values(),"','") + "'";
        modelMapper.addSqlPart(joined);
        modelMapper.addSqlPart(")");
    }
}
