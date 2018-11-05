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
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
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
import org.openmbee.spec.uml.BehavioralFeature;
import org.openmbee.spec.uml.BehavioredClassifier;
import org.openmbee.spec.uml.Classifier;
import org.openmbee.spec.uml.ClassifierTemplateParameter;
import org.openmbee.spec.uml.CollaborationUse;
import org.openmbee.spec.uml.Comment;
import org.openmbee.spec.uml.ConnectableElement;
import org.openmbee.spec.uml.Connector;
import org.openmbee.spec.uml.Constraint;
import org.openmbee.spec.uml.Dependency;
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.ElementImport;
import org.openmbee.spec.uml.Extension;
import org.openmbee.spec.uml.Feature;
import org.openmbee.spec.uml.FunctionBehavior;
import org.openmbee.spec.uml.Generalization;
import org.openmbee.spec.uml.GeneralizationSet;
import org.openmbee.spec.uml.InterfaceRealization;
import org.openmbee.spec.uml.NamedElement;
import org.openmbee.spec.uml.Namespace;
import org.openmbee.spec.uml.Operation;
import org.openmbee.spec.uml.Package;
import org.openmbee.spec.uml.PackageImport;
import org.openmbee.spec.uml.PackageableElement;
import org.openmbee.spec.uml.Parameter;
import org.openmbee.spec.uml.ParameterSet;
import org.openmbee.spec.uml.Port;
import org.openmbee.spec.uml.Property;
import org.openmbee.spec.uml.Reception;
import org.openmbee.spec.uml.RedefinableElement;
import org.openmbee.spec.uml.RedefinableTemplateSignature;
import org.openmbee.spec.uml.StringExpression;
import org.openmbee.spec.uml.Substitution;
import org.openmbee.spec.uml.TemplateBinding;
import org.openmbee.spec.uml.TemplateParameter;
import org.openmbee.spec.uml.UseCase;
import org.openmbee.spec.uml.VisibilityKind;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "FunctionBehavior")
@Table(appliesTo = "FunctionBehavior", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "FunctionBehavior")
@JsonTypeName(value = "FunctionBehavior")
public class FunctionBehaviorImpl extends MofObjectImpl implements FunctionBehavior {

    private List<String> body;
    private Collection<Extension> extension;
    private Collection<ParameterSet> ownedParameterSet;
    private Collection<ElementImport> elementImport;
    private String name;
    private Namespace namespace;
    private BehavioralFeature specification;
    private Collection<Dependency> clientDependency;
    private StringExpression nameExpression;
    private Collection<Generalization> generalization;
    private Collection<PackageImport> packageImport;
    private Collection<Constraint> precondition;
    private List<Operation> ownedOperation;
    private Boolean isLeaf;
    private Collection<Element> ownedElement;
    private Collection<Port> ownedPort;
    private List<String> language;
    private List<Property> ownedAttribute;
    private Collection<TemplateBinding> templateBinding;
    private Collection<ConnectableElement> role;
    private CollaborationUse representation;
    private List<Property> attribute;
    private Element owner;
    private Collection<RedefinableElement> redefinedElement;
    private Collection<Feature> feature;
    private Collection<Reception> ownedReception;
    private Boolean isAbstract;
    private Collection<NamedElement> inheritedMember;
    private BehavioredClassifier context;
    private Collection<UseCase> useCase;
    private Collection<Classifier> general;
    private Collection<Behavior> ownedBehavior;
    private Behavior classifierBehavior;
    private Collection<Substitution> substitution;
    private Collection<Connector> ownedConnector;
    private Collection<Property> part;
    private Collection<PackageableElement> importedMember;
    private RedefinableTemplateSignature ownedTemplateSignature;
    private Collection<InterfaceRealization> interfaceRealization;
    private Collection<Classifier> redefinedClassifier;
    private Collection<Constraint> ownedRule;
    private ClassifierTemplateParameter templateParameter;
    private Collection<CollaborationUse> collaborationUse;
    private TemplateParameter owningTemplateParameter;
    private Boolean isReentrant;
    private List<Parameter> ownedParameter;
    private String qualifiedName;
    private Boolean isFinalSpecialization;
    private VisibilityKind visibility;
    private List<Classifier> nestedClassifier;
    private Collection<UseCase> ownedUseCase;
    private Boolean isActive;
    private Collection<NamedElement> ownedMember;
    private Package package_;
    private Collection<Constraint> postcondition;
    private Collection<Behavior> redefinedBehavior;
    private Collection<Classifier> redefinitionContext;
    private Collection<NamedElement> member;
    private Collection<Comment> ownedComment;
    private Collection<GeneralizationSet> powertypeExtent;

    @JsonProperty(required = true)
    @JsonGetter
    @Lob
    @org.hibernate.annotations.Type(type = "org.hibernate.type.TextType")
    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "FunctionBehavior_body",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"))
    public List<String> getBody() {
        if (body == null) {
            body = new ArrayList<>();
        }
        return body;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setBody(List<String> body) {
        this.body = body;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<Extension> getExtension() {
        if (extension == null) {
            extension = new ArrayList<>();
        }
        return extension;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ExtensionImpl.class)
    public void setExtension(Collection<Extension> extension) {
        this.extension = extension;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ParameterSetMetaDef", metaColumn = @Column(name = "ownedParameterSetType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_ownedParameterSet",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
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
    @ManyToAny(metaDef = "ElementImportMetaDef", metaColumn = @Column(name = "elementImportType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_elementImport",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
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
    @Column(name = "name", table = "FunctionBehavior")
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
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "BehavioralFeatureMetaDef", metaColumn = @Column(name = "specificationType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "specificationId", table = "FunctionBehavior")
    public BehavioralFeature getSpecification() {
        return specification;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = BehavioralFeatureImpl.class)
    public void setSpecification(BehavioralFeature specification) {
        this.specification = specification;
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
    @JoinColumn(name = "nameExpressionId", table = "FunctionBehavior")
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
    @ManyToAny(metaDef = "GeneralizationMetaDef", metaColumn = @Column(name = "generalizationType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_generalization",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
        inverseJoinColumns = @JoinColumn(name = "generalizationId"))
    public Collection<Generalization> getGeneralization() {
        if (generalization == null) {
            generalization = new ArrayList<>();
        }
        return generalization;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = GeneralizationImpl.class)
    public void setGeneralization(Collection<Generalization> generalization) {
        this.generalization = generalization;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "PackageImportMetaDef", metaColumn = @Column(name = "packageImportType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_packageImport",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
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
    @ManyToAny(metaDef = "ConstraintMetaDef", metaColumn = @Column(name = "preconditionType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_precondition",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "OperationMetaDef", metaColumn = @Column(name = "ownedOperationType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_ownedOperation",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
        inverseJoinColumns = @JoinColumn(name = "ownedOperationId"))
    public List<Operation> getOwnedOperation() {
        if (ownedOperation == null) {
            ownedOperation = new ArrayList<>();
        }
        return ownedOperation;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = OperationImpl.class)
    public void setOwnedOperation(List<Operation> ownedOperation) {
        this.ownedOperation = ownedOperation;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isLeaf", table = "FunctionBehavior")
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<Port> getOwnedPort() {
        if (ownedPort == null) {
            ownedPort = new ArrayList<>();
        }
        return ownedPort;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = PortImpl.class)
    public void setOwnedPort(Collection<Port> ownedPort) {
        this.ownedPort = ownedPort;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Lob
    @org.hibernate.annotations.Type(type = "org.hibernate.type.TextType")
    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "FunctionBehavior_language",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"))
    public List<String> getLanguage() {
        if (language == null) {
            language = new ArrayList<>();
        }
        return language;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setLanguage(List<String> language) {
        this.language = language;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "PropertyMetaDef", metaColumn = @Column(name = "ownedAttributeType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_ownedAttribute",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
        inverseJoinColumns = @JoinColumn(name = "ownedAttributeId"))
    public List<Property> getOwnedAttribute() {
        if (ownedAttribute == null) {
            ownedAttribute = new ArrayList<>();
        }
        return ownedAttribute;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = PropertyImpl.class)
    public void setOwnedAttribute(List<Property> ownedAttribute) {
        this.ownedAttribute = ownedAttribute;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "TemplateBindingMetaDef", metaColumn = @Column(name = "templateBindingType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_templateBinding",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
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
    public Collection<ConnectableElement> getRole() {
        if (role == null) {
            role = new ArrayList<>();
        }
        return role;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ConnectableElementImpl.class)
    public void setRole(Collection<ConnectableElement> role) {
        this.role = role;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "CollaborationUseMetaDef", metaColumn = @Column(name = "representationType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "representationId", table = "FunctionBehavior")
    public CollaborationUse getRepresentation() {
        return representation;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = CollaborationUseImpl.class)
    public void setRepresentation(CollaborationUse representation) {
        this.representation = representation;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public List<Property> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<>();
        }
        return attribute;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = PropertyImpl.class)
    public void setAttribute(List<Property> attribute) {
        this.attribute = attribute;
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<Feature> getFeature() {
        if (feature == null) {
            feature = new ArrayList<>();
        }
        return feature;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = FeatureImpl.class)
    public void setFeature(Collection<Feature> feature) {
        this.feature = feature;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ReceptionMetaDef", metaColumn = @Column(name = "ownedReceptionType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_ownedReception",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
        inverseJoinColumns = @JoinColumn(name = "ownedReceptionId"))
    public Collection<Reception> getOwnedReception() {
        if (ownedReception == null) {
            ownedReception = new ArrayList<>();
        }
        return ownedReception;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ReceptionImpl.class)
    public void setOwnedReception(Collection<Reception> ownedReception) {
        this.ownedReception = ownedReception;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isAbstract", table = "FunctionBehavior")
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<NamedElement> getInheritedMember() {
        if (inheritedMember == null) {
            inheritedMember = new ArrayList<>();
        }
        return inheritedMember;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = NamedElementImpl.class)
    public void setInheritedMember(Collection<NamedElement> inheritedMember) {
        this.inheritedMember = inheritedMember;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Transient
    public BehavioredClassifier getContext() {
        return context;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = BehavioredClassifierImpl.class)
    public void setContext(BehavioredClassifier context) {
        this.context = context;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "UseCaseMetaDef", metaColumn = @Column(name = "useCaseType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_useCase",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
        inverseJoinColumns = @JoinColumn(name = "useCaseId"))
    public Collection<UseCase> getUseCase() {
        if (useCase == null) {
            useCase = new ArrayList<>();
        }
        return useCase;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = UseCaseImpl.class)
    public void setUseCase(Collection<UseCase> useCase) {
        this.useCase = useCase;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<Classifier> getGeneral() {
        if (general == null) {
            general = new ArrayList<>();
        }
        return general;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ClassifierImpl.class)
    public void setGeneral(Collection<Classifier> general) {
        this.general = general;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "BehaviorMetaDef", metaColumn = @Column(name = "ownedBehaviorType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_ownedBehavior",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
        inverseJoinColumns = @JoinColumn(name = "ownedBehaviorId"))
    public Collection<Behavior> getOwnedBehavior() {
        if (ownedBehavior == null) {
            ownedBehavior = new ArrayList<>();
        }
        return ownedBehavior;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = BehaviorImpl.class)
    public void setOwnedBehavior(Collection<Behavior> ownedBehavior) {
        this.ownedBehavior = ownedBehavior;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "BehaviorMetaDef", metaColumn = @Column(name = "classifierBehaviorType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "classifierBehaviorId", table = "FunctionBehavior")
    public Behavior getClassifierBehavior() {
        return classifierBehavior;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = BehaviorImpl.class)
    public void setClassifierBehavior(Behavior classifierBehavior) {
        this.classifierBehavior = classifierBehavior;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "SubstitutionMetaDef", metaColumn = @Column(name = "substitutionType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_substitution",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
        inverseJoinColumns = @JoinColumn(name = "substitutionId"))
    public Collection<Substitution> getSubstitution() {
        if (substitution == null) {
            substitution = new ArrayList<>();
        }
        return substitution;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = SubstitutionImpl.class)
    public void setSubstitution(Collection<Substitution> substitution) {
        this.substitution = substitution;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ConnectorMetaDef", metaColumn = @Column(name = "ownedConnectorType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_ownedConnector",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
        inverseJoinColumns = @JoinColumn(name = "ownedConnectorId"))
    public Collection<Connector> getOwnedConnector() {
        if (ownedConnector == null) {
            ownedConnector = new ArrayList<>();
        }
        return ownedConnector;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ConnectorImpl.class)
    public void setOwnedConnector(Collection<Connector> ownedConnector) {
        this.ownedConnector = ownedConnector;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<Property> getPart() {
        if (part == null) {
            part = new ArrayList<>();
        }
        return part;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = PropertyImpl.class)
    public void setPart(Collection<Property> part) {
        this.part = part;
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
    @Any(metaDef = "RedefinableTemplateSignatureMetaDef", metaColumn = @Column(name = "ownedTemplateSignatureType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "ownedTemplateSignatureId", table = "FunctionBehavior")
    public RedefinableTemplateSignature getOwnedTemplateSignature() {
        return ownedTemplateSignature;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = RedefinableTemplateSignatureImpl.class)
    public void setOwnedTemplateSignature(RedefinableTemplateSignature ownedTemplateSignature) {
        this.ownedTemplateSignature = ownedTemplateSignature;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "InterfaceRealizationMetaDef", metaColumn = @Column(name = "interfaceRealizationType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_interfaceRealization",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
        inverseJoinColumns = @JoinColumn(name = "interfaceRealizationId"))
    public Collection<InterfaceRealization> getInterfaceRealization() {
        if (interfaceRealization == null) {
            interfaceRealization = new ArrayList<>();
        }
        return interfaceRealization;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = InterfaceRealizationImpl.class)
    public void setInterfaceRealization(Collection<InterfaceRealization> interfaceRealization) {
        this.interfaceRealization = interfaceRealization;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ClassifierMetaDef", metaColumn = @Column(name = "redefinedClassifierType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_redefinedClassifier",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
        inverseJoinColumns = @JoinColumn(name = "redefinedClassifierId"))
    public Collection<Classifier> getRedefinedClassifier() {
        if (redefinedClassifier == null) {
            redefinedClassifier = new ArrayList<>();
        }
        return redefinedClassifier;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ClassifierImpl.class)
    public void setRedefinedClassifier(Collection<Classifier> redefinedClassifier) {
        this.redefinedClassifier = redefinedClassifier;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ConstraintMetaDef", metaColumn = @Column(name = "ownedRuleType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_ownedRule",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
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
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ClassifierTemplateParameterMetaDef", metaColumn = @Column(name = "templateParameterType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "templateParameterId", table = "FunctionBehavior")
    public ClassifierTemplateParameter getTemplateParameter() {
        return templateParameter;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ClassifierTemplateParameterImpl.class)
    public void setTemplateParameter(ClassifierTemplateParameter templateParameter) {
        this.templateParameter = templateParameter;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CollaborationUseMetaDef", metaColumn = @Column(name = "collaborationUseType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_collaborationUse",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
        inverseJoinColumns = @JoinColumn(name = "collaborationUseId"))
    public Collection<CollaborationUse> getCollaborationUse() {
        if (collaborationUse == null) {
            collaborationUse = new ArrayList<>();
        }
        return collaborationUse;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = CollaborationUseImpl.class)
    public void setCollaborationUse(Collection<CollaborationUse> collaborationUse) {
        this.collaborationUse = collaborationUse;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "TemplateParameterMetaDef", metaColumn = @Column(name = "owningTemplateParameterType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "owningTemplateParameterId", table = "FunctionBehavior")
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
    @Column(name = "isReentrant", table = "FunctionBehavior")
    public Boolean isReentrant() {
        return isReentrant;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setReentrant(Boolean isReentrant) {
        this.isReentrant = isReentrant;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ParameterMetaDef", metaColumn = @Column(name = "ownedParameterType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_ownedParameter",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
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
    @Column(name = "isFinalSpecialization", table = "FunctionBehavior")
    public Boolean isFinalSpecialization() {
        return isFinalSpecialization;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setFinalSpecialization(Boolean isFinalSpecialization) {
        this.isFinalSpecialization = isFinalSpecialization;
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ClassifierMetaDef", metaColumn = @Column(name = "nestedClassifierType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_nestedClassifier",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
        inverseJoinColumns = @JoinColumn(name = "nestedClassifierId"))
    public List<Classifier> getNestedClassifier() {
        if (nestedClassifier == null) {
            nestedClassifier = new ArrayList<>();
        }
        return nestedClassifier;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ClassifierImpl.class)
    public void setNestedClassifier(List<Classifier> nestedClassifier) {
        this.nestedClassifier = nestedClassifier;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "UseCaseMetaDef", metaColumn = @Column(name = "ownedUseCaseType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_ownedUseCase",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
        inverseJoinColumns = @JoinColumn(name = "ownedUseCaseId"))
    public Collection<UseCase> getOwnedUseCase() {
        if (ownedUseCase == null) {
            ownedUseCase = new ArrayList<>();
        }
        return ownedUseCase;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = UseCaseImpl.class)
    public void setOwnedUseCase(Collection<UseCase> ownedUseCase) {
        this.ownedUseCase = ownedUseCase;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isActive", table = "FunctionBehavior")
    public Boolean isActive() {
        return isActive;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setActive(Boolean isActive) {
        this.isActive = isActive;
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
    @Any(metaDef = "PackageMetaDef", metaColumn = @Column(name = "package_Type"), fetch = FetchType.LAZY)
    @JoinColumn(name = "package_Id", table = "FunctionBehavior")
    public Package getPackage() {
        return package_;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = PackageImpl.class)
    public void setPackage(Package package_) {
        this.package_ = package_;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ConstraintMetaDef", metaColumn = @Column(name = "postconditionType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_postcondition",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "BehaviorMetaDef", metaColumn = @Column(name = "redefinedBehaviorType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_redefinedBehavior",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
        inverseJoinColumns = @JoinColumn(name = "redefinedBehaviorId"))
    public Collection<Behavior> getRedefinedBehavior() {
        if (redefinedBehavior == null) {
            redefinedBehavior = new ArrayList<>();
        }
        return redefinedBehavior;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = BehaviorImpl.class)
    public void setRedefinedBehavior(Collection<Behavior> redefinedBehavior) {
        this.redefinedBehavior = redefinedBehavior;
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
    @JoinTable(name = "FunctionBehavior_ownedComment",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
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
    @ManyToAny(metaDef = "GeneralizationSetMetaDef", metaColumn = @Column(name = "powertypeExtentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "FunctionBehavior_powertypeExtent",
        joinColumns = @JoinColumn(name = "FunctionBehaviorId"),
        inverseJoinColumns = @JoinColumn(name = "powertypeExtentId"))
    public Collection<GeneralizationSet> getPowertypeExtent() {
        if (powertypeExtent == null) {
            powertypeExtent = new ArrayList<>();
        }
        return powertypeExtent;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = GeneralizationSetImpl.class)
    public void setPowertypeExtent(Collection<GeneralizationSet> powertypeExtent) {
        this.powertypeExtent = powertypeExtent;
    }

}
