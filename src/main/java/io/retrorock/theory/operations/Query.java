package io.retrorock.theory.operations;

import io.retrorock.theory.components.Join;
import io.retrorock.theory.components.TableField;
import io.retrorock.theory.helpers.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;

public class Query {
    public ModelMapper db;
    public Query() {
         db = new ModelMapper();
    }

    public String toString() {
        return selectString();
    }

    public String selectString() {
        db.sqlStringBuffer.setLength(0);
        db.addSqlPart("SELECT ");
        processFieldsString();
        db.addSqlPart(" FROM " + db.getFrom().toString());
        processJoinString();
        db.addSqlPart(db.getWhere());

        db.addSqlPart(" ORDER BY " + db.getFrom().getIdentifier() + "." + db.getPrimaryKeyName() + " ASC");

        if (db.getLimit() != null) {
            db.addSqlPart(" LIMIT " + db.getLimit());
            db.setLimit(null);
        }
        String output = db.sqlStringBuffer.toString();
        db.reset();
        System.out.println(output);
        return output;
    }

    private void processJoinString() {
        ArrayList<Join> joins = db.getJoins();
        if (joins.size() > 0) {
            Collections.reverse(joins);
            for (Join join : joins) {
                String joinString = " " + join.toString();
                db.addSqlPart(joinString);
            }
        }
    }

    private void processFieldsString() {
        ArrayList<TableField> tableFields = db.getTableFields();
        if (tableFields.size() > 0) {
            Integer index = 1;
            for (TableField tableField : tableFields) {
                String fields = tableField.toString();
                if (index > 1) {
                    db.addSqlPart(", ");
                }
                db.addSqlPart(fields);
                index++;
            }
        }
    }
}
