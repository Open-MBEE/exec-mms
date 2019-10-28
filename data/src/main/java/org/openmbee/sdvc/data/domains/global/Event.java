package org.openmbee.sdvc.data.domains.global;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "events",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"uri","project_id"})
    })
public class Event extends Base {

    @ManyToOne
    private Project project;

    private String uri;

    public Event() {}

    public Event(Project p, String uri) {
        this.project = p;
        this.uri = uri;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || id == null || getClass() != o.getClass()) {
            return false;
        }

        Event node = (Event) o;

        return id.equals(node.id);
    }
}
