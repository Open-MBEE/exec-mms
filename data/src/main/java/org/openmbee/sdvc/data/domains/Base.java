package org.openmbee.sdvc.data.domains;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@MappedSuperclass
@JsonIgnoreProperties(value = {"created", "modified"}, allowGetters = true)
public abstract class Base implements Serializable {

    private static final long serialVersionUID = 8389104517465359723L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //use of AUTO with mysql results in TABLE, for postgres auto is fine
    @Column(name = "id")
    Long id;

    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @UpdateTimestamp
    @Column(name = "modified", nullable = false)
    @LastModifiedDate
    private Instant modified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getModified() {
        return modified;
    }

    public void setModified(Instant modified) {
        this.modified = modified;
    }

    @Override
    public int hashCode() {
        return (id == null) ? -1 : id.hashCode();
    }
}
