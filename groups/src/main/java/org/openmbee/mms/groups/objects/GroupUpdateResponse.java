package org.openmbee.mms.groups.objects;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class GroupUpdateResponse {

    @Schema(required = true)
    private String group;
    @Schema(nullable = true)
    private List<String> added;
    @Schema(nullable = true)
    private List<String> removed;
    @Schema(nullable = true)
    private List<String> rejected;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<String> getAdded() {
        return added;
    }

    public void setAdded(List<String> added) {
        this.added = added;
    }

    public List<String> getRemoved() {
        return removed;
    }

    public void setRemoved(List<String> removed) {
        this.removed = removed;
    }

    public List<String> getRejected() {
        return rejected;
    }

    public void setRejected(List<String> rejected) {
        this.rejected = rejected;
    }
}
