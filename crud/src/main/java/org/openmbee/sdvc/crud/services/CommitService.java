package org.openmbee.sdvc.crud.services;

import org.openmbee.sdvc.crud.repositories.commit.CommitDAO;
import org.openmbee.sdvc.crud.repositories.commit.CommitElasticDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommitService {

    private CommitDAO commitRepository;
    private CommitElasticDAO commitElasticRepository;

    @Autowired
    public void setCommitDao(CommitDAO commitDao) {
        this.commitRepository = commitDao;
    }

    @Autowired
    public void setCommitElasticDao(CommitElasticDAO commitElasticRepository) {
        this.commitElasticRepository = commitElasticRepository;
    }

}
