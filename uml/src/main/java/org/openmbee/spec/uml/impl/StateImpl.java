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
import org.openmbee.spec.uml.ConnectionPointReference;
import org.openmbee.spec.uml.Constraint;
import org.openmbee.spec.uml.Dependency;
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.ElementImport;
import org.openmbee.spec.uml.NamedElement;
import org.openmbee.spec.uml.Namespace;
import org.openmbee.spec.uml.PackageImport;
import org.openmbee.spec.uml.PackageableElement;
import org.openmbee.spec.uml.Pseudostate;
import org.openmbee.spec.uml.RedefinableElement;
import org.openmbee.spec.uml.Region;
import org.openmbee.spec.uml.State;
import org.openmbee.spec.uml.StateMachine;
import org.openmbee.spec.uml.StringExpression;
import org.openmbee.spec.uml.Transition;
import org.openmbee.spec.uml.Trigger;
import org.openmbee.spec.uml.Vertex;
import org.openmbee.spec.uml.VisibilityKind;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "State")
@Table(appliesTo = "State", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "State")
@JsonTypeName(value = "State")
public class StateImpl extends MofObjectImpl implements State {

    private Collection<RedefinableElement> redefinedElement;
    private Element owner;
    private VisibilityKind visibility;
    private Behavior entry;
    private Collection<Transition> incoming;
    private Constraint stateInvariant;
    private Boolean isSubmachineState;
    private Collection<ElementImport> elementImport;
    private String name;
    private Namespace namespace;
    private Collection<Dependency> clientDependency;
    private StringExpression nameExpression;
    private Collection<PackageableElement> importedMember;
    private Boolean isOrthogonal;
    private Collection<PackageImport> packageImport;
    private Collection<Constraint> ownedRule;
    private Collection<ConnectionPointReference> connection;
    private Region container;
    private Boolean isLeaf;
    private Collection<Element> ownedElement;
    private Collection<Pseudostate> connectionPoint;
    private Collection<Region> region;
    private Behavior exit;
    private String qualifiedName;
    private Vertex redefinedVertex;
    private Collection<Trigger> deferrableTrigger;
    private Collection<NamedElement> ownedMember;
    private Collection<Classifier> redefinitionContext;
    private Boolean isComposite;
    private Collection<Transition> outgoing;
    private Collection<NamedElement> member;
    private Collection<Comment> ownedComment;
    private StateMachine submachine;
    private Behavior doActivity;
    private Boolean isSimple;

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
    @Any(metaDef = "BehaviorMetaDef", metaColumn = @Column(name = "entryType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "entryId", table = "State")
    public Behavior getEntry() {
        return entry;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = BehaviorImpl.class)
    public void setEntry(Behavior entry) {
        this.entry = entry;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<Transition> getIncoming() {
        if (incoming == null) {
            incoming = new ArrayList<>();
        }
        return incoming;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = TransitionImpl.class)
    public void setIncoming(Collection<Transition> incoming) {
        this.incoming = incoming;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ConstraintMetaDef", metaColumn = @Column(name = "stateInvariantType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "stateInvariantId", table = "State")
    public Constraint getStateInvariant() {
        return stateInvariant;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ConstraintImpl.class)
    public void setStateInvariant(Constraint stateInvariant) {
        this.stateInvariant = stateInvariant;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Transient
    public Boolean isSubmachineState() {
        return isSubmachineState;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setSubmachineState(Boolean isSubmachineState) {
        this.isSubmachineState = isSubmachineState;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ElementImportMetaDef", metaColumn = @Column(name = "elementImportType"), fetch = FetchType.LAZY)
    @JoinTable(name = "State_elementImport",
        joinColumns = @JoinColumn(name = "StateId"),
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
    @Column(name = "name", table = "State")
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
    @JoinColumn(name = "nameExpressionId", table = "State")
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
    @Transient
    public Boolean isOrthogonal() {
        return isOrthogonal;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setOrthogonal(Boolean isOrthogonal) {
        this.isOrthogonal = isOrthogonal;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "PackageImportMetaDef", metaColumn = @Column(name = "packageImportType"), fetch = FetchType.LAZY)
    @JoinTable(name = "State_packageImport",
        joinColumns = @JoinColumn(name = "StateId"),
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
    @JoinTable(name = "State_ownedRule",
        joinColumns = @JoinColumn(name = "StateId"),
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
    @ManyToAny(metaDef = "ConnectionPointReferenceMetaDef", metaColumn = @Column(name = "connectionType"), fetch = FetchType.LAZY)
    @JoinTable(name = "State_connection",
        joinColumns = @JoinColumn(name = "StateId"),
        inverseJoinColumns = @JoinColumn(name = "connectionId"))
    public Collection<ConnectionPointReference> getConnection() {
        if (connection == null) {
            connection = new ArrayList<>();
        }
        return connection;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ConnectionPointReferenceImpl.class)
    public void setConnection(Collection<ConnectionPointReference> connection) {
        this.connection = connection;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "RegionMetaDef", metaColumn = @Column(name = "containerType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "containerId", table = "State")
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
    @Column(name = "isLeaf", table = "State")
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
    @ManyToAny(metaDef = "PseudostateMetaDef", metaColumn = @Column(name = "connectionPointType"), fetch = FetchType.LAZY)
    @JoinTable(name = "State_connectionPoint",
        joinColumns = @JoinColumn(name = "StateId"),
        inverseJoinColumns = @JoinColumn(name = "connectionPointId"))
    public Collection<Pseudostate> getConnectionPoint() {
        if (connectionPoint == null) {
            connectionPoint = new ArrayList<>();
        }
        return connectionPoint;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = PseudostateImpl.class)
    public void setConnectionPoint(Collection<Pseudostate> connectionPoint) {
        this.connectionPoint = connectionPoint;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "RegionMetaDef", metaColumn = @Column(name = "regionType"), fetch = FetchType.LAZY)
    @JoinTable(name = "State_region",
        joinColumns = @JoinColumn(name = "StateId"),
        inverseJoinColumns = @JoinColumn(name = "regionId"))
    public Collection<Region> getRegion() {
        if (region == null) {
            region = new ArrayList<>();
        }
        return region;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = RegionImpl.class)
    public void setRegion(Collection<Region> region) {
        this.region = region;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "BehaviorMetaDef", metaColumn = @Column(name = "exitType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "exitId", table = "State")
    public Behavior getExit() {
        return exit;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = BehaviorImpl.class)
    public void setExit(Behavior exit) {
        this.exit = exit;
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
    @Any(metaDef = "VertexMetaDef", metaColumn = @Column(name = "redefinedVertexType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "redefinedVertexId", table = "State")
    public Vertex getRedefinedVertex() {
        return redefinedVertex;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = VertexImpl.class)
    public void setRedefinedVertex(Vertex redefinedVertex) {
        this.redefinedVertex = redefinedVertex;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "TriggerMetaDef", metaColumn = @Column(name = "deferrableTriggerType"), fetch = FetchType.LAZY)
    @JoinTable(name = "State_deferrableTrigger",
        joinColumns = @JoinColumn(name = "StateId"),
        inverseJoinColumns = @JoinColumn(name = "deferrableTriggerId"))
    public Collection<Trigger> getDeferrableTrigger() {
        if (deferrableTrigger == null) {
            deferrableTrigger = new ArrayList<>();
        }
        return deferrableTrigger;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = TriggerImpl.class)
    public void setDeferrableTrigger(Collection<Trigger> deferrableTrigger) {
        this.deferrableTrigger = deferrableTrigger;
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<Transition> getOutgoing() {
        if (outgoing == null) {
            outgoing = new ArrayList<>();
        }
        return outgoing;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = TransitionImpl.class)
    public void setOutgoing(Collection<Transition> outgoing) {
        this.outgoing = outgoing;
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
    @JoinTable(name = "State_ownedComment",
        joinColumns = @JoinColumn(name = "StateId"),
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
    @Any(metaDef = "StateMachineMetaDef", metaColumn = @Column(name = "submachineType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "submachineId", table = "State")
    public StateMachine getSubmachine() {
        return submachine;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = StateMachineImpl.class)
    public void setSubmachine(StateMachine submachine) {
        this.submachine = submachine;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "BehaviorMetaDef", metaColumn = @Column(name = "doActivityType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "doActivityId", table = "State")
    public Behavior getDoActivity() {
        return doActivity;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = BehaviorImpl.class)
    public void setDoActivity(Behavior doActivity) {
        this.doActivity = doActivity;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Transient
    public Boolean isSimple() {
        return isSimple;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setSimple(Boolean isSimple) {
        this.isSimple = isSimple;
    }

}
