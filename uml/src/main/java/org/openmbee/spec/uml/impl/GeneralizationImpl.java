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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.SecondaryTable;
import javax.persistence.Transient;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.Table;
import org.openmbee.spec.uml.Classifier;
import org.openmbee.spec.uml.Comment;
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.Generalization;
import org.openmbee.spec.uml.GeneralizationSet;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "Generalization")
@Table(appliesTo = "Generalization", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "Generalization")
@JsonTypeName(value = "Generalization")
public class GeneralizationImpl extends MofObjectImpl implements Generalization {

    private Element owner;
    private Collection<Element> source;
    private Collection<Element> target;
    private Collection<Element> relatedElement;
    private Collection<Element> ownedElement;
    private Collection<GeneralizationSet> generalizationSet;
    private Collection<Comment> ownedComment;
    private Classifier general;
    private Boolean isSubstitutable;
    private Classifier specific;

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
    public Collection<Element> getSource() {
        if (source == null) {
            source = new ArrayList<>();
        }
        return source;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ElementImpl.class)
    public void setSource(Collection<Element> source) {
        this.source = source;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<Element> getTarget() {
        if (target == null) {
            target = new ArrayList<>();
        }
        return target;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ElementImpl.class)
    public void setTarget(Collection<Element> target) {
        this.target = target;
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
    @ManyToAny(metaDef = "GeneralizationSetMetaDef", metaColumn = @Column(name = "generalizationSetType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Generalization_generalizationSet",
        joinColumns = @JoinColumn(name = "GeneralizationId"),
        inverseJoinColumns = @JoinColumn(name = "generalizationSetId"))
    public Collection<GeneralizationSet> getGeneralizationSet() {
        if (generalizationSet == null) {
            generalizationSet = new ArrayList<>();
        }
        return generalizationSet;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = GeneralizationSetImpl.class)
    public void setGeneralizationSet(Collection<GeneralizationSet> generalizationSet) {
        this.generalizationSet = generalizationSet;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Generalization_ownedComment",
        joinColumns = @JoinColumn(name = "GeneralizationId"),
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
    @Any(metaDef = "ClassifierMetaDef", metaColumn = @Column(name = "generalType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "generalId", table = "Generalization")
    public Classifier getGeneral() {
        return general;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ClassifierImpl.class)
    public void setGeneral(Classifier general) {
        this.general = general;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isSubstitutable", table = "Generalization")
    public Boolean isSubstitutable() {
        return isSubstitutable;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setSubstitutable(Boolean isSubstitutable) {
        this.isSubstitutable = isSubstitutable;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ClassifierMetaDef", metaColumn = @Column(name = "specificType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "specificId", table = "Generalization")
    public Classifier getSpecific() {
        return specific;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ClassifierImpl.class)
    public void setSpecific(Classifier specific) {
        this.specific = specific;
    }

}
