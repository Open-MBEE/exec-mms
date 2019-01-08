package org.openmbee.sdvc.crud.repositories.node;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.repositories.BaseRowMapper;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.jdbc.core.RowMapper;

public class NodeRowMapper extends BaseRowMapper implements RowMapper<Node> {

    public Node mapRow(ResultSet rs, int rowNum) throws SQLException {
        BeanMap beanMap = BeanMap.create(new Node());
        ResultSetMetaData resultSetMetaData = rs.getMetaData();

        for (int colnum = 1; colnum <= resultSetMetaData.getColumnCount(); colnum++) {

            String field = getFieldName(resultSetMetaData.getColumnName(colnum), beanMap.keySet());
            if (field == null) {
                continue;
            }

            Object column = rs.getObject(colnum);
            if (column != null) {
                if (column instanceof Timestamp) {
                    beanMap.put(field, ((Timestamp) column).toInstant());
                } else {
                    beanMap.put(field, column);
                }
            }
        }

        return (Node) beanMap.getBean();
    }
}
