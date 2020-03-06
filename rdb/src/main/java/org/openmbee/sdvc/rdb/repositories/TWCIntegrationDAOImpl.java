package org.openmbee.sdvc.rdb.repositories;

import org.openmbee.sdvc.core.dao.TWCIntegrationDAO;
import org.openmbee.sdvc.data.domains.global.TWCIntegration;
import org.openmbee.sdvc.rdb.config.DatabaseDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TWCIntegrationDAOImpl implements TWCIntegrationDAO {
    protected TWCIntegrationRepository twcIntegrationRepository;
    protected DatabaseDefinitionService twcIntegrationOperations;

    @Autowired
    public void setProjectRepository(TWCIntegrationRepository twcIntegrationRepository) {
        this.twcIntegrationRepository = twcIntegrationRepository;
    }

    @Autowired
    public void setDatabaseDefinitionService(DatabaseDefinitionService twcIntegrationOperations) {
        this.twcIntegrationOperations = twcIntegrationOperations;
    }

    @Override
    public Optional<TWCIntegration> findTWCIntegrationByProjectId(String id) {
        return twcIntegrationRepository.findTWCIntegrationByProjectId(id);
    }

    @Override
    public Optional<TWCIntegration> findTWCIntegrationByProjectName(String name) {
        return twcIntegrationRepository.findTWCIntegrationByProjectName(name);
    }

    @Override
    public TWCIntegration save(TWCIntegration integration) {
        // if this save is just like projectDAO's version, do we need to have the Project object inside TWCIntegration?
//        if (integration.getProjectId() == null) {
//            try {
////                twcIntegrationOperations.createProjectDatabase(integration); ??
//            } catch (SQLException ex) {
//                //TODO db already exists, attempt to delete db?
//                throw new InternalErrorException(ex);
//            }
//        }
        return twcIntegrationRepository.save(integration);
    }

    @Override
    public void delete(TWCIntegration integration) {
        twcIntegrationRepository.delete(integration);
    }

    @Override
    public List<TWCIntegration> findAll() {
        return twcIntegrationRepository.findAll();
    }
}
