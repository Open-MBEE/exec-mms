package org.openmbee.sdvc.rdb.repositories.edge;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.openmbee.sdvc.data.domains.Edge;
import org.springframework.jdbc.core.RowMapper;

public class EdgeRowMapper implements RowMapper {

    public Edge mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Edge(
            rs.getLong("id"),
            rs.getLong("parent"),
            rs.getLong("child"),
            rs.getInt("edgeType")
        );
    }
}
