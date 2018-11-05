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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.SecondaryTable;
import javax.persistence.Transient;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.Table;
import org.openmbee.spec.uml.Classifier;
import org.openmbee.spec.uml.Comment;
import org.openmbee.spec.uml.Dependency;
import org.openmbee.spec.uml.Deployment;
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.Enumeration;
import org.openmbee.spec.uml.EnumerationLiteral;
import org.openmbee.spec.uml.Namespace;
import org.openmbee.spec.uml.PackageableElement;
import org.openmbee.spec.uml.Slot;
import org.openmbee.spec.uml.StringExpression;
import org.openmbee.spec.uml.TemplateParameter;
import org.openmbee.spec.uml.ValueSpecification;
import org.openmbee.spec.uml.VisibilityKind;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "EnumerationLiteral")
@Table(appliesTo = "EnumerationLiteral", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "EnumerationLiteral")
@JsonTypeName(value = "EnumerationLiteral")
public class EnumerationLiteralImpl extends MofObjectImpl implements EnumerationLiteral {

    private Collection<PackageableElement> deployedElement;
    private Element owner;
    private Enumeration enumeration;
    private Collection<Slot> slot;
    private ValueSpecification specification;
    private Collection<Element> ownedElement;
    private TemplateParameter owningTemplateParameter;
    private String qualifiedName;
    private VisibilityKind visibility;
    private String name;
    private Namespace namespace;
    private Collection<Classifier> classifier;
    private Collection<Deployment> deployment;
    private TemplateParameter templateParameter;
    private Collection<Dependency> clientDependency;
    private StringExpression nameExpression;
    private Collection<Comment> ownedComment;

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<PackageableElement> getDeployedElement() {
        if (deployedElement == null) {
            deployedElement = new ArrayList<>();
        }
        return deployedElement;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = PackageableElementImpl.class)
    public void setDeployedElement(Collection<PackageableElement> deployedElement) {
        this.deployedElement = deployedElement;
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
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "EnumerationMetaDef", metaColumn = @Column(name = "enumerationType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "enumerationId", table = "EnumerationLiteral")
    public Enumeration getEnumeration() {
        return enumeration;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = EnumerationImpl.class)
    public void setEnumeration(Enumeration enumeration) {
        this.enumeration = enumeration;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "SlotMetaDef", metaColumn = @Column(name = "slotType"), fetch = FetchType.LAZY)
    @JoinTable(name = "EnumerationLiteral_slot",
        joinColumns = @JoinColumn(name = "EnumerationLiteralId"),
        inverseJoinColumns = @JoinColumn(name = "slotId"))
    public Collection<Slot> getSlot() {
        if (slot == null) {
            slot = new ArrayList<>();
        }
        return slot;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = SlotImpl.class)
    public void setSlot(Collection<Slot> slot) {
        this.slot = slot;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ValueSpecificationMetaDef", metaColumn = @Column(name = "specificationType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "specificationId", table = "EnumerationLiteral")
    public ValueSpecification getSpecification() {
        return specification;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ValueSpecificationImpl.class)
    public void setSpecification(ValueSpecification specification) {
        this.specification = specification;
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
    @Any(metaDef = "TemplateParameterMetaDef", metaColumn = @Column(name = "owningTemplateParameterType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "owningTemplateParameterId", table = "EnumerationLiteral")
    public TemplateParameter getOwningTemplateParameter() {
        return owningTemplateParameter;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = TemplateParameterImpl.class)
    public void setOwningTemplateParameter(TemplateParameter owningTemplateParameter) {
        this.owningTemplateParameter = owningTemplateParameter;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Lob
    @org.hibernate.annotations.Type(type = "org.hibernate.type.TextType")
    @Transient
    public String getQualifiedName() {
        return qualifiedName;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Enumerated(EnumType.STRING)
    public VisibilityKind getVisibility() {
        return visibility;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setVisibility(VisibilityKind visibility) {
        this.visibility = visibility;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Lob
    @org.hibernate.annotations.Type(type = "org.hibernate.type.TextType")
    @Column(name = "name", table = "EnumerationLiteral")
    public String getName() {
        return name;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Transient
    public Namespace getNamespace() {
        return namespace;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = NamespaceImpl.class)
    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ClassifierMetaDef", metaColumn = @Column(name = "classifierType"), fetch = FetchType.LAZY)
    @JoinTable(name = "EnumerationLiteral_classifier",
        joinColumns = @JoinColumn(name = "EnumerationLiteralId"),
        inverseJoinColumns = @JoinColumn(name = "classifierId"))
    public Collection<Classifier> getClassifier() {
        if (classifier == null) {
            classifier = new ArrayList<>();
        }
        return classifier;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ClassifierImpl.class)
    public void setClassifier(Collection<Classifier> classifier) {
        this.classifier = classifier;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "DeploymentMetaDef", metaColumn = @Column(name = "deploymentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "EnumerationLiteral_deployment",
        joinColumns = @JoinColumn(name = "EnumerationLiteralId"),
        inverseJoinColumns = @JoinColumn(name = "deploymentId"))
    public Collection<Deployment> getDeployment() {
        if (deployment == null) {
            deployment = new ArrayList<>();
        }
        return deployment;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = DeploymentImpl.class)
    public void setDeployment(Collection<Deployment> deployment) {
        this.deployment = deployment;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "TemplateParameterMetaDef", metaColumn = @Column(name = "templateParameterType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "templateParameterId", table = "EnumerationLiteral")
    public TemplateParameter getTemplateParameter() {
        return templateParameter;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = TemplateParameterImpl.class)
    public void setTemplateParameter(TemplateParameter templateParameter) {
        this.templateParameter = templateParameter;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<Dependency> getClientDependency() {
        if (clientDependency == null) {
            clientDependency = new ArrayList<>();
        }
        return clientDependency;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = DependencyImpl.class)
    public void setClientDependency(Collection<Dependency> clientDependency) {
        this.clientDependency = clientDependency;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "StringExpressionMetaDef", metaColumn = @Column(name = "nameExpressionType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "nameExpressionId", table = "EnumerationLiteral")
    public StringExpression getNameExpression() {
        return nameExpression;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = StringExpressionImpl.class)
    public void setNameExpression(StringExpression nameExpression) {
        this.nameExpression = nameExpression;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "EnumerationLiteral_ownedComment",
        joinColumns = @JoinColumn(name = "EnumerationLiteralId"),
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
