package org.openmbee.sdvc.crud.repositories.node;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Set;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.domains.NodeType;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.jdbc.core.RowMapper;

public class NodeRowMapper implements RowMapper {

    static final String NODES = "nodes";

    public Node mapRow(ResultSet rs, int rowNum) throws SQLException {

        HashMap<String, BeanMap> beansByName = new HashMap<>();

        beansByName.put(NODES, BeanMap.create(new Node()));
        Set nodeKeys = beansByName.get(NODES).keySet();

        ResultSetMetaData resultSetMetaData = rs.getMetaData();

        for (int colnum = 1; colnum <= resultSetMetaData.getColumnCount(); colnum++) {

            String[] arr = resultSetMetaData.getColumnName(colnum).split("\\.");

            String table = NODES;
            String field = null;

            if (arr.length < 2) {
                field = getFieldName(arr[0], nodeKeys);
            } else {
                table = arr[0];
                field = getFieldName(arr[1], nodeKeys);
            }

            if (field == null) {
                continue;
            }

            BeanMap beanMap = beansByName.get(table);
            Object column = rs.getObject(colnum);
            if (column != null) {
                if (column instanceof Timestamp) {
                    beanMap.put(field, ((Timestamp) column).toInstant());
                } else {
                    beanMap.put(field, column);
                }
            }
        }

        return (Node) beansByName.get(NODES).getBean();
    }

    private String getFieldName(String field, Set keys) {
        for (Object key : keys) {
            if (key.toString().equalsIgnoreCase(field)) {
                return key.toString();
            }
        }
        return null;
    }
}
