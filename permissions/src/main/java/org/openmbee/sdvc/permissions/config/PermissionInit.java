package org.openmbee.sdvc.permissions.config;

import org.openmbee.sdvc.core.config.Roles;
import org.openmbee.sdvc.data.domains.global.Privilege;
import org.openmbee.sdvc.core.config.Privileges;
import org.openmbee.sdvc.data.domains.global.Role;
import org.openmbee.sdvc.rdb.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.*;

@Component
@Transactional
public class PermissionInit implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private PrivilegeRepository privRepo;
    @Autowired
    private RoleRepository roleRepo;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        Map<String, List> RPmap = new LinkedHashMap<>();
        List<String> aPriv = new ArrayList<String>();
        List<String> rPriv = Arrays.asList(Privileges.values()[0].toString(), Privileges.values()[3].toString(), Privileges.values()[6].toString(), Privileges.values()[8].toString(), Privileges.values()[12].toString(), Privileges.values()[14].toString(), Privileges.values()[18].toString());
        List<String> wPriv = Arrays.asList(Privileges.values()[0].toString(), Privileges.values()[1].toString(), Privileges.values()[3].toString(), Privileges.values()[4].toString(), Privileges.values()[6].toString(), Privileges.values()[7].toString(), Privileges.values()[8].toString(), Privileges.values()[9].toString(), Privileges.values()[12].toString(), Privileges.values()[13].toString(), Privileges.values()[14].toString(), Privileges.values()[15].toString(), Privileges.values()[18].toString());

        for (int i = 0; i < Privileges.values().length; i++) {
            aPriv.add(Privileges.values()[i].toString());
        }

        RPmap.put(Roles.ADMIN.toString(), aPriv);
        RPmap.put(Roles.WRITER.toString(), wPriv);
        RPmap.put(Roles.READER.toString(), rPriv);

        for (Role role : roleRepo.findAll()) {
            Set<Privilege> pSet = new HashSet<Privilege>();
            for (Privilege priv : privRepo.findAll()) {
                if (((RPmap.get(role.getName())).contains(priv.getName())) && (!(role.getPrivileges().contains(priv)))) {
                    pSet.add(priv);
                    role.setPrivileges(pSet);
                    roleRepo.save(role);
                }
            }
        }
    }
}