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
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.ParameterableElement;
import org.openmbee.spec.uml.TemplateBinding;
import org.openmbee.spec.uml.TemplateParameter;
import org.openmbee.spec.uml.TemplateParameterSubstitution;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "TemplateParameterSubstitution")
@Table(appliesTo = "TemplateParameterSubstitution", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "TemplateParameterSubstitution")
@JsonTypeName(value = "TemplateParameterSubstitution")
public class TemplateParameterSubstitutionImpl extends MofObjectImpl implements
    TemplateParameterSubstitution {

    private ParameterableElement actual;
    private Element owner;
    private Collection<Element> ownedElement;
    private ParameterableElement ownedActual;
    private TemplateBinding templateBinding;
    private TemplateParameter formal;
    private Collection<Comment> ownedComment;

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ParameterableElementMetaDef", metaColumn = @Column(name = "actualType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "actualId", table = "TemplateParameterSubstitution")
    public ParameterableElement getActual() {
        return actual;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ParameterableElementImpl.class)
    public void setActual(ParameterableElement actual) {
        this.actual = actual;
    }

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
    @Any(metaDef = "ParameterableElementMetaDef", metaColumn = @Column(name = "ownedActualType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "ownedActualId", table = "TemplateParameterSubstitution")
    public ParameterableElement getOwnedActual() {
        return ownedActual;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ParameterableElementImpl.class)
    public void setOwnedActual(ParameterableElement ownedActual) {
        this.ownedActual = ownedActual;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "TemplateBindingMetaDef", metaColumn = @Column(name = "templateBindingType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "templateBindingId", table = "TemplateParameterSubstitution")
    public TemplateBinding getTemplateBinding() {
        return templateBinding;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = TemplateBindingImpl.class)
    public void setTemplateBinding(TemplateBinding templateBinding) {
        this.templateBinding = templateBinding;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "TemplateParameterMetaDef", metaColumn = @Column(name = "formalType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "formalId", table = "TemplateParameterSubstitution")
    public TemplateParameter getFormal() {
        return formal;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = TemplateParameterImpl.class)
    public void setFormal(TemplateParameter formal) {
        this.formal = formal;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "TemplateParameterSubstitution_ownedComment",
        joinColumns = @JoinColumn(name = "TemplateParameterSubstitutionId"),
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
