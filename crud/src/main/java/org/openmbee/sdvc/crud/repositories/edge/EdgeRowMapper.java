package org.openmbee.sdvc.crud.repositories.edge;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.openmbee.sdvc.crud.domains.Edge;
import org.openmbee.sdvc.crud.domains.Node;
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
