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
import org.openmbee.spec.uml.ExceptionHandler;
import org.openmbee.spec.uml.ExecutableNode;
import org.openmbee.spec.uml.ObjectNode;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "ExceptionHandler")
@Table(appliesTo = "ExceptionHandler", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "ExceptionHandler")
@JsonTypeName(value = "ExceptionHandler")
public class ExceptionHandlerImpl extends MofObjectImpl implements ExceptionHandler {

    private Element owner;
    private ObjectNode exceptionInput;
    private ExecutableNode protectedNode;
    private Collection<Classifier> exceptionType;
    private Collection<Element> ownedElement;
    private ExecutableNode handlerBody;
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
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ObjectNodeMetaDef", metaColumn = @Column(name = "exceptionInputType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "exceptionInputId", table = "ExceptionHandler")
    public ObjectNode getExceptionInput() {
        return exceptionInput;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ObjectNodeImpl.class)
    public void setExceptionInput(ObjectNode exceptionInput) {
        this.exceptionInput = exceptionInput;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ExecutableNodeMetaDef", metaColumn = @Column(name = "protectedNodeType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "protectedNodeId", table = "ExceptionHandler")
    public ExecutableNode getProtectedNode() {
        return protectedNode;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ExecutableNodeImpl.class)
    public void setProtectedNode(ExecutableNode protectedNode) {
        this.protectedNode = protectedNode;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ClassifierMetaDef", metaColumn = @Column(name = "exceptionTypeType"), fetch = FetchType.LAZY)
    @JoinTable(name = "ExceptionHandler_exceptionType",
        joinColumns = @JoinColumn(name = "ExceptionHandlerId"),
        inverseJoinColumns = @JoinColumn(name = "exceptionTypeId"))
    public Collection<Classifier> getExceptionType() {
        if (exceptionType == null) {
            exceptionType = new ArrayList<>();
        }
        return exceptionType;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ClassifierImpl.class)
    public void setExceptionType(Collection<Classifier> exceptionType) {
        this.exceptionType = exceptionType;
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
    @Any(metaDef = "ExecutableNodeMetaDef", metaColumn = @Column(name = "handlerBodyType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "handlerBodyId", table = "ExceptionHandler")
    public ExecutableNode getHandlerBody() {
        return handlerBody;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ExecutableNodeImpl.class)
    public void setHandlerBody(ExecutableNode handlerBody) {
        this.handlerBody = handlerBody;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "ExceptionHandler_ownedComment",
        joinColumns = @JoinColumn(name = "ExceptionHandlerId"),
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
