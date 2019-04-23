package org.openmbee.sdvc.crud.repositories;

import java.util.Map;
import javax.sql.DataSource;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseDAOImpl {

    private Map<String, DataSource> crudDataSources;

    @Autowired
    public void setCrudDataSources(
        @Qualifier("crudDataSources") Map<String, DataSource> crudDataSources) {
        this.crudDataSources = crudDataSources;
    }

    public JdbcTemplate getConnection() {
        return new JdbcTemplate(crudDataSources.get(DbContextHolder.getContext().getKey()));
    }

    public String getSuffix() {
        return DbContextHolder.getContext().getDbTableSuffix();
    }

}
