.. _rdb:

RDB
===

The RDB Module implements the JPA Repositories defined in the CRUD Module.

Configuration
*************

The following are a list of options to configure the RDB Module for MMS.

  spring.datasource.url
    The datasource url in jdbc format. Required.

  spring.datasource.database
    The database name to use for global MMS configuration. Required.

  spring.datasource.username
    The username to use for authentication. Optional.

  spring.datasource.password
    The password to use for authentication. Optional.

  spring.datasource.driver-class-name
    The driver to use for JDBC. Any database driver supported by Spring Data can be used. Required.

  spring.datasource.initialization-mode
    The initialization mode to use when starting the MMS application. Accepted values are `always`, `embedded`, and `never`. Required.

  spring.jpa.properties.hibernate.dialect
    The hibernate dialect to use. Required.

  spring.jpa.properties.hibernate.dialect.storage_engine
    The storage engine to use. Optional.

  spring.jpa.hibernate.ddl-auto
    The DDL generation option. Accepted values are `none`, `create`, `create-drop`, `validate`, and `update` Required.

Required Properties
*******************

These properties are required for the MMS application to provide all available features. They are set by default, but are listed here for completeness.

  ::

  `spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true`
  `spring.jpa.open-in-view=false`
  `spring.main.allow-bean-definition-overriding=true`