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
import org.openmbee.spec.uml.Class;
import org.openmbee.spec.uml.Classifier;
import org.openmbee.spec.uml.ClassifierTemplateParameter;
import org.openmbee.spec.uml.CollaborationUse;
import org.openmbee.spec.uml.Comment;
import org.openmbee.spec.uml.Constraint;
import org.openmbee.spec.uml.Dependency;
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.ElementImport;
import org.openmbee.spec.uml.Extension;
import org.openmbee.spec.uml.Feature;
import org.openmbee.spec.uml.Generalization;
import org.openmbee.spec.uml.GeneralizationSet;
import org.openmbee.spec.uml.NamedElement;
import org.openmbee.spec.uml.Namespace;
import org.openmbee.spec.uml.Package;
import org.openmbee.spec.uml.PackageImport;
import org.openmbee.spec.uml.PackageableElement;
import org.openmbee.spec.uml.Property;
import org.openmbee.spec.uml.RedefinableElement;
import org.openmbee.spec.uml.RedefinableTemplateSignature;
import org.openmbee.spec.uml.StringExpression;
import org.openmbee.spec.uml.Substitution;
import org.openmbee.spec.uml.TemplateBinding;
import org.openmbee.spec.uml.TemplateParameter;
import org.openmbee.spec.uml.Type;
import org.openmbee.spec.uml.UseCase;
import org.openmbee.spec.uml.VisibilityKind;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "Extension")
@Table(appliesTo = "Extension", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "Extension")
@JsonTypeName(value = "Extension")
public class ExtensionImpl extends MofObjectImpl implements Extension {

    private Element owner;
    private Collection<RedefinableElement> redefinedElement;
    private Collection<Feature> feature;
    private Boolean isDerived;
    private Boolean isAbstract;
    private Collection<NamedElement> inheritedMember;
    private Collection<UseCase> useCase;
    private Collection<ElementImport> elementImport;
    private List<Property> memberEnd;
    private String name;
    private Collection<Classifier> general;
    private Namespace namespace;
    private Collection<Substitution> substitution;
    private Collection<Dependency> clientDependency;
    private StringExpression nameExpression;
    private Collection<PackageableElement> importedMember;
    private Collection<Generalization> generalization;
    private RedefinableTemplateSignature ownedTemplateSignature;
    private Collection<PackageImport> packageImport;
    private List<Property> ownedEnd;
    private Collection<Classifier> redefinedClassifier;
    private Collection<Constraint> ownedRule;
    private Collection<Property> navigableOwnedEnd;
    private ClassifierTemplateParameter templateParameter;
    private Collection<Type> endType;
    private Boolean isLeaf;
    private Collection<CollaborationUse> collaborationUse;
    private Collection<Element> ownedElement;
    private Class metaclass;
    private TemplateParameter owningTemplateParameter;
    private String qualifiedName;
    private Boolean isFinalSpecialization;
    private VisibilityKind visibility;
    private Collection<UseCase> ownedUseCase;
    private Collection<Element> relatedElement;
    private Collection<TemplateBinding> templateBinding;
    private Collection<NamedElement> ownedMember;
    private Package package_;
    private Boolean isRequired;
    private Collection<Classifier> redefinitionContext;
    private CollaborationUse representation;
    private Collection<NamedElement> member;
    private List<Property> attribute;
    private Collection<Comment> ownedComment;
    private Collection<GeneralizationSet> powertypeExtent;

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
    @Column(name = "isDerived", table = "Extension")
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
    @Column(name = "isAbstract", table = "Extension")
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "UseCaseMetaDef", metaColumn = @Column(name = "useCaseType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Extension_useCase",
        joinColumns = @JoinColumn(name = "ExtensionId"),
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
    @ManyToAny(metaDef = "ElementImportMetaDef", metaColumn = @Column(name = "elementImportType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Extension_elementImport",
        joinColumns = @JoinColumn(name = "ExtensionId"),
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "PropertyMetaDef", metaColumn = @Column(name = "memberEndType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Extension_memberEnd",
        joinColumns = @JoinColumn(name = "ExtensionId"),
        inverseJoinColumns = @JoinColumn(name = "memberEndId"))
    public List<Property> getMemberEnd() {
        if (memberEnd == null) {
            memberEnd = new ArrayList<>();
        }
        return memberEnd;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = PropertyImpl.class)
    public void setMemberEnd(List<Property> memberEnd) {
        this.memberEnd = memberEnd;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Lob
    @org.hibernate.annotations.Type(type = "org.hibernate.type.TextType")
    @Column(name = "name", table = "Extension")
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
    @ManyToAny(metaDef = "SubstitutionMetaDef", metaColumn = @Column(name = "substitutionType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Extension_substitution",
        joinColumns = @JoinColumn(name = "ExtensionId"),
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
    @JoinColumn(name = "nameExpressionId", table = "Extension")
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "GeneralizationMetaDef", metaColumn = @Column(name = "generalizationType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Extension_generalization",
        joinColumns = @JoinColumn(name = "ExtensionId"),
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
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "RedefinableTemplateSignatureMetaDef", metaColumn = @Column(name = "ownedTemplateSignatureType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "ownedTemplateSignatureId", table = "Extension")
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
    @ManyToAny(metaDef = "PackageImportMetaDef", metaColumn = @Column(name = "packageImportType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Extension_packageImport",
        joinColumns = @JoinColumn(name = "ExtensionId"),
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
    @ManyToAny(metaDef = "PropertyMetaDef", metaColumn = @Column(name = "ownedEndType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Extension_ownedEnd",
        joinColumns = @JoinColumn(name = "ExtensionId"),
        inverseJoinColumns = @JoinColumn(name = "ownedEndId"))
    public List<Property> getOwnedEnd() {
        if (ownedEnd == null) {
            ownedEnd = new ArrayList<>();
        }
        return ownedEnd;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = PropertyImpl.class)
    public void setOwnedEnd(List<Property> ownedEnd) {
        this.ownedEnd = ownedEnd;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ClassifierMetaDef", metaColumn = @Column(name = "redefinedClassifierType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Extension_redefinedClassifier",
        joinColumns = @JoinColumn(name = "ExtensionId"),
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
    @JoinTable(name = "Extension_ownedRule",
        joinColumns = @JoinColumn(name = "ExtensionId"),
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
    @ManyToAny(metaDef = "PropertyMetaDef", metaColumn = @Column(name = "navigableOwnedEndType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Extension_navigableOwnedEnd",
        joinColumns = @JoinColumn(name = "ExtensionId"),
        inverseJoinColumns = @JoinColumn(name = "navigableOwnedEndId"))
    public Collection<Property> getNavigableOwnedEnd() {
        if (navigableOwnedEnd == null) {
            navigableOwnedEnd = new ArrayList<>();
        }
        return navigableOwnedEnd;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = PropertyImpl.class)
    public void setNavigableOwnedEnd(Collection<Property> navigableOwnedEnd) {
        this.navigableOwnedEnd = navigableOwnedEnd;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ClassifierTemplateParameterMetaDef", metaColumn = @Column(name = "templateParameterType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "templateParameterId", table = "Extension")
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
    @Transient
    public Collection<Type> getEndType() {
        if (endType == null) {
            endType = new ArrayList<>();
        }
        return endType;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = TypeImpl.class)
    public void setEndType(Collection<Type> endType) {
        this.endType = endType;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isLeaf", table = "Extension")
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
    @ManyToAny(metaDef = "CollaborationUseMetaDef", metaColumn = @Column(name = "collaborationUseType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Extension_collaborationUse",
        joinColumns = @JoinColumn(name = "ExtensionId"),
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
    @Transient
    public Class getMetaclass() {
        return metaclass;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ClassImpl.class)
    public void setMetaclass(Class metaclass) {
        this.metaclass = metaclass;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "TemplateParameterMetaDef", metaColumn = @Column(name = "owningTemplateParameterType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "owningTemplateParameterId", table = "Extension")
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
    @Column(name = "isFinalSpecialization", table = "Extension")
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
    @ManyToAny(metaDef = "UseCaseMetaDef", metaColumn = @Column(name = "ownedUseCaseType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Extension_ownedUseCase",
        joinColumns = @JoinColumn(name = "ExtensionId"),
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
    @ManyToAny(metaDef = "TemplateBindingMetaDef", metaColumn = @Column(name = "templateBindingType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Extension_templateBinding",
        joinColumns = @JoinColumn(name = "ExtensionId"),
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
    @Any(metaDef = "PackageMetaDef", metaColumn = @Column(name = "package_Type"), fetch = FetchType.LAZY)
    @JoinColumn(name = "package_Id", table = "Extension")
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
    @Transient
    public Boolean isRequired() {
        return isRequired;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setRequired(Boolean isRequired) {
        this.isRequired = isRequired;
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
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "CollaborationUseMetaDef", metaColumn = @Column(name = "representationType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "representationId", table = "Extension")
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Extension_ownedComment",
        joinColumns = @JoinColumn(name = "ExtensionId"),
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
    @JoinTable(name = "Extension_powertypeExtent",
        joinColumns = @JoinColumn(name = "ExtensionId"),
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
