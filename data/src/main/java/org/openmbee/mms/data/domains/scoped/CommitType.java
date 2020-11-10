package org.openmbee.mms.data.domains.scoped;

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

    public static CommitType getFromValue(int id) {
        for (CommitType commitType : CommitType.values()) {
            if (commitType.getId() == id) {
                return commitType;
            }
        }

        return null;
    }

    public int getId() {
        return id;
    }
}
