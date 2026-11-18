package org.openmbee.mms.data.domains.global;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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
}
