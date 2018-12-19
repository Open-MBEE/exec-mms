package org.openmbee.sdvc.crud.repositories;

import java.io.IOException;

public interface ProjectIndex {

    void create(String index) throws IOException;

    void delete(String index)  throws IOException;

}
