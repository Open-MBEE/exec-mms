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
import org.openmbee.spec.uml.ActivityEdge;
import org.openmbee.spec.uml.Classifier;
import org.openmbee.spec.uml.Comment;
import org.openmbee.spec.uml.Connector;
import org.openmbee.spec.uml.Dependency;
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.InformationFlow;
import org.openmbee.spec.uml.Message;
import org.openmbee.spec.uml.NamedElement;
import org.openmbee.spec.uml.Namespace;
import org.openmbee.spec.uml.Relationship;
import org.openmbee.spec.uml.StringExpression;
import org.openmbee.spec.uml.TemplateParameter;
import org.openmbee.spec.uml.VisibilityKind;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "InformationFlow")
@Table(appliesTo = "InformationFlow", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "InformationFlow")
@JsonTypeName(value = "InformationFlow")
public class InformationFlowImpl extends MofObjectImpl implements InformationFlow {

    private Collection<Relationship> realization;
    private Collection<ActivityEdge> realizingActivityEdge;
    private Element owner;
    private Collection<Message> realizingMessage;
    private Collection<Element> target;
    private Collection<Element> ownedElement;
    private TemplateParameter owningTemplateParameter;
    private Collection<Classifier> conveyed;
    private Collection<NamedElement> informationSource;
    private String qualifiedName;
    private VisibilityKind visibility;
    private String name;
    private Collection<Element> source;
    private Collection<Element> relatedElement;
    private Namespace namespace;
    private Collection<NamedElement> informationTarget;
    private TemplateParameter templateParameter;
    private Collection<Dependency> clientDependency;
    private StringExpression nameExpression;
    private Collection<Connector> realizingConnector;
    private Collection<Comment> ownedComment;

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "RelationshipMetaDef", metaColumn = @Column(name = "realizationType"), fetch = FetchType.LAZY)
    @JoinTable(name = "InformationFlow_realization",
        joinColumns = @JoinColumn(name = "InformationFlowId"),
        inverseJoinColumns = @JoinColumn(name = "realizationId"))
    public Collection<Relationship> getRealization() {
        if (realization == null) {
            realization = new ArrayList<>();
        }
        return realization;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = RelationshipImpl.class)
    public void setRealization(Collection<Relationship> realization) {
        this.realization = realization;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ActivityEdgeMetaDef", metaColumn = @Column(name = "realizingActivityEdgeType"), fetch = FetchType.LAZY)
    @JoinTable(name = "InformationFlow_realizingActivityEdge",
        joinColumns = @JoinColumn(name = "InformationFlowId"),
        inverseJoinColumns = @JoinColumn(name = "realizingActivityEdgeId"))
    public Collection<ActivityEdge> getRealizingActivityEdge() {
        if (realizingActivityEdge == null) {
            realizingActivityEdge = new ArrayList<>();
        }
        return realizingActivityEdge;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ActivityEdgeImpl.class)
    public void setRealizingActivityEdge(Collection<ActivityEdge> realizingActivityEdge) {
        this.realizingActivityEdge = realizingActivityEdge;
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
    @ManyToAny(metaDef = "MessageMetaDef", metaColumn = @Column(name = "realizingMessageType"), fetch = FetchType.LAZY)
    @JoinTable(name = "InformationFlow_realizingMessage",
        joinColumns = @JoinColumn(name = "InformationFlowId"),
        inverseJoinColumns = @JoinColumn(name = "realizingMessageId"))
    public Collection<Message> getRealizingMessage() {
        if (realizingMessage == null) {
            realizingMessage = new ArrayList<>();
        }
        return realizingMessage;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = MessageImpl.class)
    public void setRealizingMessage(Collection<Message> realizingMessage) {
        this.realizingMessage = realizingMessage;
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
    @Any(metaDef = "TemplateParameterMetaDef", metaColumn = @Column(name = "owningTemplateParameterType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "owningTemplateParameterId", table = "InformationFlow")
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
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ClassifierMetaDef", metaColumn = @Column(name = "conveyedType"), fetch = FetchType.LAZY)
    @JoinTable(name = "InformationFlow_conveyed",
        joinColumns = @JoinColumn(name = "InformationFlowId"),
        inverseJoinColumns = @JoinColumn(name = "conveyedId"))
    public Collection<Classifier> getConveyed() {
        if (conveyed == null) {
            conveyed = new ArrayList<>();
        }
        return conveyed;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ClassifierImpl.class)
    public void setConveyed(Collection<Classifier> conveyed) {
        this.conveyed = conveyed;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "NamedElementMetaDef", metaColumn = @Column(name = "informationSourceType"), fetch = FetchType.LAZY)
    @JoinTable(name = "InformationFlow_informationSource",
        joinColumns = @JoinColumn(name = "InformationFlowId"),
        inverseJoinColumns = @JoinColumn(name = "informationSourceId"))
    public Collection<NamedElement> getInformationSource() {
        if (informationSource == null) {
            informationSource = new ArrayList<>();
        }
        return informationSource;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = NamedElementImpl.class)
    public void setInformationSource(Collection<NamedElement> informationSource) {
        this.informationSource = informationSource;
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
    @Lob
    @org.hibernate.annotations.Type(type = "org.hibernate.type.TextType")
    @Column(name = "name", table = "InformationFlow")
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
    @ManyToAny(metaDef = "NamedElementMetaDef", metaColumn = @Column(name = "informationTargetType"), fetch = FetchType.LAZY)
    @JoinTable(name = "InformationFlow_informationTarget",
        joinColumns = @JoinColumn(name = "InformationFlowId"),
        inverseJoinColumns = @JoinColumn(name = "informationTargetId"))
    public Collection<NamedElement> getInformationTarget() {
        if (informationTarget == null) {
            informationTarget = new ArrayList<>();
        }
        return informationTarget;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = NamedElementImpl.class)
    public void setInformationTarget(Collection<NamedElement> informationTarget) {
        this.informationTarget = informationTarget;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "TemplateParameterMetaDef", metaColumn = @Column(name = "templateParameterType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "templateParameterId", table = "InformationFlow")
    public TemplateParameter getTemplateParameter() {
        return templateParameter;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = TemplateParameterImpl.class)
    public void setTemplateParameter(TemplateParameter templateParameter) {
        this.templateParameter = templateParameter;
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
    @JoinColumn(name = "nameExpressionId", table = "InformationFlow")
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
    @ManyToAny(metaDef = "ConnectorMetaDef", metaColumn = @Column(name = "realizingConnectorType"), fetch = FetchType.LAZY)
    @JoinTable(name = "InformationFlow_realizingConnector",
        joinColumns = @JoinColumn(name = "InformationFlowId"),
        inverseJoinColumns = @JoinColumn(name = "realizingConnectorId"))
    public Collection<Connector> getRealizingConnector() {
        if (realizingConnector == null) {
            realizingConnector = new ArrayList<>();
        }
        return realizingConnector;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ConnectorImpl.class)
    public void setRealizingConnector(Collection<Connector> realizingConnector) {
        this.realizingConnector = realizingConnector;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "InformationFlow_ownedComment",
        joinColumns = @JoinColumn(name = "InformationFlowId"),
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
