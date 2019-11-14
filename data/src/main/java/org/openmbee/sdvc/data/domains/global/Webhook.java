package org.openmbee.sdvc.data.domains.global;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "webhooks",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"url","project_id"})
    })
public class Webhook extends Base {

    @ManyToOne
    @JsonIgnore
    private Project project;

    private String url;

    public Webhook() {}

    public Webhook(Project p, String url) {
        this.project = p;
        this.url = url;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || id == null || getClass() != o.getClass()) {
            return false;
        }

        Webhook node = (Webhook) o;

        return id.equals(node.id);
    }
}
