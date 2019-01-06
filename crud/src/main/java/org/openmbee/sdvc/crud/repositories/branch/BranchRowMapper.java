package org.openmbee.sdvc.crud.repositories.branch;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Set;
import org.openmbee.sdvc.crud.domains.Branch;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.jdbc.core.RowMapper;

public class BranchRowMapper implements RowMapper<Branch> {

    static final String BRANCHES = "branches";

    public Branch mapRow(ResultSet rs, int rowNum) throws SQLException {
        HashMap<String, BeanMap> beansByName = new HashMap();

        beansByName.put(BRANCHES, BeanMap.create(new Branch()));

        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        Set branchKeys = beansByName.get(BRANCHES).keySet();

        for (int colnum = 1; colnum <= resultSetMetaData.getColumnCount(); colnum++) {

            String[] arr = resultSetMetaData.getColumnName(colnum).split("\\.");

            String table = BRANCHES;
            String field = null;

            if (arr.length < 2) {
                field = getFieldName(arr[0], branchKeys);
            } else {
                table = arr[0];
                field = getFieldName(arr[1], branchKeys);
            }

            if (field == null) {
                continue;
            }

            BeanMap beanMap = beansByName.get(table);

            if (rs.getObject(colnum) != null) {
                if (rs.getObject(colnum) instanceof Timestamp) {
                    beanMap.put(field, rs.getTimestamp(colnum).toInstant());
                } else {
                    beanMap.put(field, rs.getObject(colnum));
                }
            }
        }

        return (Branch) beansByName.get(BRANCHES).getBean();
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
