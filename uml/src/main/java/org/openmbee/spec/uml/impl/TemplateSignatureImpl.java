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
import org.openmbee.spec.uml.TemplateParameter;
import org.openmbee.spec.uml.TemplateSignature;
import org.openmbee.spec.uml.TemplateableElement;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "TemplateSignature")
@Table(appliesTo = "TemplateSignature", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "TemplateSignature")
@JsonTypeName(value = "TemplateSignature")
public class TemplateSignatureImpl extends MofObjectImpl implements TemplateSignature {

    private Element owner;
    private List<TemplateParameter> ownedParameter;
    private Collection<Element> ownedElement;
    private TemplateableElement template;
    private List<TemplateParameter> parameter;
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "TemplateParameterMetaDef", metaColumn = @Column(name = "ownedParameterType"), fetch = FetchType.LAZY)
    @JoinTable(name = "TemplateSignature_ownedParameter",
        joinColumns = @JoinColumn(name = "TemplateSignatureId"),
        inverseJoinColumns = @JoinColumn(name = "ownedParameterId"))
    public List<TemplateParameter> getOwnedParameter() {
        if (ownedParameter == null) {
            ownedParameter = new ArrayList<>();
        }
        return ownedParameter;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = TemplateParameterImpl.class)
    public void setOwnedParameter(List<TemplateParameter> ownedParameter) {
        this.ownedParameter = ownedParameter;
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
    @Any(metaDef = "TemplateableElementMetaDef", metaColumn = @Column(name = "templateType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "templateId", table = "TemplateSignature")
    public TemplateableElement getTemplate() {
        return template;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = TemplateableElementImpl.class)
    public void setTemplate(TemplateableElement template) {
        this.template = template;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "TemplateParameterMetaDef", metaColumn = @Column(name = "parameterType"), fetch = FetchType.LAZY)
    @JoinTable(name = "TemplateSignature_parameter",
        joinColumns = @JoinColumn(name = "TemplateSignatureId"),
        inverseJoinColumns = @JoinColumn(name = "parameterId"))
    public List<TemplateParameter> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<>();
        }
        return parameter;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = TemplateParameterImpl.class)
    public void setParameter(List<TemplateParameter> parameter) {
        this.parameter = parameter;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "TemplateSignature_ownedComment",
        joinColumns = @JoinColumn(name = "TemplateSignatureId"),
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
