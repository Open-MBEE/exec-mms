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
import org.openmbee.spec.uml.Comment;
import org.openmbee.spec.uml.Dependency;
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.GeneralOrdering;
import org.openmbee.spec.uml.Interaction;
import org.openmbee.spec.uml.InteractionOperand;
import org.openmbee.spec.uml.Lifeline;
import org.openmbee.spec.uml.Namespace;
import org.openmbee.spec.uml.OccurrenceSpecification;
import org.openmbee.spec.uml.StringExpression;
import org.openmbee.spec.uml.VisibilityKind;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "OccurrenceSpecification")
@Table(appliesTo = "OccurrenceSpecification", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "OccurrenceSpecification")
@JsonTypeName(value = "OccurrenceSpecification")
public class OccurrenceSpecificationImpl extends MofObjectImpl implements OccurrenceSpecification {

    private Element owner;
    private VisibilityKind visibility;
    private Collection<GeneralOrdering> generalOrdering;
    private Collection<Element> ownedElement;
    private Interaction enclosingInteraction;
    private InteractionOperand enclosingOperand;
    private Collection<Lifeline> covered;
    private String qualifiedName;
    private String name;
    private Collection<GeneralOrdering> toAfter;
    private Namespace namespace;
    private Collection<Dependency> clientDependency;
    private StringExpression nameExpression;
    private Collection<GeneralOrdering> toBefore;
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
    @ManyToAny(metaDef = "GeneralOrderingMetaDef", metaColumn = @Column(name = "generalOrderingType"), fetch = FetchType.LAZY)
    @JoinTable(name = "OccurrenceSpecification_generalOrdering",
        joinColumns = @JoinColumn(name = "OccurrenceSpecificationId"),
        inverseJoinColumns = @JoinColumn(name = "generalOrderingId"))
    public Collection<GeneralOrdering> getGeneralOrdering() {
        if (generalOrdering == null) {
            generalOrdering = new ArrayList<>();
        }
        return generalOrdering;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = GeneralOrderingImpl.class)
    public void setGeneralOrdering(Collection<GeneralOrdering> generalOrdering) {
        this.generalOrdering = generalOrdering;
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
    @Any(metaDef = "InteractionMetaDef", metaColumn = @Column(name = "enclosingInteractionType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "enclosingInteractionId", table = "OccurrenceSpecification")
    public Interaction getEnclosingInteraction() {
        return enclosingInteraction;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = InteractionImpl.class)
    public void setEnclosingInteraction(Interaction enclosingInteraction) {
        this.enclosingInteraction = enclosingInteraction;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "InteractionOperandMetaDef", metaColumn = @Column(name = "enclosingOperandType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "enclosingOperandId", table = "OccurrenceSpecification")
    public InteractionOperand getEnclosingOperand() {
        return enclosingOperand;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = InteractionOperandImpl.class)
    public void setEnclosingOperand(InteractionOperand enclosingOperand) {
        this.enclosingOperand = enclosingOperand;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "LifelineMetaDef", metaColumn = @Column(name = "coveredType"), fetch = FetchType.LAZY)
    @JoinTable(name = "OccurrenceSpecification_covered",
        joinColumns = @JoinColumn(name = "OccurrenceSpecificationId"),
        inverseJoinColumns = @JoinColumn(name = "coveredId"))
    public Collection<Lifeline> getCovered() {
        if (covered == null) {
            covered = new ArrayList<>();
        }
        return covered;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = LifelineImpl.class)
    public void setCovered(Collection<Lifeline> covered) {
        this.covered = covered;
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
    @Lob
    @org.hibernate.annotations.Type(type = "org.hibernate.type.TextType")
    @Column(name = "name", table = "OccurrenceSpecification")
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
    @ManyToAny(metaDef = "GeneralOrderingMetaDef", metaColumn = @Column(name = "toAfterType"), fetch = FetchType.LAZY)
    @JoinTable(name = "OccurrenceSpecification_toAfter",
        joinColumns = @JoinColumn(name = "OccurrenceSpecificationId"),
        inverseJoinColumns = @JoinColumn(name = "toAfterId"))
    public Collection<GeneralOrdering> getToAfter() {
        if (toAfter == null) {
            toAfter = new ArrayList<>();
        }
        return toAfter;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = GeneralOrderingImpl.class)
    public void setToAfter(Collection<GeneralOrdering> toAfter) {
        this.toAfter = toAfter;
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
    @JoinColumn(name = "nameExpressionId", table = "OccurrenceSpecification")
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
    @ManyToAny(metaDef = "GeneralOrderingMetaDef", metaColumn = @Column(name = "toBeforeType"), fetch = FetchType.LAZY)
    @JoinTable(name = "OccurrenceSpecification_toBefore",
        joinColumns = @JoinColumn(name = "OccurrenceSpecificationId"),
        inverseJoinColumns = @JoinColumn(name = "toBeforeId"))
    public Collection<GeneralOrdering> getToBefore() {
        if (toBefore == null) {
            toBefore = new ArrayList<>();
        }
        return toBefore;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = GeneralOrderingImpl.class)
    public void setToBefore(Collection<GeneralOrdering> toBefore) {
        this.toBefore = toBefore;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "OccurrenceSpecification_ownedComment",
        joinColumns = @JoinColumn(name = "OccurrenceSpecificationId"),
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
