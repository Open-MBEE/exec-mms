package org.openmbee.mms.data.domains.global;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.lang.reflect.Field;
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
@JsonIgnoreProperties(value = {"created", "modified", "id"}, allowGetters = true)
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        try {
            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields) {
                Object current = field.get(this);
                Object value = field.get(o);
                switch(field.getType().toString()) {
                    case "String" :
                    case "DecimalFormat" :
                    case "BigDecimal":
                        if ((current != null && !current.equals(value)) || (current == null && value != null)) {
                            return false;
                        }
                        break;

                    default:
                        if (current != value) {
                            return false;
                        }
                }
            }
        } catch (IllegalAccessException iae) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int seed = 42;
        int hash = 0;
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                Object value = field.get(this);
                hash = hash + (seed * (value != null ? value.hashCode() : 0));
            } catch (IllegalAccessException iae) {
                //Intentionally muted
            }
        }
        return hash;
    }
}
