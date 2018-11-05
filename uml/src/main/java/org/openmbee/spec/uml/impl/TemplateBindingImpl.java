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
import org.openmbee.spec.uml.TemplateBinding;
import org.openmbee.spec.uml.TemplateParameterSubstitution;
import org.openmbee.spec.uml.TemplateSignature;
import org.openmbee.spec.uml.TemplateableElement;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "TemplateBinding")
@Table(appliesTo = "TemplateBinding", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "TemplateBinding")
@JsonTypeName(value = "TemplateBinding")
public class TemplateBindingImpl extends MofObjectImpl implements TemplateBinding {

    private Collection<TemplateParameterSubstitution> parameterSubstitution;
    private Element owner;
    private Collection<Element> source;
    private Collection<Element> target;
    private Collection<Element> relatedElement;
    private Collection<Element> ownedElement;
    private TemplateSignature signature;
    private TemplateableElement boundElement;
    private Collection<Comment> ownedComment;

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "TemplateParameterSubstitutionMetaDef", metaColumn = @Column(name = "parameterSubstitutionType"), fetch = FetchType.LAZY)
    @JoinTable(name = "TemplateBinding_parameterSubstitution",
        joinColumns = @JoinColumn(name = "TemplateBindingId"),
        inverseJoinColumns = @JoinColumn(name = "parameterSubstitutionId"))
    public Collection<TemplateParameterSubstitution> getParameterSubstitution() {
        if (parameterSubstitution == null) {
            parameterSubstitution = new ArrayList<>();
        }
        return parameterSubstitution;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = TemplateParameterSubstitutionImpl.class)
    public void setParameterSubstitution(
        Collection<TemplateParameterSubstitution> parameterSubstitution) {
        this.parameterSubstitution = parameterSubstitution;
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
    public Collection<Element> getSource() {
        if (source == null) {
            source = new ArrayList<>();
        }
        return source;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ElementImpl.class)
    public void setSource(Collection<Element> source) {
        this.source = source;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<Element> getTarget() {
        if (target == null) {
            target = new ArrayList<>();
        }
        return target;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ElementImpl.class)
    public void setTarget(Collection<Element> target) {
        this.target = target;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<Element> getRelatedElement() {
        if (relatedElement == null) {
            relatedElement = new ArrayList<>();
        }
        return relatedElement;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ElementImpl.class)
    public void setRelatedElement(Collection<Element> relatedElement) {
        this.relatedElement = relatedElement;
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
    @Any(metaDef = "TemplateSignatureMetaDef", metaColumn = @Column(name = "signatureType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "signatureId", table = "TemplateBinding")
    public TemplateSignature getSignature() {
        return signature;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = TemplateSignatureImpl.class)
    public void setSignature(TemplateSignature signature) {
        this.signature = signature;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "TemplateableElementMetaDef", metaColumn = @Column(name = "boundElementType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "boundElementId", table = "TemplateBinding")
    public TemplateableElement getBoundElement() {
        return boundElement;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = TemplateableElementImpl.class)
    public void setBoundElement(TemplateableElement boundElement) {
        this.boundElement = boundElement;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "TemplateBinding_ownedComment",
        joinColumns = @JoinColumn(name = "TemplateBindingId"),
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
