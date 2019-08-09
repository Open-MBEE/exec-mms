package org.openmbee.sdvc.example;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmbee.sdvc.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleApplication.class)
public class ExampleApplicationTests {

    @Autowired
    private UserService userService;

    @Before
    public void before() {
    }

    @Test
    public void testSave() {
        System.out.println("Save Test");
    }
}
