package org.openmbee.mms.rdb.repositories.commit;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.openmbee.mms.data.domains.scoped.Commit;
import org.openmbee.mms.data.domains.scoped.CommitType;
import org.openmbee.mms.rdb.repositories.BaseRowMapper;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.jdbc.core.RowMapper;

public class CommitRowMapper extends BaseRowMapper implements RowMapper<Commit> {

    public Commit mapRow(ResultSet rs, int rowNum) throws SQLException {
        BeanMap beanMap = BeanMap.create(new Commit());

        ResultSetMetaData resultSetMetaData = rs.getMetaData();

        for (int colnum = 1; colnum <= resultSetMetaData.getColumnCount(); colnum++) {

            String field = getFieldName(resultSetMetaData.getColumnName(colnum), beanMap.keySet());
            if (field == null) {
                continue;
            }

            if (rs.getObject(colnum) instanceof Timestamp) {
                beanMap.put(field, rs.getTimestamp(colnum).toInstant());
            } else if (rs.getObject(colnum) instanceof Integer && field
                .equalsIgnoreCase("commitType")) {
                beanMap.put(field, CommitType.getFromValue(rs.getInt(colnum)));
            } else {
                beanMap.put(field, rs.getObject(colnum));
            }
        }

        return (Commit) beanMap.getBean();
    }
}
