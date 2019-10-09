package com.tianzhu.filtering.impl;

import com.tianzhu.filtering.CompoundFilterExpression;
import com.tianzhu.filtering.ExpressionVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:31
 * @Modified: 2018/10/9 14:31
 * @Modified By: liaoyq
 */
public class ComplexExpression extends Expression
        implements CompoundFilterExpression
{
    protected List<Expression> children = new ArrayList();

    public ComplexExpression(List<Expression> children, String columnName)
    {
        this.children = children;

        this.columnName = columnName;
    }

    public ComplexExpression(List<Expression> children, String columnName, String operator)
    {
        this(children, columnName);
        this.operator = operator;
    }

    public List<Expression> getChildExpressions()
    {
        return Collections.unmodifiableList(this.children);
    }

    public String toString()
    {
        StringBuilder str = new StringBuilder();

        str.append("[ operator: ");
        str.append(this.operator);
        str.append(" columnName: ");
        str.append(this.columnName);
        str.append(" expressionValues: ");
        str.append(this.expressionValues);
        str.append(" children: [");
        for (Expression expr : this.children) {
            str.append(expr.toString());
        }
        str.append("]] \n");

        return str.toString();
    }

    public void accept(ExpressionVisitor visitor)
    {
        visitor.visitCompoundFilterExpression(this);
    }
}
