package org.openmbee.mms.groups.services;

import org.openmbee.mms.data.domains.global.Group;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Pattern;

import static org.openmbee.mms.core.config.AuthorizationConstants.EVERYONE;
import static org.openmbee.mms.core.config.AuthorizationConstants.MMSADMIN;

@Service
public class GroupValidationService {

    private static final Set<String> RESTRICTED_NAMES =  Set.of(MMSADMIN, EVERYONE);
    private Pattern VALID_GROUP_NAME_PATTERN = Pattern.compile("^[\\w-]+");

    public boolean isRestrictedGroup(String groupName) {
        return RESTRICTED_NAMES.contains(groupName);
    }

    public boolean isValidGroupName(String groupName){
        return groupName != null &&
            !isRestrictedGroup(groupName) &&
            VALID_GROUP_NAME_PATTERN.matcher(groupName).matches();
    }

    public boolean canDeleteGroup(Group group){
        return !isRestrictedGroup(group.getName()) &&
            (group.getUsers() == null || group.getUsers().isEmpty());
    }
}
