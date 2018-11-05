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
import org.openmbee.spec.uml.ConnectableElementTemplateParameter;
import org.openmbee.spec.uml.ConnectorEnd;
import org.openmbee.spec.uml.Dependency;
import org.openmbee.spec.uml.Element;
import org.openmbee.spec.uml.Namespace;
import org.openmbee.spec.uml.Operation;
import org.openmbee.spec.uml.Parameter;
import org.openmbee.spec.uml.ParameterDirectionKind;
import org.openmbee.spec.uml.ParameterEffectKind;
import org.openmbee.spec.uml.ParameterSet;
import org.openmbee.spec.uml.StringExpression;
import org.openmbee.spec.uml.TemplateParameter;
import org.openmbee.spec.uml.Type;
import org.openmbee.spec.uml.ValueSpecification;
import org.openmbee.spec.uml.VisibilityKind;
import org.openmbee.spec.uml.jackson.MofObjectDeserializer;
import org.openmbee.spec.uml.jackson.MofObjectSerializer;

@Entity
@SecondaryTable(name = "Parameter")
@Table(appliesTo = "Parameter", fetch = FetchMode.SELECT, optional = false)
@DiscriminatorValue(value = "Parameter")
@JsonTypeName(value = "Parameter")
public class ParameterImpl extends MofObjectImpl implements Parameter {

    private ValueSpecification defaultValue;
    private Collection<ConnectorEnd> end;
    private Element owner;
    private VisibilityKind visibility;
    private Type type;
    private ValueSpecification lowerValue;
    private Boolean isOrdered;
    private Integer upper;
    private ParameterDirectionKind direction;
    private String name;
    private Namespace namespace;
    private Integer lower;
    private Collection<ParameterSet> parameterSet;
    private Collection<Dependency> clientDependency;
    private StringExpression nameExpression;
    private ConnectableElementTemplateParameter templateParameter;
    private String default_;
    private Collection<Element> ownedElement;
    private TemplateParameter owningTemplateParameter;
    private Boolean isStream;
    private Boolean isException;
    private ValueSpecification upperValue;
    private String qualifiedName;
    private Boolean isUnique;
    private Operation operation;
    private ParameterEffectKind effect;
    private Collection<Comment> ownedComment;

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ValueSpecificationMetaDef", metaColumn = @Column(name = "defaultValueType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "defaultValueId", table = "Parameter")
    public ValueSpecification getDefaultValue() {
        return defaultValue;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ValueSpecificationImpl.class)
    public void setDefaultValue(ValueSpecification defaultValue) {
        this.defaultValue = defaultValue;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @Transient
    public Collection<ConnectorEnd> getEnd() {
        if (end == null) {
            end = new ArrayList<>();
        }
        return end;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ConnectorEndImpl.class)
    public void setEnd(Collection<ConnectorEnd> end) {
        this.end = end;
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
    @Any(metaDef = "TypeMetaDef", metaColumn = @Column(name = "typeType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "typeId", table = "Parameter")
    public Type getType() {
        return type;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = TypeImpl.class)
    public void setType(Type type) {
        this.type = type;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ValueSpecificationMetaDef", metaColumn = @Column(name = "lowerValueType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "lowerValueId", table = "Parameter")
    public ValueSpecification getLowerValue() {
        return lowerValue;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ValueSpecificationImpl.class)
    public void setLowerValue(ValueSpecification lowerValue) {
        this.lowerValue = lowerValue;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isOrdered", table = "Parameter")
    public Boolean isOrdered() {
        return isOrdered;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setOrdered(Boolean isOrdered) {
        this.isOrdered = isOrdered;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Transient
    public Integer getUpper() {
        return upper;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setUpper(Integer upper) {
        this.upper = upper;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Enumerated(EnumType.STRING)
    public ParameterDirectionKind getDirection() {
        return direction;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setDirection(ParameterDirectionKind direction) {
        this.direction = direction;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Lob
    @org.hibernate.annotations.Type(type = "org.hibernate.type.TextType")
    @Column(name = "name", table = "Parameter")
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
    @Transient
    public Integer getLower() {
        return lower;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setLower(Integer lower) {
        this.lower = lower;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "ParameterSetMetaDef", metaColumn = @Column(name = "parameterSetType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Parameter_parameterSet",
        joinColumns = @JoinColumn(name = "ParameterId"),
        inverseJoinColumns = @JoinColumn(name = "parameterSetId"))
    public Collection<ParameterSet> getParameterSet() {
        if (parameterSet == null) {
            parameterSet = new ArrayList<>();
        }
        return parameterSet;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(contentUsing = MofObjectDeserializer.class, contentAs = ParameterSetImpl.class)
    public void setParameterSet(Collection<ParameterSet> parameterSet) {
        this.parameterSet = parameterSet;
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
    @JoinColumn(name = "nameExpressionId", table = "Parameter")
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
    @Any(metaDef = "ConnectableElementTemplateParameterMetaDef", metaColumn = @Column(name = "templateParameterType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "templateParameterId", table = "Parameter")
    public ConnectableElementTemplateParameter getTemplateParameter() {
        return templateParameter;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ConnectableElementTemplateParameterImpl.class)
    public void setTemplateParameter(ConnectableElementTemplateParameter templateParameter) {
        this.templateParameter = templateParameter;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Lob
    @org.hibernate.annotations.Type(type = "org.hibernate.type.TextType")
    @Transient
    public String getDefault() {
        return default_;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setDefault(String default_) {
        this.default_ = default_;
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
    @JoinColumn(name = "owningTemplateParameterId", table = "Parameter")
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
    @Column(name = "isStream", table = "Parameter")
    public Boolean isStream() {
        return isStream;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setStream(Boolean isStream) {
        this.isStream = isStream;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Column(name = "isException", table = "Parameter")
    public Boolean isException() {
        return isException;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setException(Boolean isException) {
        this.isException = isException;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "ValueSpecificationMetaDef", metaColumn = @Column(name = "upperValueType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "upperValueId", table = "Parameter")
    public ValueSpecification getUpperValue() {
        return upperValue;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = ValueSpecificationImpl.class)
    public void setUpperValue(ValueSpecification upperValue) {
        this.upperValue = upperValue;
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
    @Column(name = "isUnique", table = "Parameter")
    public Boolean isUnique() {
        return isUnique;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setUnique(Boolean isUnique) {
        this.isUnique = isUnique;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(using = MofObjectSerializer.class)
    @Any(metaDef = "OperationMetaDef", metaColumn = @Column(name = "operationType"), fetch = FetchType.LAZY)
    @JoinColumn(name = "operationId", table = "Parameter")
    public Operation getOperation() {
        return operation;
    }

    @JsonProperty(required = true)
    @JsonSetter
    @JsonDeserialize(using = MofObjectDeserializer.class, as = OperationImpl.class)
    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @Enumerated(EnumType.STRING)
    public ParameterEffectKind getEffect() {
        return effect;
    }

    @JsonProperty(required = true)
    @JsonSetter
    public void setEffect(ParameterEffectKind effect) {
        this.effect = effect;
    }

    @JsonProperty(required = true)
    @JsonGetter
    @JsonSerialize(contentUsing = MofObjectSerializer.class)
    @ManyToAny(metaDef = "CommentMetaDef", metaColumn = @Column(name = "ownedCommentType"), fetch = FetchType.LAZY)
    @JoinTable(name = "Parameter_ownedComment",
        joinColumns = @JoinColumn(name = "ParameterId"),
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
