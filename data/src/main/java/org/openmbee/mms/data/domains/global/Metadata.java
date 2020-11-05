package org.openmbee.mms.data.domains.global;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "metadata")
public class Metadata extends Base {

    @ManyToOne
    private Project project;

    @Column(name="\"key\"") //key is reserved word in mysql
    private String key;
    private String value;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
