package org.openmbee.sdvc.crud.repositories.edge;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.openmbee.sdvc.crud.domains.Node;
import org.springframework.jdbc.core.RowMapper;

public class EdgeRowMapper implements RowMapper {

    public Node mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Node(
            rs.getLong("id"),
            rs.getString("sysmlid"),
            rs.getString("elasticid"),
            rs.getString("lastcommit"),
            rs.getString("initialcommit"),
            rs.getBoolean("deleted"),
            rs.getInt("nodetype")
        );
    }
}
