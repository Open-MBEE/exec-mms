.. _core:

Core
====

This contains core configurations, constants, interfaces, and object classes used by all other modules.

Core data organization concept
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Org (like github org) -> Project (like git repo) -> Commits and Branches

The objects being versioned are json objects (with optional binary attachments, see ``artifacts`` module)

DAO interfaces (\ ``dao``\ )
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Interfaces for metadata and json storage

Events (\ ``services``\ , ``pubsub``\ )
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

A default ``EventService`` that can publish ``EventObject`` (extension of Spring ApplicationEvent that includes event type, projectId, branchId and payload)

Other modules can use the publisher to publish and listen to events (ex. ``crud`` publishes events on commit, branch created, etc and ``webhooks`` listens for those events)

Service interfaces (\ ``services``\ )
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Major ones are ProjectService, BranchService, NodeService

Project and Node services can have different implementations based on the project schema in order to provide different behaviors if needed.

A default schema implementation is registered by the ``crud`` module, new schemas and service implementations provided by modules need to be registered by injecting and adding to the ``ProjectSchemas`` under ``config``

see ``crud``\ , ``cameo`` for examples

Security and Permissions (\ ``security``\ , ``config``\ , ``delegation``\ , ``services``\ )
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Locally, MMS uses role based permissions on the org, project, and branch level, for user and groups. There can be different permission implementations that are delegated to for looking up whether a user has certain privileges to do certain things (ex. can user read from project A/branch b)

Current roles are ADMIN, READER, WRITER, each grants a set of Privileges like PROJECT_READ, PROJECT_EDIT, etc

Roles can be assigned to groups per org/project/branch, a user's groups are inferred from Spring Security's ``Authentication``\ 's ``getAuthorities`` method (it's hijacked a bit to fit what we need), then the user, groups, target object and privilege requested are given to a permission delegate implementation to determine authorization. The permission delegate implementation can do whatever it wants with the input to return true/false.

A special group/authority that denotes admin status is ``mmsadmin``\ , an authentication provider can add this to the user's authorities to denote admin status and mms will allow operation on all endpoints.

See ``MethodSecurityService`` (used by controllers gating access to endpoints) and ``PermissionService``

see ``permission`` or ``twc`` for more examples

ContextHolder (\ ``config``\ )
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Instead of passing projectId, branchId everywhere, ``ContextHolder`` can be used to get/set the context for the thread.

Exceptions (\ ``exceptions``\ )
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Runtime Exceptions that'll return the corresponding http response code
