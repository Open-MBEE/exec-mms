.. _webhooks:

Webhooks
--------

Adds endpoints for registering webhooks per project

Current triggers are on commit or branch creation

Commit event
^^^^^^^^^^^^

.. code-block:: JSON

       {
           "projectId": "projectId",
           "branchId": "branchId",
           "event": "commit",
           "payload": {{see CommitJson}}
       }


Branch event
^^^^^^^^^^^^

.. code-block:: JSON

       {
           "projectId": "projectId",
           "branchId": "branchId",
           "event": "branch_created",
           "payload": {{see RefJson}}
       }
