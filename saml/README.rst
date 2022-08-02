.. _saml:

SAML
----

Adds saml authentication provider, saml groups the user belongs to that are also assigned a role in mms will be added as the user's granted authorities.

Configuration
^^^^^^^^^^^^^

  saml.enabled
    Boolean value to enable the ldap module. Required.

  saml.provider.base
    The base string to use. Required.

  saml.provider.url
    The provider url, including the base. Required.

  saml.provider.userdn
    The userdn to use to authenticate to the provider. Optional.

  saml.provider.password
    The password to use to authenticate to the provider. Optional.

  saml.user.dn.pattern
    The dn pattern for the user. Required.

  saml.user.attributes.username
    The attribute to use for the username. Optional.

    | `Default: uid`

  saml.user.attributes.email
    The attribute to use for the email address. Optional.

    | `Default: mail`

  saml.group.role.attribute
    The attribute to use for the group role. Optional.

  saml.group.search.base
    The base for group search. Optional.

  saml.group.search.filter
    The search filter for group search. Optional.
