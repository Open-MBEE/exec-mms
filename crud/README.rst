.. _crud:

CRUD
----

This provides the main json document/object versioning functionality and endpoints for cruding orgs, projects, branches, elements and commits.

Service Factory
^^^^^^^^^^^^^^^

``ServiceFactory`` under ``services`` is used to get the correct service bean for a particular project type/schema. When creating or getting a project or element, the schema of the project is prefixed to "ProjectService" or "NodeService" to construct the name of the bean. A default implementation is provided.

``defaultNodeService`` - allows partial update of an element by merging it with existing version, allows overwrite of existing version by supplied document, also has overridable hooks for modifying data on create/update/get to allow extending of this class instead of another implementation from scratch

``defaultProjectService`` - auto creates ``master`` branch on project creation

see :ref:`cameo` module for ``cameoNodeService`` and ``cameoProjectService`` for an extension example for a different schema
