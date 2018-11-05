package org.openmbee.spec.uml;

import java.util.List;

public interface StringExpression extends TemplateableElement, Expression, MofObject {

    StringExpression getOwningExpression();

    List<StringExpression> getSubExpression();
}
