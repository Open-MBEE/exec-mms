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
import org.openmbee.spec.uml.Behavior;
import org.openmbee.spec.uml.CallConcurrencyKind;
import org.openmbee.spec.uml.Class;
import org.openmbee.spec.uml.Classifier;
import org.openmbee.spec.uml.Comment;
import org.openmbee.spec.uml.Constraint;
import org.openmbee.spec.uml.DataType;
import org.openmbee.spec.uml.Dependency;
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.ElementImport;
import org.openmbee.spec.uml.Interface;
import org.openmbee.spec.uml.NamedElement;
import org.openmbee.spec.uml.Namespace;
import org.openmbee.spec.uml.Operation;
import org.openmbee.spec.uml.OperationTemplateParameter;
import org.openmbee.spec.uml.PackageImport;
import org.openmbee.spec.uml.PackageableElement;
import org.openmbee.spec.uml.Parameter;
import org.openmbee.spec.uml.ParameterSet;
import org.openmbee.spec.uml.RedefinableElement;
import org.openmbee.spec.uml.StringExpression;
import org.openmbee.spec.uml.TemplateBinding;
import org.openmbee.spec.uml.TemplateParameter;
import org.openmbee.spec.uml.TemplateSignature;
import org.openmbee.spec.uml.Type;
import org.openmbee.spec.uml.VisibilityKind;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "Operation")
@Table(appliesTo = "Operation", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "Operation")
@JsonTypeName(value = "Operation")
public class OperationImpl extends MofObjectImpl implements Operation {

    private Element owner;
    private Collection<RedefinableElement> redefinedElement;
    private TemplateSignature ownedTemplateSignature;
    private VisibilityKind visibility;
    private Interface interface_;
    private Constraint bodyCondition;
    private Boolean isUnique;
    private Collection<Behavior> method;
    private DataType datatype;
    private Integer lower;
    private Boolean isStatic;
    private Boolean isOrdered;
    private Collection<ElementImport> elementImport;
    private String name;
    private Boolean isQuery;
    private Collection<Constraint> precondition;
    private Namespace namespace;
    private Collection<Dependency> clientDependency;
    private StringExpression nameExpression;
    private Collection<PackageableElement> importedMember;
    private Class class_;
    private Collection<PackageImport> packageImport;
    private Collection<ParameterSet> ownedParameterSet;
    private Collection<Type> raisedException;
    private Collection<Constraint> ownedRule;
    private List<Parameter> ownedParameter;
    private Boolean isAbstract;
    private Boolean isLeaf;
    private Collection<Element> ownedElement;
    private OperationTemplateParameter templateParameter;
    private TemplateParameter owningTemplateParameter;
    private String qualifiedName;
    private Type type;
    private Collection<TemplateBinding> templateBinding;
    private Collection<NamedElement> ownedMember;
    private Classifier featuringClassifier;
    private Collection<Classifier> redefinitionContext;
    private Integer upper;
    private Collection<Constraint> postcondition;
    private CallConcurrencyKind concurrency;
    private Collection<NamedElement> member;
    private Collection<Comment> ownedComment;
    private Collection<Operation> redefinedOperation;

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
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "TemplateSignatureMetaDef", metaColumn = @Column(name = "ownedTemplateSignatureType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "ownedTemplateSignatureId", table = "Operation")
    public TemplateSignature getOwnedTemplateSignature() {
        return ownedTemplateSignature;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = TemplateSignatureImpl.class)
    public void setOwnedTemplateSignature(TemplateSignature ownedTemplateSignature) {
        this.ownedTemplateSignature = ownedTemplateSignature;
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
    @Any(metaDef = "InterfaceMetaDef", metaColumn = @Column(name = "interface_Type"), fetch = FetchType.LAZY)
    @JoinColumn(name = "interface_Id", table = "Operation")
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
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ConstraintMetaDef", metaColumn = @Column(name = "bodyConditionType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "bodyConditionId", table = "Operation")
    public Constraint getBodyCondition() {
        return bodyCondition;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ConstraintImpl.class)
    public void setBodyCondition(Constraint bodyCondition) {
        this.bodyCondition = bodyCondition;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Transient
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "BehaviorMetaDef", metaColumn = @Column(name = "methodType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Operation_method",
        joinColumns = @JoinColumn(name = "OperationId"),
        inverseJoinColumns = @JoinColumn(name = "methodId"))
    public Collection<Behavior> getMethod() {
        if (method == null) {
            method = new ArrayList<>();
        }
        return method;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = BehaviorImpl.class)
    public void setMethod(Collection<Behavior> method) {
        this.method = method;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "DataTypeMetaDef", metaColumn = @Column(name = "datatypeType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "datatypeId", table = "Operation")
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
    @Column(name = "isStatic", table = "Operation")
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
    @Transient
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ElementImportMetaDef", metaColumn = @Column(name = "elementImportType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Operation_elementImport",
        joinColumns = @JoinColumn(name = "OperationId"),
        inverseJoinColumns = @JoinColumn(name = "elementImportId"))
    public Collection<ElementImport> getElementImport() {
        if (elementImport == null) {
            elementImport = new ArrayList<>();
        }
        return elementImport;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ElementImportImpl.class)
    public void setElementImport(Collection<ElementImport> elementImport) {
        this.elementImport = elementImport;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Lob
    @org.hibernate.annotations.Type(type = "org.hibernate.type.TextType")
    @Column(name = "name", table = "Operation")
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
    @Column(name = "isQuery", table = "Operation")
    public Boolean isQuery() {
        return isQuery;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setQuery(Boolean isQuery) {
        this.isQuery = isQuery;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ConstraintMetaDef", metaColumn = @Column(name = "preconditionType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Operation_precondition",
        joinColumns = @JoinColumn(name = "OperationId"),
        inverseJoinColumns = @JoinColumn(name = "preconditionId"))
    public Collection<Constraint> getPrecondition() {
        if (precondition == null) {
            precondition = new ArrayList<>();
        }
        return precondition;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ConstraintImpl.class)
    public void setPrecondition(Collection<Constraint> precondition) {
        this.precondition = precondition;
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
    @JoinColumn(name = "nameExpressionId", table = "Operation")
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
    @Transient
    public Collection<PackageableElement> getImportedMember() {
        if (importedMember == null) {
            importedMember = new ArrayList<>();
        }
        return importedMember;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = PackageableElementImpl.class)
    public void setImportedMember(Collection<PackageableElement> importedMember) {
        this.importedMember = importedMember;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ClassMetaDef", metaColumn = @Column(name = "class_Type"), fetch = FetchType.LAZY)
    @JoinColumn(name = "class_Id", table = "Operation")
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
    @ManyToAny(metaDef = "PackageImportMetaDef", metaColumn = @Column(name = "packageImportType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Operation_packageImport",
        joinColumns = @JoinColumn(name = "OperationId"),
        inverseJoinColumns = @JoinColumn(name = "packageImportId"))
    public Collection<PackageImport> getPackageImport() {
        if (packageImport == null) {
            packageImport = new ArrayList<>();
        }
        return packageImport;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = PackageImportImpl.class)
    public void setPackageImport(Collection<PackageImport> packageImport) {
        this.packageImport = packageImport;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ParameterSetMetaDef", metaColumn = @Column(name = "ownedParameterSetType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Operation_ownedParameterSet",
        joinColumns = @JoinColumn(name = "OperationId"),
        inverseJoinColumns = @JoinColumn(name = "ownedParameterSetId"))
    public Collection<ParameterSet> getOwnedParameterSet() {
        if (ownedParameterSet == null) {
            ownedParameterSet = new ArrayList<>();
        }
        return ownedParameterSet;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ParameterSetImpl.class)
    public void setOwnedParameterSet(Collection<ParameterSet> ownedParameterSet) {
        this.ownedParameterSet = ownedParameterSet;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "TypeMetaDef", metaColumn = @Column(name = "raisedExceptionType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Operation_raisedException",
        joinColumns = @JoinColumn(name = "OperationId"),
        inverseJoinColumns = @JoinColumn(name = "raisedExceptionId"))
    public Collection<Type> getRaisedException() {
        if (raisedException == null) {
            raisedException = new ArrayList<>();
        }
        return raisedException;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = TypeImpl.class)
    public void setRaisedException(Collection<Type> raisedException) {
        this.raisedException = raisedException;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ConstraintMetaDef", metaColumn = @Column(name = "ownedRuleType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Operation_ownedRule",
        joinColumns = @JoinColumn(name = "OperationId"),
        inverseJoinColumns = @JoinColumn(name = "ownedRuleId"))
    public Collection<Constraint> getOwnedRule() {
        if (ownedRule == null) {
            ownedRule = new ArrayList<>();
        }
        return ownedRule;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ConstraintImpl.class)
    public void setOwnedRule(Collection<Constraint> ownedRule) {
        this.ownedRule = ownedRule;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ParameterMetaDef", metaColumn = @Column(name = "ownedParameterType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Operation_ownedParameter",
        joinColumns = @JoinColumn(name = "OperationId"),
        inverseJoinColumns = @JoinColumn(name = "ownedParameterId"))
    public List<Parameter> getOwnedParameter() {
        if (ownedParameter == null) {
            ownedParameter = new ArrayList<>();
        }
        return ownedParameter;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ParameterImpl.class)
    public void setOwnedParameter(List<Parameter> ownedParameter) {
        this.ownedParameter = ownedParameter;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isAbstract", table = "Operation")
    public Boolean isAbstract() {
        return isAbstract;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setAbstract(Boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isLeaf", table = "Operation")
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
    @Any(metaDef = "OperationTemplateParameterMetaDef", metaColumn = @Column(name = "templateParameterType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "templateParameterId", table = "Operation")
    public OperationTemplateParameter getTemplateParameter() {
        return templateParameter;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = OperationTemplateParameterImpl.class)
    public void setTemplateParameter(OperationTemplateParameter templateParameter) {
        this.templateParameter = templateParameter;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "TemplateParameterMetaDef", metaColumn = @Column(name = "owningTemplateParameterType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "owningTemplateParameterId", table = "Operation")
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
    @JsonSerialize(using = MofObjectSerializer.class)
    @Transient
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "TemplateBindingMetaDef", metaColumn = @Column(name = "templateBindingType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Operation_templateBinding",
        joinColumns = @JoinColumn(name = "OperationId"),
        inverseJoinColumns = @JoinColumn(name = "templateBindingId"))
    public Collection<TemplateBinding> getTemplateBinding() {
        if (templateBinding == null) {
            templateBinding = new ArrayList<>();
        }
        return templateBinding;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = TemplateBindingImpl.class)
    public void setTemplateBinding(Collection<TemplateBinding> templateBinding) {
        this.templateBinding = templateBinding;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<NamedElement> getOwnedMember() {
        if (ownedMember == null) {
            ownedMember = new ArrayList<>();
        }
        return ownedMember;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = NamedElementImpl.class)
    public void setOwnedMember(Collection<NamedElement> ownedMember) {
        this.ownedMember = ownedMember;
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ConstraintMetaDef", metaColumn = @Column(name = "postconditionType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Operation_postcondition",
        joinColumns = @JoinColumn(name = "OperationId"),
        inverseJoinColumns = @JoinColumn(name = "postconditionId"))
    public Collection<Constraint> getPostcondition() {
        if (postcondition == null) {
            postcondition = new ArrayList<>();
        }
        return postcondition;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ConstraintImpl.class)
    public void setPostcondition(Collection<Constraint> postcondition) {
        this.postcondition = postcondition;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Enumerated(EnumType.STRING)
    public CallConcurrencyKind getConcurrency() {
        return concurrency;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setConcurrency(CallConcurrencyKind concurrency) {
        this.concurrency = concurrency;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<NamedElement> getMember() {
        if (member == null) {
            member = new ArrayList<>();
        }
        return member;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = NamedElementImpl.class)
    public void setMember(Collection<NamedElement> member) {
        this.member = member;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Operation_ownedComment",
        joinColumns = @JoinColumn(name = "OperationId"),
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "OperationMetaDef", metaColumn = @Column(name = "redefinedOperationType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Operation_redefinedOperation",
        joinColumns = @JoinColumn(name = "OperationId"),
        inverseJoinColumns = @JoinColumn(name = "redefinedOperationId"))
    public Collection<Operation> getRedefinedOperation() {
        if (redefinedOperation == null) {
            redefinedOperation = new ArrayList<>();
        }
        return redefinedOperation;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = OperationImpl.class)
    public void setRedefinedOperation(Collection<Operation> redefinedOperation) {
        this.redefinedOperation = redefinedOperation;
    }

}
