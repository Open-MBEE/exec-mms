package org.openmbee.sdvc.crud.repositories;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import org.springframework.cglib.beans.BeanMap;

public class BaseRowMapper {

    public HashMap<String, BeanMap> getBeanMapping(ResultSet rs) throws SQLException {

        HashMap<String, BeanMap> beansByName = new HashMap<>();
        ResultSetMetaData resultSetMetaData = rs.getMetaData();

        for (int colnum = 1; colnum <= resultSetMetaData.getColumnCount(); colnum++) {

            String table = resultSetMetaData.getColumnName(colnum).split("\\.")[0];
            String field = resultSetMetaData.getColumnName(colnum).split("\\.")[1];

            BeanMap beanMap = beansByName.get(table);

            if (rs.getObject(colnum) != null) {
                beanMap.put(field, rs.getObject(colnum));
            }
        }

        return beansByName;
    }
}
