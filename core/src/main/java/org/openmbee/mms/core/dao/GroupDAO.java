package org.openmbee.mms.core.dao;

import org.openmbee.mms.data.domains.global.Group;

import java.util.List;
import java.util.Optional;

public interface GroupDAO {

    Optional<Group> findByGroupId(String id);

    Optional<Group> findByGroupName(String name);

    Group save(Group group);

    void delete(Group group);

    List<Group> findAll();
}
