package org.openmbee.mms.rdb.repositories;

import org.openmbee.mms.core.dao.GroupDAO;
import org.openmbee.mms.data.domains.global.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GroupDAOImpl implements GroupDAO {

    private GroupRepository groupRepository;

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public Optional<Group> findByGroupId(String id) {
        return groupRepository.findByName(id);
    }

    @Override
    public Optional<Group> findByGroupName(String name) {
        return groupRepository.findByName(name);
    }

    @Override
    public Group save(Group group) {
        return groupRepository.save(group);
    }

    @Override
    public void delete(Group group) {
        groupRepository.delete(group);
    }

    @Override
    public List<Group> findAll() {
        return groupRepository.findAll();
    }
}
