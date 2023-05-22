package org.openmbee.mms.users.security;

import org.openmbee.mms.json.UserJson;

public interface UserDetails extends org.springframework.security.core.userdetails.UserDetails {

    UserJson getUser();
}
