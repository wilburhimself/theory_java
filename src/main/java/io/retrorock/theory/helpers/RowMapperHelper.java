package io.retrorock.theory.helpers;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class RowMapperHelper {

    private ResultSetMetaData metaData;
    private ResultSet resultSet;

    public RowMapperHelper(ResultSet resultSet) throws SQLException {
        this.resultSet = resultSet;
        metaData = resultSet.getMetaData();
    }

    public Boolean fieldExists(String fieldName) throws SQLException {
        Boolean result = false;
        for (int i = 1; i <= this.metaData.getColumnCount(); i ++) {
            String label = metaData.getColumnLabel(i);
            if (label != null && label.equalsIgnoreCase(fieldName)) {
                Object fieldObject = resultSet.getObject(label);
                if (fieldObject != null) {
                    result = true;
                }
            }
        }
        return result;
    }
}
