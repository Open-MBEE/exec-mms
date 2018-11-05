package org.openmbee.sdvc.crud.domains;

import javax.persistence.Table;

@Table(name = "committypes")
public enum CommitType {
    COMMIT(1),
    BRANCH(2),
    MERGE(3);

    private int id;

    CommitType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
