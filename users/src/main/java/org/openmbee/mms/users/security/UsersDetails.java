package org.openmbee.mms.users.security;

import org.openmbee.mms.data.domains.global.User;

public interface UsersDetails extends org.springframework.security.core.userdetails.UserDetails {

    User getUser();
}
