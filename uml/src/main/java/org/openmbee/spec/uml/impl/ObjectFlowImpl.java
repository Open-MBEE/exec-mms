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
import org.openmbee.spec.uml.Activity;
import org.openmbee.spec.uml.ActivityEdge;
import org.openmbee.spec.uml.ActivityGroup;
import org.openmbee.spec.uml.ActivityNode;
import org.openmbee.spec.uml.ActivityPartition;
import org.openmbee.spec.uml.Behavior;
import org.openmbee.spec.uml.Classifier;
import org.openmbee.spec.uml.Comment;
import org.openmbee.spec.uml.Dependency;
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.InterruptibleActivityRegion;
import org.openmbee.spec.uml.Namespace;
import org.openmbee.spec.uml.ObjectFlow;
import org.openmbee.spec.uml.RedefinableElement;
import org.openmbee.spec.uml.StringExpression;
import org.openmbee.spec.uml.StructuredActivityNode;
import org.openmbee.spec.uml.ValueSpecification;
import org.openmbee.spec.uml.VisibilityKind;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "ObjectFlow")
@Table(appliesTo = "ObjectFlow", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "ObjectFlow")
@JsonTypeName(value = "ObjectFlow")
public class ObjectFlowImpl extends MofObjectImpl implements ObjectFlow {

    private Collection<RedefinableElement> redefinedElement;
    private Element owner;
    private Collection<ActivityGroup> inGroup;
    private VisibilityKind visibility;
    private ValueSpecification guard;
    private Collection<ActivityEdge> redefinedEdge;
    private String name;
    private Behavior selection;
    private Namespace namespace;
    private Collection<Dependency> clientDependency;
    private StringExpression nameExpression;
    private Activity activity;
    private Boolean isLeaf;
    private ActivityNode source;
    private Collection<Element> ownedElement;
    private ActivityNode target;
    private String qualifiedName;
    private Boolean isMultireceive;
    private InterruptibleActivityRegion interrupts;
    private Collection<ActivityPartition> inPartition;
    private StructuredActivityNode inStructuredNode;
    private Collection<Classifier> redefinitionContext;
    private ValueSpecification weight;
    private Behavior transformation;
    private Boolean isMulticast;
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<ActivityGroup> getInGroup() {
        if (inGroup == null) {
            inGroup = new ArrayList<>();
        }
        return inGroup;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ActivityGroupImpl.class)
    public void setInGroup(Collection<ActivityGroup> inGroup) {
        this.inGroup = inGroup;
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
    @Any(metaDef = "ValueSpecificationMetaDef", metaColumn = @Column(name = "guardType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "guardId", table = "ObjectFlow")
    public ValueSpecification getGuard() {
        return guard;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ValueSpecificationImpl.class)
    public void setGuard(ValueSpecification guard) {
        this.guard = guard;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ActivityEdgeMetaDef", metaColumn = @Column(name = "redefinedEdgeType"), fetch = FetchType.LAZY)
    @JoinTable(name = "ObjectFlow_redefinedEdge",
        joinColumns = @JoinColumn(name = "ObjectFlowId"),
        inverseJoinColumns = @JoinColumn(name = "redefinedEdgeId"))
    public Collection<ActivityEdge> getRedefinedEdge() {
        if (redefinedEdge == null) {
            redefinedEdge = new ArrayList<>();
        }
        return redefinedEdge;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ActivityEdgeImpl.class)
    public void setRedefinedEdge(Collection<ActivityEdge> redefinedEdge) {
        this.redefinedEdge = redefinedEdge;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Lob
    @org.hibernate.annotations.Type(type = "org.hibernate.type.TextType")
    @Column(name = "name", table = "ObjectFlow")
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
    @Any(metaDef = "BehaviorMetaDef", metaColumn = @Column(name = "selectionType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "selectionId", table = "ObjectFlow")
    public Behavior getSelection() {
        return selection;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = BehaviorImpl.class)
    public void setSelection(Behavior selection) {
        this.selection = selection;
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
    @JoinColumn(name = "nameExpressionId", table = "ObjectFlow")
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
    @Any(metaDef = "ActivityMetaDef", metaColumn = @Column(name = "activityType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "activityId", table = "ObjectFlow")
    public Activity getActivity() {
        return activity;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ActivityImpl.class)
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isLeaf", table = "ObjectFlow")
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
    @Any(metaDef = "ActivityNodeMetaDef", metaColumn = @Column(name = "sourceType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "sourceId", table = "ObjectFlow")
    public ActivityNode getSource() {
        return source;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ActivityNodeImpl.class)
    public void setSource(ActivityNode source) {
        this.source = source;
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
    @Any(metaDef = "ActivityNodeMetaDef", metaColumn = @Column(name = "targetType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "targetId", table = "ObjectFlow")
    public ActivityNode getTarget() {
        return target;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ActivityNodeImpl.class)
    public void setTarget(ActivityNode target) {
        this.target = target;
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
    @Column(name = "isMultireceive", table = "ObjectFlow")
    public Boolean isMultireceive() {
        return isMultireceive;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setMultireceive(Boolean isMultireceive) {
        this.isMultireceive = isMultireceive;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "InterruptibleActivityRegionMetaDef", metaColumn = @Column(name = "interruptsType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "interruptsId", table = "ObjectFlow")
    public InterruptibleActivityRegion getInterrupts() {
        return interrupts;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = InterruptibleActivityRegionImpl.class)
    public void setInterrupts(InterruptibleActivityRegion interrupts) {
        this.interrupts = interrupts;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ActivityPartitionMetaDef", metaColumn = @Column(name = "inPartitionType"), fetch = FetchType.LAZY)
    @JoinTable(name = "ObjectFlow_inPartition",
        joinColumns = @JoinColumn(name = "ObjectFlowId"),
        inverseJoinColumns = @JoinColumn(name = "inPartitionId"))
    public Collection<ActivityPartition> getInPartition() {
        if (inPartition == null) {
            inPartition = new ArrayList<>();
        }
        return inPartition;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ActivityPartitionImpl.class)
    public void setInPartition(Collection<ActivityPartition> inPartition) {
        this.inPartition = inPartition;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "StructuredActivityNodeMetaDef", metaColumn = @Column(name = "inStructuredNodeType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "inStructuredNodeId", table = "ObjectFlow")
    public StructuredActivityNode getInStructuredNode() {
        return inStructuredNode;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = StructuredActivityNodeImpl.class)
    public void setInStructuredNode(StructuredActivityNode inStructuredNode) {
        this.inStructuredNode = inStructuredNode;
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
    @Any(metaDef = "ValueSpecificationMetaDef", metaColumn = @Column(name = "weightType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "weightId", table = "ObjectFlow")
    public ValueSpecification getWeight() {
        return weight;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ValueSpecificationImpl.class)
    public void setWeight(ValueSpecification weight) {
        this.weight = weight;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "BehaviorMetaDef", metaColumn = @Column(name = "transformationType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "transformationId", table = "ObjectFlow")
    public Behavior getTransformation() {
        return transformation;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = BehaviorImpl.class)
    public void setTransformation(Behavior transformation) {
        this.transformation = transformation;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isMulticast", table = "ObjectFlow")
    public Boolean isMulticast() {
        return isMulticast;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setMulticast(Boolean isMulticast) {
        this.isMulticast = isMulticast;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "ObjectFlow_ownedComment",
        joinColumns = @JoinColumn(name = "ObjectFlowId"),
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
