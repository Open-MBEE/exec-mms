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
import org.openmbee.spec.uml.AggregationKind;
import org.openmbee.spec.uml.Association;
import org.openmbee.spec.uml.Class;
import org.openmbee.spec.uml.Classifier;
import org.openmbee.spec.uml.Comment;
import org.openmbee.spec.uml.ConnectableElementTemplateParameter;
import org.openmbee.spec.uml.ConnectorEnd;
import org.openmbee.spec.uml.DataType;
import org.openmbee.spec.uml.Dependency;
import org.openmbee.spec.uml.Deployment;
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.Interface;
import org.openmbee.spec.uml.Namespace;
import org.openmbee.spec.uml.PackageableElement;
import org.openmbee.spec.uml.Property;
import org.openmbee.spec.uml.RedefinableElement;
import org.openmbee.spec.uml.StringExpression;
import org.openmbee.spec.uml.TemplateParameter;
import org.openmbee.spec.uml.Type;
import org.openmbee.spec.uml.ValueSpecification;
import org.openmbee.spec.uml.VisibilityKind;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "Property")
@Table(appliesTo = "Property", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "Property")
@JsonTypeName(value = "Property")
public class PropertyImpl extends MofObjectImpl implements Property {

    private Collection<ConnectorEnd> end;
    private Element owner;
    private Collection<PackageableElement> deployedElement;
    private Collection<RedefinableElement> redefinedElement;
    private Boolean isReadOnly;
    private Property opposite;
    private VisibilityKind visibility;
    private Association association;
    private Type type;
    private ValueSpecification lowerValue;
    private Boolean isOrdered;
    private Integer upper;
    private Boolean isStatic;
    private AggregationKind aggregation;
    private String name;
    private Namespace namespace;
    private List<Property> qualifier;
    private Integer lower;
    private Class class_;
    private Collection<Dependency> clientDependency;
    private StringExpression nameExpression;
    private Interface interface_;
    private Boolean isID;
    private Boolean isDerivedUnion;
    private ConnectableElementTemplateParameter templateParameter;
    private Boolean isComposite;
    private Boolean isLeaf;
    private Collection<Element> ownedElement;
    private TemplateParameter owningTemplateParameter;
    private Collection<Property> subsettedProperty;
    private DataType datatype;
    private ValueSpecification upperValue;
    private String qualifiedName;
    private Property associationEnd;
    private Collection<Property> redefinedProperty;
    private Collection<Deployment> deployment;
    private ValueSpecification defaultValue;
    private Boolean isUnique;
    private Classifier featuringClassifier;
    private Collection<Classifier> redefinitionContext;
    private Boolean isDerived;
    private Collection<Comment> ownedComment;
    private Association owningAssociation;

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<ConnectorEnd> getEnd() {
        if (end == null) {
            end = new ArrayList<>();
        }
        return end;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ConnectorEndImpl.class)
    public void setEnd(Collection<ConnectorEnd> end) {
        this.end = end;
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<RedefinableElement> getRedefinedElement() {
        if (redefinedElement == null) {
            redefinedElement = new ArrayList<>();
        }
        return redefinedElement;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = RedefinableElementImpl.class)
    public void setRedefinedElement(Collection<RedefinableElement> redefinedElement) {
        this.redefinedElement = redefinedElement;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isReadOnly", table = "Property")
    public Boolean isReadOnly() {
        return isReadOnly;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setReadOnly(Boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Transient
    public Property getOpposite() {
        return opposite;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = PropertyImpl.class)
    public void setOpposite(Property opposite) {
        this.opposite = opposite;
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
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "AssociationMetaDef", metaColumn = @Column(name = "associationType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "associationId", table = "Property")
    public Association getAssociation() {
        return association;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = AssociationImpl.class)
    public void setAssociation(Association association) {
        this.association = association;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "TypeMetaDef", metaColumn = @Column(name = "typeType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "typeId", table = "Property")
    public Type getType() {
        return type;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = TypeImpl.class)
    public void setType(Type type) {
        this.type = type;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ValueSpecificationMetaDef", metaColumn = @Column(name = "lowerValueType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "lowerValueId", table = "Property")
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
    @Column(name = "isOrdered", table = "Property")
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
    @Column(name = "isStatic", table = "Property")
    public Boolean isStatic() {
        return isStatic;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setStatic(Boolean isStatic) {
        this.isStatic = isStatic;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Enumerated(EnumType.STRING)
    public AggregationKind getAggregation() {
        return aggregation;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setAggregation(AggregationKind aggregation) {
        this.aggregation = aggregation;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Lob
    @org.hibernate.annotations.Type(type = "org.hibernate.type.TextType")
    @Column(name = "name", table = "Property")
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
    @ManyToAny(metaDef = "PropertyMetaDef", metaColumn = @Column(name = "qualifierType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Property_qualifier",
        joinColumns = @JoinColumn(name = "PropertyId"),
        inverseJoinColumns = @JoinColumn(name = "qualifierId"))
    public List<Property> getQualifier() {
        if (qualifier == null) {
            qualifier = new ArrayList<>();
        }
        return qualifier;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = PropertyImpl.class)
    public void setQualifier(List<Property> qualifier) {
        this.qualifier = qualifier;
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
    @Any(metaDef = "ClassMetaDef", metaColumn = @Column(name = "class_Type"), fetch = FetchType.LAZY)
    @JoinColumn(name = "class_Id", table = "Property")
    public Class getClass_() {
        return class_;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ClassImpl.class)
    public void setClass_(Class class_) {
        this.class_ = class_;
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
    @JoinColumn(name = "nameExpressionId", table = "Property")
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
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "InterfaceMetaDef", metaColumn = @Column(name = "interface_Type"), fetch = FetchType.LAZY)
    @JoinColumn(name = "interface_Id", table = "Property")
    public Interface getInterface() {
        return interface_;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = InterfaceImpl.class)
    public void setInterface(Interface interface_) {
        this.interface_ = interface_;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isID", table = "Property")
    public Boolean isID() {
        return isID;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setID(Boolean isID) {
        this.isID = isID;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isDerivedUnion", table = "Property")
    public Boolean isDerivedUnion() {
        return isDerivedUnion;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setDerivedUnion(Boolean isDerivedUnion) {
        this.isDerivedUnion = isDerivedUnion;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ConnectableElementTemplateParameterMetaDef", metaColumn = @Column(name = "templateParameterType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "templateParameterId", table = "Property")
    public ConnectableElementTemplateParameter getTemplateParameter() {
        return templateParameter;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ConnectableElementTemplateParameterImpl.class)
    public void setTemplateParameter(ConnectableElementTemplateParameter templateParameter) {
        this.templateParameter = templateParameter;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Transient
    public Boolean isComposite() {
        return isComposite;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setComposite(Boolean isComposite) {
        this.isComposite = isComposite;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isLeaf", table = "Property")
    public Boolean isLeaf() {
        return isLeaf;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setLeaf(Boolean isLeaf) {
        this.isLeaf = isLeaf;
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
    @JoinColumn(name = "owningTemplateParameterId", table = "Property")
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "PropertyMetaDef", metaColumn = @Column(name = "subsettedPropertyType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Property_subsettedProperty",
        joinColumns = @JoinColumn(name = "PropertyId"),
        inverseJoinColumns = @JoinColumn(name = "subsettedPropertyId"))
    public Collection<Property> getSubsettedProperty() {
        if (subsettedProperty == null) {
            subsettedProperty = new ArrayList<>();
        }
        return subsettedProperty;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = PropertyImpl.class)
    public void setSubsettedProperty(Collection<Property> subsettedProperty) {
        this.subsettedProperty = subsettedProperty;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "DataTypeMetaDef", metaColumn = @Column(name = "datatypeType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "datatypeId", table = "Property")
    public DataType getDatatype() {
        return datatype;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = DataTypeImpl.class)
    public void setDatatype(DataType datatype) {
        this.datatype = datatype;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ValueSpecificationMetaDef", metaColumn = @Column(name = "upperValueType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "upperValueId", table = "Property")
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
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "PropertyMetaDef", metaColumn = @Column(name = "associationEndType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "associationEndId", table = "Property")
    public Property getAssociationEnd() {
        return associationEnd;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = PropertyImpl.class)
    public void setAssociationEnd(Property associationEnd) {
        this.associationEnd = associationEnd;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "PropertyMetaDef", metaColumn = @Column(name = "redefinedPropertyType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Property_redefinedProperty",
        joinColumns = @JoinColumn(name = "PropertyId"),
        inverseJoinColumns = @JoinColumn(name = "redefinedPropertyId"))
    public Collection<Property> getRedefinedProperty() {
        if (redefinedProperty == null) {
            redefinedProperty = new ArrayList<>();
        }
        return redefinedProperty;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = PropertyImpl.class)
    public void setRedefinedProperty(Collection<Property> redefinedProperty) {
        this.redefinedProperty = redefinedProperty;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "DeploymentMetaDef", metaColumn = @Column(name = "deploymentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Property_deployment",
        joinColumns = @JoinColumn(name = "PropertyId"),
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
    @Any(metaDef = "ValueSpecificationMetaDef", metaColumn = @Column(name = "defaultValueType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "defaultValueId", table = "Property")
    public ValueSpecification getDefaultValue() {
        return defaultValue;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ValueSpecificationImpl.class)
    public void setDefaultValue(ValueSpecification defaultValue) {
        this.defaultValue = defaultValue;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isUnique", table = "Property")
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
    @JsonSerialize(using = MofObjectSerializer.class)
    @Transient
    public Classifier getFeaturingClassifier() {
        return featuringClassifier;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ClassifierImpl.class)
    public void setFeaturingClassifier(Classifier featuringClassifier) {
        this.featuringClassifier = featuringClassifier;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<Classifier> getRedefinitionContext() {
        if (redefinitionContext == null) {
            redefinitionContext = new ArrayList<>();
        }
        return redefinitionContext;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ClassifierImpl.class)
    public void setRedefinitionContext(Collection<Classifier> redefinitionContext) {
        this.redefinitionContext = redefinitionContext;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isDerived", table = "Property")
    public Boolean isDerived() {
        return isDerived;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setDerived(Boolean isDerived) {
        this.isDerived = isDerived;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Property_ownedComment",
        joinColumns = @JoinColumn(name = "PropertyId"),
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
    @Any(metaDef = "AssociationMetaDef", metaColumn = @Column(name = "owningAssociationType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "owningAssociationId", table = "Property")
    public Association getOwningAssociation() {
        return owningAssociation;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = AssociationImpl.class)
    public void setOwningAssociation(Association owningAssociation) {
        this.owningAssociation = owningAssociation;
    }

}
