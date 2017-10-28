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
            if (metaData.getColumnLabel(i).equals(fieldName)) {
                Object fieldObject = resultSet.getObject(fieldName);
                if (fieldObject != null) {
                    result = true;
                }
            }
        }
        return result;
    }
}
