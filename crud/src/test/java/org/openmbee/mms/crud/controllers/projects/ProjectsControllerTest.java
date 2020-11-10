package org.openmbee.mms.crud.controllers.projects;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProjectsControllerTest {

    private void checkFail(String id){
        assertFalse(ProjectsController.isProjectIdValid(id));
    }

    private void checkGood(String id){
        assertTrue(ProjectsController.isProjectIdValid(id));
    }

    @Test
    public void isProjectIdValid() {
        checkGood("project-1234");
        checkGood("_A-project");
        checkGood("000000000000000000000");
        checkGood("project-with64characters0000000000000000000000000000000000000000");
        checkFail("project with spaces");
        checkFail("specialcharacters!");
    }
}