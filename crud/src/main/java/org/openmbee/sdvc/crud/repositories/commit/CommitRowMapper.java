package org.openmbee.sdvc.crud.repositories.commit;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Set;
import org.openmbee.sdvc.crud.domains.Commit;
import org.openmbee.sdvc.crud.domains.CommitType;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.jdbc.core.RowMapper;

public class CommitRowMapper implements RowMapper<Commit> {

    static final String COMMITS = "commits";

    public Commit mapRow(ResultSet rs, int rowNum) throws SQLException {
        HashMap<String, BeanMap> beansByName = new HashMap<>();

        beansByName.put(COMMITS, BeanMap.create(new Commit()));

        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        Set commitKeys = beansByName.get(COMMITS).keySet();

        for (int colnum = 1; colnum <= resultSetMetaData.getColumnCount(); colnum++) {

            String[] arr = resultSetMetaData.getColumnName(colnum).split("\\.");

            String table = COMMITS;
            String field = null;

            if (arr.length < 2) {
                field = getFieldName(arr[0], commitKeys);
            } else {
                table = arr[0];
                field = getFieldName(arr[1], commitKeys);
            }

            if (field == null) {
                continue;
            }

            BeanMap beanMap = beansByName.get(table);

            if (rs.getObject(colnum) instanceof Timestamp) {
                beanMap.put(field, rs.getTimestamp(colnum).toInstant());
            } else if (rs.getObject(colnum) instanceof Integer && field
                .equalsIgnoreCase("commitType")) {
                beanMap.put(field, CommitType.getFromValue(rs.getInt(colnum)));
            } else {
                beanMap.put(field, rs.getObject(colnum));
            }
        }

        return (Commit) beansByName.get(COMMITS).getBean();
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
