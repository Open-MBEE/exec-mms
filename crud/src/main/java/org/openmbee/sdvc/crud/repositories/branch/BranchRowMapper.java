package org.openmbee.sdvc.crud.repositories.branch;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import org.openmbee.sdvc.crud.domains.Branch;
import org.openmbee.sdvc.crud.domains.Commit;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.jdbc.core.RowMapper;

public class BranchRowMapper implements RowMapper {

    public Branch mapRow(ResultSet rs, int rowNum) throws SQLException {
        HashMap<String, BeanMap> beansByName = new HashMap();

        beansByName.put("branches", BeanMap.create(new Branch()));
        beansByName.put("parentRef", BeanMap.create(new Branch()));
        beansByName.put("parentCommit", BeanMap.create(new Commit()));

        ResultSetMetaData resultSetMetaData = rs.getMetaData();

        for (int colnum = 1; colnum <= resultSetMetaData.getColumnCount(); colnum++) {

            String[] arr = resultSetMetaData.getColumnName(colnum).split("\\.");

            if (arr.length < 2) {
                continue;
            }

            String table = arr[0];
            String field = arr[1];

            BeanMap beanMap = beansByName.get(table);

            if (rs.getObject(colnum) != null) {
                beanMap.put(field, rs.getObject(colnum));
            }
        }

        Branch branch = (Branch) beansByName.get("branches").getBean();
        branch.setParentRef((Branch) beansByName.get("parentRef").getBean());
        branch.setParentCommit((Commit) beansByName.get("parentCommit").getBean());

        return branch;
    }
}
