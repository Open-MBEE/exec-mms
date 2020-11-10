package org.openmbee.mms.rdb.repositories.branch;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.openmbee.mms.data.domains.scoped.Branch;
import org.openmbee.mms.rdb.repositories.BaseRowMapper;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.jdbc.core.RowMapper;

public class BranchRowMapper extends BaseRowMapper implements RowMapper<Branch> {

    public Branch mapRow(ResultSet rs, int rowNum) throws SQLException {

        BeanMap beanMap = BeanMap.create(new Branch());

        ResultSetMetaData resultSetMetaData = rs.getMetaData();

        for (int colnum = 1; colnum <= resultSetMetaData.getColumnCount(); colnum++) {
            String field = getFieldName(resultSetMetaData.getColumnName(colnum), beanMap.keySet());

            if (field == null) {
                continue;
            }

            if (rs.getObject(colnum) != null) {
                if (rs.getObject(colnum) instanceof Timestamp) {
                    beanMap.put(field, rs.getTimestamp(colnum).toInstant());
                } else {
                    beanMap.put(field, rs.getObject(colnum));
                }
            }
        }

        return (Branch) beanMap.getBean();
    }
}
