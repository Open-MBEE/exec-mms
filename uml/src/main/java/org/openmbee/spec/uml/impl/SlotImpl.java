package org.openmbee.spec.uml.impl;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.SecondaryTable;
import javax.persistence.Transient;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.Table;
import org.openmbee.spec.uml.Comment;
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.InstanceSpecification;
import org.openmbee.spec.uml.Slot;
import org.openmbee.spec.uml.StructuralFeature;
import org.openmbee.spec.uml.ValueSpecification;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "Slot")
@Table(appliesTo = "Slot", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "Slot")
@JsonTypeName(value = "Slot")
public class SlotImpl extends MofObjectImpl implements Slot {

    private Element owner;
    private Collection<Element> ownedElement;
    private InstanceSpecification owningInstance;
    private List<ValueSpecification> value;
    private Collection<Comment> ownedComment;
    private StructuralFeature definingFeature;

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Transient
    public Element getOwner() {
        return owner;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ElementImpl.class)
    public void setOwner(Element owner) {
        this.owner = owner;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<Element> getOwnedElement() {
        if (ownedElement == null) {
            ownedElement = new ArrayList<>();
        }
        return ownedElement;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ElementImpl.class)
    public void setOwnedElement(Collection<Element> ownedElement) {
        this.ownedElement = ownedElement;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "InstanceSpecificationMetaDef", metaColumn = @Column(name = "owningInstanceType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "owningInstanceId", table = "Slot")
    public InstanceSpecification getOwningInstance() {
        return owningInstance;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = InstanceSpecificationImpl.class)
    public void setOwningInstance(InstanceSpecification owningInstance) {
        this.owningInstance = owningInstance;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ValueSpecificationMetaDef", metaColumn = @Column(name = "valueType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Slot_value",
        joinColumns = @JoinColumn(name = "SlotId"),
        inverseJoinColumns = @JoinColumn(name = "valueId"))
    public List<ValueSpecification> getValue() {
        if (value == null) {
            value = new ArrayList<>();
        }
        return value;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ValueSpecificationImpl.class)
    public void setValue(List<ValueSpecification> value) {
        this.value = value;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Slot_ownedComment",
        joinColumns = @JoinColumn(name = "SlotId"),
        inverseJoinColumns = @JoinColumn(name = "ownedCommentId"))
    public Collection<Comment> getOwnedComment() {
        if (ownedComment == null) {
            ownedComment = new ArrayList<>();
        }
        return ownedComment;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = CommentImpl.class)
    public void setOwnedComment(Collection<Comment> ownedComment) {
        this.ownedComment = ownedComment;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "StructuralFeatureMetaDef", metaColumn = @Column(name = "definingFeatureType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "definingFeatureId", table = "Slot")
    public StructuralFeature getDefiningFeature() {
        return definingFeature;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = StructuralFeatureImpl.class)
    public void setDefiningFeature(StructuralFeature definingFeature) {
        this.definingFeature = definingFeature;
    }

}
