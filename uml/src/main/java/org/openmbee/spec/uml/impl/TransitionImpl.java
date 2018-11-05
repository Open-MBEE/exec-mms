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
import org.openmbee.spec.uml.Behavior;
import org.openmbee.spec.uml.Classifier;
import org.openmbee.spec.uml.Comment;
import org.openmbee.spec.uml.Constraint;
import org.openmbee.spec.uml.Dependency;
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.ElementImport;
import org.openmbee.spec.uml.NamedElement;
import org.openmbee.spec.uml.Namespace;
import org.openmbee.spec.uml.PackageImport;
import org.openmbee.spec.uml.PackageableElement;
import org.openmbee.spec.uml.RedefinableElement;
import org.openmbee.spec.uml.Region;
import org.openmbee.spec.uml.StringExpression;
import org.openmbee.spec.uml.Transition;
import org.openmbee.spec.uml.TransitionKind;
import org.openmbee.spec.uml.Trigger;
import org.openmbee.spec.uml.Vertex;
import org.openmbee.spec.uml.VisibilityKind;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "Transition")
@Table(appliesTo = "Transition", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "Transition")
@JsonTypeName(value = "Transition")
public class TransitionImpl extends MofObjectImpl implements Transition {

    private Collection<RedefinableElement> redefinedElement;
    private Element owner;
    private VisibilityKind visibility;
    private Vertex source;
    private Vertex target;
    private Collection<ElementImport> elementImport;
    private String name;
    private TransitionKind kind;
    private Namespace namespace;
    private Collection<Dependency> clientDependency;
    private StringExpression nameExpression;
    private Collection<PackageableElement> importedMember;
    private Collection<PackageImport> packageImport;
    private Collection<Constraint> ownedRule;
    private Transition redefinedTransition;
    private Boolean isLeaf;
    private Constraint guard;
    private Collection<Element> ownedElement;
    private Behavior effect;
    private Collection<Trigger> trigger;
    private String qualifiedName;
    private Region container;
    private Collection<NamedElement> ownedMember;
    private Collection<Classifier> redefinitionContext;
    private Collection<NamedElement> member;
    private Collection<Comment> ownedComment;

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
    @Any(metaDef = "VertexMetaDef", metaColumn = @Column(name = "sourceType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "sourceId", table = "Transition")
    public Vertex getSource() {
        return source;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = VertexImpl.class)
    public void setSource(Vertex source) {
        this.source = source;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "VertexMetaDef", metaColumn = @Column(name = "targetType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "targetId", table = "Transition")
    public Vertex getTarget() {
        return target;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = VertexImpl.class)
    public void setTarget(Vertex target) {
        this.target = target;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ElementImportMetaDef", metaColumn = @Column(name = "elementImportType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Transition_elementImport",
        joinColumns = @JoinColumn(name = "TransitionId"),
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
    @Column(name = "name", table = "Transition")
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
    @Enumerated(EnumType.STRING)
    public TransitionKind getKind() {
        return kind;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setKind(TransitionKind kind) {
        this.kind = kind;
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
    @JoinColumn(name = "nameExpressionId", table = "Transition")
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
    @ManyToAny(metaDef = "PackageImportMetaDef", metaColumn = @Column(name = "packageImportType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Transition_packageImport",
        joinColumns = @JoinColumn(name = "TransitionId"),
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
    @ManyToAny(metaDef = "ConstraintMetaDef", metaColumn = @Column(name = "ownedRuleType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Transition_ownedRule",
        joinColumns = @JoinColumn(name = "TransitionId"),
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
    @Any(metaDef = "TransitionMetaDef", metaColumn = @Column(name = "redefinedTransitionType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "redefinedTransitionId", table = "Transition")
    public Transition getRedefinedTransition() {
        return redefinedTransition;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = TransitionImpl.class)
    public void setRedefinedTransition(Transition redefinedTransition) {
        this.redefinedTransition = redefinedTransition;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isLeaf", table = "Transition")
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
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ConstraintMetaDef", metaColumn = @Column(name = "guardType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "guardId", table = "Transition")
    public Constraint getGuard() {
        return guard;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ConstraintImpl.class)
    public void setGuard(Constraint guard) {
        this.guard = guard;
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
    @Any(metaDef = "BehaviorMetaDef", metaColumn = @Column(name = "effectType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "effectId", table = "Transition")
    public Behavior getEffect() {
        return effect;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = BehaviorImpl.class)
    public void setEffect(Behavior effect) {
        this.effect = effect;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "TriggerMetaDef", metaColumn = @Column(name = "triggerType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Transition_trigger",
        joinColumns = @JoinColumn(name = "TransitionId"),
        inverseJoinColumns = @JoinColumn(name = "triggerId"))
    public Collection<Trigger> getTrigger() {
        if (trigger == null) {
            trigger = new ArrayList<>();
        }
        return trigger;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = TriggerImpl.class)
    public void setTrigger(Collection<Trigger> trigger) {
        this.trigger = trigger;
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
    @Any(metaDef = "RegionMetaDef", metaColumn = @Column(name = "containerType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "containerId", table = "Transition")
    public Region getContainer() {
        return container;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = RegionImpl.class)
    public void setContainer(Region container) {
        this.container = container;
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
    @JoinTable(name = "Transition_ownedComment",
        joinColumns = @JoinColumn(name = "TransitionId"),
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
