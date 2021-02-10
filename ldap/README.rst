.. _ldap:

LDAP
----

Adds ldap authentication provider, ldap groups the user belongs to that are also assigned a role in mms will be added as the user's granted authorities.

Configuration
^^^^^^^^^^^^^

  ldap.enabled
    Boolean value to enable the ldap module. Required.

  ldap.provider.base
    The base string to use. Required.

  ldap.provider.url
    The provider url, including the base. Required.

  ldap.provider.userdn
    The userdn to use to authenticate to the provider. Optional.

  ldap.provider.password
    The password to use to authenticate to the provider. Optional.

  ldap.user.dn.pattern
    The dn pattern for the user. Required.

  ldap.user.attributes.username
    The attribute to use for the username. Optional.

    | `Default: uid`

  ldap.user.attributes.email
    The attribute to use for the email address. Optional.

    | `Default: mail`

  ldap.group.role.attribute
    The attribute to use for the group role. Optional.

  ldap.group.search.base
    The base for group search. Optional.

  ldap.group.search.filter
    The search filter for group search. Optional.
