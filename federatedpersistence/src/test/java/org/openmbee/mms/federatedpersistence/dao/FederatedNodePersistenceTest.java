package org.openmbee.mms.federatedpersistence.dao;

import org.junit.Before;
import org.junit.Test;
import org.openmbee.mms.core.dao.CommitPersistence;
import org.openmbee.mms.core.exceptions.InternalErrorException;
import org.openmbee.mms.data.dao.BranchDAO;
import org.openmbee.mms.data.dao.CommitDAO;
import org.openmbee.mms.data.dao.NodeDAO;
import org.openmbee.mms.data.dao.NodeIndexDAO;
import org.openmbee.mms.data.domains.scoped.Node;
import org.openmbee.mms.json.CommitJson;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class FederatedNodePersistenceTest {

    private PlatformTransactionManager transactionManager;
    private TransactionStatus status;
    private NodeDAO nodeDAO;
    private FederatedNodePersistence persistence;
    private FederatedNodeChangeInfoImpl info;

    @Before
    public void setUp() {
        transactionManager = mock(PlatformTransactionManager.class);
        status = mock(TransactionStatus.class);
        nodeDAO = mock(NodeDAO.class);
        when(nodeDAO.getTransactionManager()).thenReturn(transactionManager);
        when(transactionManager.getTransaction(any())).thenReturn(status);

        persistence = new FederatedNodePersistence(nodeDAO, mock(NodeIndexDAO.class), mock(CommitDAO.class),
            mock(BranchDAO.class), null, null, mock(CommitPersistence.class));

        CommitJson commitJson = new CommitJson();
        commitJson.setProjectId("PROJECT_ID");
        commitJson.setRefId("REF_ID");
        info = new FederatedNodeChangeInfoImpl();
        info.setToSaveNodeMap(new java.util.HashMap<>());
        info.setOldDocIds(new java.util.HashSet<>());
        info.setCommitJson(commitJson);
    }

    @Test
    public void commitWithNothingToSaveOpensNoTransaction() {
        persistence.commitChanges(info);

        verifyNoInteractions(transactionManager);
    }

    @Test
    public void commitWithNodesCommitsTheTransaction() {
        info.getToSaveNodeMap().put("a", new Node());

        persistence.commitChanges(info);

        verify(transactionManager).commit(status);
        verify(transactionManager, never()).rollback(any());
    }

    @Test
    public void failedCommitRollsBackTheTransaction() {
        info.getToSaveNodeMap().put("a", new Node());
        doThrow(new RuntimeException("save failed")).when(nodeDAO).saveAll(any());

        assertThrows(InternalErrorException.class, () -> persistence.commitChanges(info));

        verify(transactionManager).rollback(status);
        verify(transactionManager, never()).commit(any());
    }
}
