.. _permissions:

Permissions
-----------------

This module implements local permissions and adds endpoints for modifying and getting permissions

Default Access Levels
^^^^^^^^^^^^^^^^^^^^^

The MMS includes 3 hierarchical levels of permissions in the default implementation. Permissions are inherited from the parent level.

  Organization level
    Permissions on this level are inherited by the Project and Branch level by default.

  Project level
    Permissions on this level are inherited by the Branch level by default.

  Branch level
    Permissions on this level are not inherited.

From these levels, 3 levels of access permissions are provided by default:

  Admin
    Can read and write all elements. Can also access admin level operations.

  Write
    Can read and write all elements on this level.

  Read
    Can read all elements on this level.

Setting Permissions
^^^^^^^^^^^^^^^^^^^

The Permissions module provides several REST endpoints for managing permissions.

  /orgs/{orgId}/permissions
    Update permissions for organizations.

  /projects/{projectId}/permissions
    Update permissions for projects.

  /projects/{projectId}/refs/{refId}/permissions
    Update permissions for branches.

For each of these endpoints, a payload must be sent with acceptable values

Permissions acceptable values

.. code-block:: JSON

  {
    "users / groups": {
      "action": "MODIFY / REPLACE / REMOVE",
      "permissions": [
        {
          "name": "USERNAME",
          "role": "ADMIN / WRITER / READER"
        }
      ]
    },
    "inherit": true,
    "public": true
  }

More Information
^^^^^^^^^^^^^^^^

For more information, see the OpenAPI Documentation.