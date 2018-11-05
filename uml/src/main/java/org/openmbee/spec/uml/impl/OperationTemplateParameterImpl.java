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
import org.openmbee.spec.uml.Operation;
import org.openmbee.spec.uml.OperationTemplateParameter;
import org.openmbee.spec.uml.ParameterableElement;
import org.openmbee.spec.uml.TemplateSignature;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "OperationTemplateParameter")
@Table(appliesTo = "OperationTemplateParameter", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "OperationTemplateParameter")
@JsonTypeName(value = "OperationTemplateParameter")
public class OperationTemplateParameterImpl extends MofObjectImpl implements
    OperationTemplateParameter {

    private Element owner;
    private Operation parameteredElement;
    private Collection<Element> ownedElement;
    private ParameterableElement default_;
    private ParameterableElement ownedDefault;
    private ParameterableElement ownedParameteredElement;
    private Collection<Comment> ownedComment;
    private TemplateSignature signature;

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
    @Any(metaDef = "OperationMetaDef", metaColumn = @Column(name = "parameteredElementType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "parameteredElementId", table = "OperationTemplateParameter")
    public Operation getParameteredElement() {
        return parameteredElement;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = OperationImpl.class)
    public void setParameteredElement(Operation parameteredElement) {
        this.parameteredElement = parameteredElement;
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
    @Any(metaDef = "ParameterableElementMetaDef", metaColumn = @Column(name = "default_Type"), fetch = FetchType.LAZY)
    @JoinColumn(name = "default_Id", table = "OperationTemplateParameter")
    public ParameterableElement getDefault() {
        return default_;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ParameterableElementImpl.class)
    public void setDefault(ParameterableElement default_) {
        this.default_ = default_;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ParameterableElementMetaDef", metaColumn = @Column(name = "ownedDefaultType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "ownedDefaultId", table = "OperationTemplateParameter")
    public ParameterableElement getOwnedDefault() {
        return ownedDefault;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ParameterableElementImpl.class)
    public void setOwnedDefault(ParameterableElement ownedDefault) {
        this.ownedDefault = ownedDefault;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ParameterableElementMetaDef", metaColumn = @Column(name = "ownedParameteredElementType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "ownedParameteredElementId", table = "OperationTemplateParameter")
    public ParameterableElement getOwnedParameteredElement() {
        return ownedParameteredElement;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ParameterableElementImpl.class)
    public void setOwnedParameteredElement(ParameterableElement ownedParameteredElement) {
        this.ownedParameteredElement = ownedParameteredElement;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "OperationTemplateParameter_ownedComment",
        joinColumns = @JoinColumn(name = "OperationTemplateParameterId"),
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
    @Any(metaDef = "TemplateSignatureMetaDef", metaColumn = @Column(name = "signatureType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "signatureId", table = "OperationTemplateParameter")
    public TemplateSignature getSignature() {
        return signature;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = TemplateSignatureImpl.class)
    public void setSignature(TemplateSignature signature) {
        this.signature = signature;
    }

}
