package org.openmbee.mms.federatedpersistence.dao;

import org.junit.Before;
import org.junit.Test;
import org.openmbee.mms.core.exceptions.InternalErrorException;
import org.openmbee.mms.data.dao.OrgDAO;
import org.openmbee.mms.data.dao.ProjectDAO;
import org.openmbee.mms.data.dao.ProjectIndex;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FederatedProjectPersistenceTest {

    private ProjectDAO projectDao;
    private ProjectIndex projectIndex;
    private FederatedProjectPersistence persistence;

    @Before
    public void setUp() {
        projectDao = mock(ProjectDAO.class);
        projectIndex = mock(ProjectIndex.class);
        persistence = new FederatedProjectPersistence(projectDao, projectIndex, mock(OrgDAO.class));
    }

    @Test
    public void hardDeleteRemovesTheProjectFromBothStores() {
        persistence.hardDelete("p1");

        verify(projectDao).delete("p1");
        verify(projectIndex).delete("p1");
    }

    @Test
    public void hardDeleteStillClearsTheIndexWhenTheDatabaseDeleteFails() {
        doThrow(new InternalErrorException("no database")).when(projectDao).delete("p1");
        doThrow(new InternalErrorException("no index")).when(projectIndex).delete("p1");

        persistence.hardDelete("p1");

        verify(projectDao).delete("p1");
        verify(projectIndex).delete("p1");
    }
}
