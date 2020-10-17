## Webhooks

Adds endpoints for registering webhooks per project

Current triggers are on commit or branch creation

Commit event:

        {
            "projectId": "projectId",
            "branchId": "branchId",
            "event": "commit",
            "payload": {{see CommitJson}}
        }
        
Branch event:

        {
            "projectId": "projectId",
            "branchId": "branchId",
            "event": "branch_created",
            "payload": {{see RefJson}}
        }