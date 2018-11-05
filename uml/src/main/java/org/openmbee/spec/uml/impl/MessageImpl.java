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
import org.openmbee.spec.uml.Comment;
import org.openmbee.spec.uml.Connector;
import org.openmbee.spec.uml.Dependency;
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.Interaction;
import org.openmbee.spec.uml.Message;
import org.openmbee.spec.uml.MessageEnd;
import org.openmbee.spec.uml.MessageKind;
import org.openmbee.spec.uml.MessageSort;
import org.openmbee.spec.uml.NamedElement;
import org.openmbee.spec.uml.Namespace;
import org.openmbee.spec.uml.StringExpression;
import org.openmbee.spec.uml.ValueSpecification;
import org.openmbee.spec.uml.VisibilityKind;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "Message")
@Table(appliesTo = "Message", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "Message")
@JsonTypeName(value = "Message")
public class MessageImpl extends MofObjectImpl implements Message {

    private Element owner;
    private MessageSort messageSort;
    private VisibilityKind visibility;
    private Collection<Element> ownedElement;
    private Interaction interaction;
    private NamedElement signature;
    private String qualifiedName;
    private MessageEnd sendEvent;
    private String name;
    private Namespace namespace;
    private MessageKind messageKind;
    private MessageEnd receiveEvent;
    private Collection<Dependency> clientDependency;
    private StringExpression nameExpression;
    private Collection<Comment> ownedComment;
    private List<ValueSpecification> argument;
    private Connector connector;

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
    public MessageSort getMessageSort() {
        return messageSort;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setMessageSort(MessageSort messageSort) {
        this.messageSort = messageSort;
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
    @Any(metaDef = "InteractionMetaDef", metaColumn = @Column(name = "interactionType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "interactionId", table = "Message")
    public Interaction getInteraction() {
        return interaction;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = InteractionImpl.class)
    public void setInteraction(Interaction interaction) {
        this.interaction = interaction;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "NamedElementMetaDef", metaColumn = @Column(name = "signatureType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "signatureId", table = "Message")
    public NamedElement getSignature() {
        return signature;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = NamedElementImpl.class)
    public void setSignature(NamedElement signature) {
        this.signature = signature;
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
    @Any(metaDef = "MessageEndMetaDef", metaColumn = @Column(name = "sendEventType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "sendEventId", table = "Message")
    public MessageEnd getSendEvent() {
        return sendEvent;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = MessageEndImpl.class)
    public void setSendEvent(MessageEnd sendEvent) {
        this.sendEvent = sendEvent;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Lob
    @org.hibernate.annotations.Type(type = "org.hibernate.type.TextType")
    @Column(name = "name", table = "Message")
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
    @Enumerated(EnumType.STRING)
    @Transient
    public MessageKind getMessageKind() {
        return messageKind;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setMessageKind(MessageKind messageKind) {
        this.messageKind = messageKind;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "MessageEndMetaDef", metaColumn = @Column(name = "receiveEventType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "receiveEventId", table = "Message")
    public MessageEnd getReceiveEvent() {
        return receiveEvent;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = MessageEndImpl.class)
    public void setReceiveEvent(MessageEnd receiveEvent) {
        this.receiveEvent = receiveEvent;
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
    @JoinColumn(name = "nameExpressionId", table = "Message")
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
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Message_ownedComment",
        joinColumns = @JoinColumn(name = "MessageId"),
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
    @ManyToAny(metaDef = "ValueSpecificationMetaDef", metaColumn = @Column(name = "argumentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Message_argument",
        joinColumns = @JoinColumn(name = "MessageId"),
        inverseJoinColumns = @JoinColumn(name = "argumentId"))
    public List<ValueSpecification> getArgument() {
        if (argument == null) {
            argument = new ArrayList<>();
        }
        return argument;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ValueSpecificationImpl.class)
    public void setArgument(List<ValueSpecification> argument) {
        this.argument = argument;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ConnectorMetaDef", metaColumn = @Column(name = "connectorType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "connectorId", table = "Message")
    public Connector getConnector() {
        return connector;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ConnectorImpl.class)
    public void setConnector(Connector connector) {
        this.connector = connector;
    }

}
