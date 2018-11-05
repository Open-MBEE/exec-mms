package org.openmbee.spec.uml.impl;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.Collection;
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
import org.openmbee.spec.uml.ConnectableElement;
import org.openmbee.spec.uml.ConnectorEnd;
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.Property;
import org.openmbee.spec.uml.ValueSpecification;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "ConnectorEnd")
@Table(appliesTo = "ConnectorEnd", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "ConnectorEnd")
@JsonTypeName(value = "ConnectorEnd")
public class ConnectorEndImpl extends MofObjectImpl implements ConnectorEnd {

    private Element owner;
    private Property partWithPort;
    private Collection<Element> ownedElement;
    private Integer lower;
    private ValueSpecification lowerValue;
    private Boolean isUnique;
    private Boolean isOrdered;
    private Property definingEnd;
    private Integer upper;
    private ConnectableElement role;
    private ValueSpecification upperValue;
    private Collection<Comment> ownedComment;

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
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "PropertyMetaDef", metaColumn = @Column(name = "partWithPortType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "partWithPortId", table = "ConnectorEnd")
    public Property getPartWithPort() {
        return partWithPort;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = PropertyImpl.class)
    public void setPartWithPort(Property partWithPort) {
        this.partWithPort = partWithPort;
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
    @Transient
    public Integer getLower() {
        return lower;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setLower(Integer lower) {
        this.lower = lower;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ValueSpecificationMetaDef", metaColumn = @Column(name = "lowerValueType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "lowerValueId", table = "ConnectorEnd")
    public ValueSpecification getLowerValue() {
        return lowerValue;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ValueSpecificationImpl.class)
    public void setLowerValue(ValueSpecification lowerValue) {
        this.lowerValue = lowerValue;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isUnique", table = "ConnectorEnd")
    public Boolean isUnique() {
        return isUnique;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setUnique(Boolean isUnique) {
        this.isUnique = isUnique;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isOrdered", table = "ConnectorEnd")
    public Boolean isOrdered() {
        return isOrdered;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setOrdered(Boolean isOrdered) {
        this.isOrdered = isOrdered;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Transient
    public Property getDefiningEnd() {
        return definingEnd;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = PropertyImpl.class)
    public void setDefiningEnd(Property definingEnd) {
        this.definingEnd = definingEnd;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Transient
    public Integer getUpper() {
        return upper;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setUpper(Integer upper) {
        this.upper = upper;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ConnectableElementMetaDef", metaColumn = @Column(name = "roleType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "roleId", table = "ConnectorEnd")
    public ConnectableElement getRole() {
        return role;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ConnectableElementImpl.class)
    public void setRole(ConnectableElement role) {
        this.role = role;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ValueSpecificationMetaDef", metaColumn = @Column(name = "upperValueType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "upperValueId", table = "ConnectorEnd")
    public ValueSpecification getUpperValue() {
        return upperValue;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ValueSpecificationImpl.class)
    public void setUpperValue(ValueSpecification upperValue) {
        this.upperValue = upperValue;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "ConnectorEnd_ownedComment",
        joinColumns = @JoinColumn(name = "ConnectorEndId"),
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

}
