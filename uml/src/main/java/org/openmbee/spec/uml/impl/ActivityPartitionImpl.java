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
import org.openmbee.spec.uml.Comment;
import org.openmbee.spec.uml.Dependency;
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.Namespace;
import org.openmbee.spec.uml.StringExpression;
import org.openmbee.spec.uml.VisibilityKind;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "ActivityPartition")
@Table(appliesTo = "ActivityPartition", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "ActivityPartition")
@JsonTypeName(value = "ActivityPartition")
public class ActivityPartitionImpl extends MofObjectImpl implements ActivityPartition {

    private Element owner;
    private Collection<ActivityNode> node;
    private VisibilityKind visibility;
    private Collection<Element> ownedElement;
    private Activity inActivity;
    private Collection<ActivityPartition> subpartition;
    private Collection<ActivityEdge> containedEdge;
    private Collection<ActivityEdge> edge;
    private Collection<ActivityGroup> subgroup;
    private ActivityPartition superPartition;
    private String qualifiedName;
    private Boolean isExternal;
    private String name;
    private ActivityGroup superGroup;
    private Namespace namespace;
    private Boolean isDimension;
    private Collection<Dependency> clientDependency;
    private StringExpression nameExpression;
    private Collection<ActivityNode> containedNode;
    private Element represents;
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
    @ManyToAny(metaDef = "ActivityNodeMetaDef", metaColumn = @Column(name = "nodeType"), fetch = FetchType.LAZY)
    @JoinTable(name = "ActivityPartition_node",
        joinColumns = @JoinColumn(name = "ActivityPartitionId"),
        inverseJoinColumns = @JoinColumn(name = "nodeId"))
    public Collection<ActivityNode> getNode() {
        if (node == null) {
            node = new ArrayList<>();
        }
        return node;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ActivityNodeImpl.class)
    public void setNode(Collection<ActivityNode> node) {
        this.node = node;
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
    @Any(metaDef = "ActivityMetaDef", metaColumn = @Column(name = "inActivityType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "inActivityId", table = "ActivityPartition")
    public Activity getInActivity() {
        return inActivity;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ActivityImpl.class)
    public void setInActivity(Activity inActivity) {
        this.inActivity = inActivity;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ActivityPartitionMetaDef", metaColumn = @Column(name = "subpartitionType"), fetch = FetchType.LAZY)
    @JoinTable(name = "ActivityPartition_subpartition",
        joinColumns = @JoinColumn(name = "ActivityPartitionId"),
        inverseJoinColumns = @JoinColumn(name = "subpartitionId"))
    public Collection<ActivityPartition> getSubpartition() {
        if (subpartition == null) {
            subpartition = new ArrayList<>();
        }
        return subpartition;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ActivityPartitionImpl.class)
    public void setSubpartition(Collection<ActivityPartition> subpartition) {
        this.subpartition = subpartition;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<ActivityEdge> getContainedEdge() {
        if (containedEdge == null) {
            containedEdge = new ArrayList<>();
        }
        return containedEdge;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ActivityEdgeImpl.class)
    public void setContainedEdge(Collection<ActivityEdge> containedEdge) {
        this.containedEdge = containedEdge;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ActivityEdgeMetaDef", metaColumn = @Column(name = "edgeType"), fetch = FetchType.LAZY)
    @JoinTable(name = "ActivityPartition_edge",
        joinColumns = @JoinColumn(name = "ActivityPartitionId"),
        inverseJoinColumns = @JoinColumn(name = "edgeId"))
    public Collection<ActivityEdge> getEdge() {
        if (edge == null) {
            edge = new ArrayList<>();
        }
        return edge;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ActivityEdgeImpl.class)
    public void setEdge(Collection<ActivityEdge> edge) {
        this.edge = edge;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<ActivityGroup> getSubgroup() {
        if (subgroup == null) {
            subgroup = new ArrayList<>();
        }
        return subgroup;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ActivityGroupImpl.class)
    public void setSubgroup(Collection<ActivityGroup> subgroup) {
        this.subgroup = subgroup;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ActivityPartitionMetaDef", metaColumn = @Column(name = "superPartitionType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "superPartitionId", table = "ActivityPartition")
    public ActivityPartition getSuperPartition() {
        return superPartition;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ActivityPartitionImpl.class)
    public void setSuperPartition(ActivityPartition superPartition) {
        this.superPartition = superPartition;
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
    @Column(name = "isExternal", table = "ActivityPartition")
    public Boolean isExternal() {
        return isExternal;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setExternal(Boolean isExternal) {
        this.isExternal = isExternal;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Lob
    @org.hibernate.annotations.Type(type = "org.hibernate.type.TextType")
    @Column(name = "name", table = "ActivityPartition")
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
    public ActivityGroup getSuperGroup() {
        return superGroup;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ActivityGroupImpl.class)
    public void setSuperGroup(ActivityGroup superGroup) {
        this.superGroup = superGroup;
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
    @Column(name = "isDimension", table = "ActivityPartition")
    public Boolean isDimension() {
        return isDimension;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setDimension(Boolean isDimension) {
        this.isDimension = isDimension;
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
    @JoinColumn(name = "nameExpressionId", table = "ActivityPartition")
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
    public Collection<ActivityNode> getContainedNode() {
        if (containedNode == null) {
            containedNode = new ArrayList<>();
        }
        return containedNode;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ActivityNodeImpl.class)
    public void setContainedNode(Collection<ActivityNode> containedNode) {
        this.containedNode = containedNode;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ElementMetaDef", metaColumn = @Column(name = "representsType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "representsId", table = "ActivityPartition")
    public Element getRepresents() {
        return represents;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ElementImpl.class)
    public void setRepresents(Element represents) {
        this.represents = represents;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "ActivityPartition_ownedComment",
        joinColumns = @JoinColumn(name = "ActivityPartitionId"),
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
