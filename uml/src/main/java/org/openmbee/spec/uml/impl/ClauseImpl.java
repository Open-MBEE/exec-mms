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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.SecondaryTable;
import javax.persistence.Transient;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.Table;
import org.openmbee.spec.uml.Clause;
import org.openmbee.spec.uml.Comment;
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.ExecutableNode;
import org.openmbee.spec.uml.OutputPin;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "Clause")
@Table(appliesTo = "Clause", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "Clause")
@JsonTypeName(value = "Clause")
public class ClauseImpl extends MofObjectImpl implements Clause {

    private List<OutputPin> bodyOutput;
    private OutputPin decider;
    private Element owner;
    private Collection<Clause> successorClause;
    private Collection<Element> ownedElement;
    private Collection<Clause> predecessorClause;
    private Collection<ExecutableNode> test;
    private Collection<ExecutableNode> body;
    private Collection<Comment> ownedComment;

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "OutputPinMetaDef", metaColumn = @Column(name = "bodyOutputType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Clause_bodyOutput",
        joinColumns = @JoinColumn(name = "ClauseId"),
        inverseJoinColumns = @JoinColumn(name = "bodyOutputId"))
    public List<OutputPin> getBodyOutput() {
        if (bodyOutput == null) {
            bodyOutput = new ArrayList<>();
        }
        return bodyOutput;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = OutputPinImpl.class)
    public void setBodyOutput(List<OutputPin> bodyOutput) {
        this.bodyOutput = bodyOutput;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "OutputPinMetaDef", metaColumn = @Column(name = "deciderType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "deciderId", table = "Clause")
    public OutputPin getDecider() {
        return decider;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = OutputPinImpl.class)
    public void setDecider(OutputPin decider) {
        this.decider = decider;
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
    @ManyToAny(metaDef = "ClauseMetaDef", metaColumn = @Column(name = "successorClauseType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Clause_successorClause",
        joinColumns = @JoinColumn(name = "ClauseId"),
        inverseJoinColumns = @JoinColumn(name = "successorClauseId"))
    public Collection<Clause> getSuccessorClause() {
        if (successorClause == null) {
            successorClause = new ArrayList<>();
        }
        return successorClause;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ClauseImpl.class)
    public void setSuccessorClause(Collection<Clause> successorClause) {
        this.successorClause = successorClause;
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
    @ManyToAny(metaDef = "ClauseMetaDef", metaColumn = @Column(name = "predecessorClauseType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Clause_predecessorClause",
        joinColumns = @JoinColumn(name = "ClauseId"),
        inverseJoinColumns = @JoinColumn(name = "predecessorClauseId"))
    public Collection<Clause> getPredecessorClause() {
        if (predecessorClause == null) {
            predecessorClause = new ArrayList<>();
        }
        return predecessorClause;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ClauseImpl.class)
    public void setPredecessorClause(Collection<Clause> predecessorClause) {
        this.predecessorClause = predecessorClause;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ExecutableNodeMetaDef", metaColumn = @Column(name = "testType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Clause_test",
        joinColumns = @JoinColumn(name = "ClauseId"),
        inverseJoinColumns = @JoinColumn(name = "testId"))
    public Collection<ExecutableNode> getTest() {
        if (test == null) {
            test = new ArrayList<>();
        }
        return test;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ExecutableNodeImpl.class)
    public void setTest(Collection<ExecutableNode> test) {
        this.test = test;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ExecutableNodeMetaDef", metaColumn = @Column(name = "bodyType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Clause_body",
        joinColumns = @JoinColumn(name = "ClauseId"),
        inverseJoinColumns = @JoinColumn(name = "bodyId"))
    public Collection<ExecutableNode> getBody() {
        if (body == null) {
            body = new ArrayList<>();
        }
        return body;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ExecutableNodeImpl.class)
    public void setBody(Collection<ExecutableNode> body) {
        this.body = body;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Clause_ownedComment",
        joinColumns = @JoinColumn(name = "ClauseId"),
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
