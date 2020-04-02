package org.openmbee.sdvc.data.domains.global;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metadata metadata = (Metadata) o;
        return Objects.equals(getProject(), metadata.getProject()) &&
            Objects.equals(getKey(), metadata.getKey()) &&
            Objects.equals(getValue(), metadata.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProject(), getKey(), getValue());
    }
}
