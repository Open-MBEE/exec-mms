package org.openmbee.sdvc.crud.repositories.commit;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import org.openmbee.sdvc.crud.domains.Commit;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.jdbc.core.RowMapper;

public class CommitRowMapper implements RowMapper {

    public Commit mapRow(ResultSet rs, int rowNum) throws SQLException {
        HashMap<String, BeanMap> beansByName = new HashMap<>();

        beansByName.put("commits", BeanMap.create(new Commit()));

        ResultSetMetaData resultSetMetaData = rs.getMetaData();

        for (int colnum = 1; colnum <= resultSetMetaData.getColumnCount(); colnum++) {

            String[] arr = resultSetMetaData.getColumnName(colnum).split("\\.");

            if (arr.length < 2) {
                continue;
            }

            String table = resultSetMetaData.getColumnName(colnum).split("\\.")[0];
            String field = resultSetMetaData.getColumnName(colnum).split("\\.")[1];

            BeanMap beanMap = beansByName.get(table);

            if (rs.getObject(colnum) != null) {
                beanMap.put(field, rs.getObject(colnum));
            }
        }

        return (Commit) beansByName.get("commits").getBean();
    }
}
