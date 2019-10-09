package com.tianzhu.filtering.impl;

import com.tianzhu.filtering.ExpressionVisitor;

import java.util.Date;
import java.util.List;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:33
 * @Modified: 2018/10/9 14:33
 * @Modified By: liaoyq
 */
public class SimpleExpression  extends Expression
{
    public SimpleExpression(String name, Object value, String columnName)
    {
        if (("".equals(columnName)) || (columnName == null)) {
            throw new FilterSyntaxException("No column context for simple expression -> " + name);
        }
        validateColumnName(columnName);

        validateFilter(name, value);

        this.operator = name;
        this.expressionValues.add(value);
        this.columnName = columnName;
    }

    public SimpleExpression(String name, List<Object> values, String columnName)
    {
        if (("".equals(columnName)) || (columnName == null)) {
            throw new RuntimeException("No column context for simple expression -> " + name);
        }
        validateColumnName(columnName);
        for (Object value : values)
        {
            validateFilter(name, value);
            this.expressionValues.add(value);
        }
        this.operator = name;
        this.columnName = columnName;
    }

    private void validateFilter(String operator, Object value)
    {
        boolean valid = true;

        valid = (valid) && (simpleOperatorMap.containsKey(operator));
        if ((value instanceof String))
        {
            valid = (valid) && (!operator.startsWith("$lt")) && (!operator.startsWith("$gt"));
        }
        else if ((value instanceof Number))
        {
            valid = (valid) && (!"$instr".equals(operator)) && (!"$ninstr".equals(operator));
        }
        else if ((value instanceof Date))
        {
            valid = (valid) && (!"$instr".equals(operator)) && (!"$ninstr".equals(operator));
            valid = (valid) && (!"$like".equals(operator));
        }
        else if (value == null)
        {
            valid = (valid) && (("$between".equals(operator)) || ("$null".equals(operator)) || ("$notnull".equals(operator)));
        }
        else if ((!"$null".equals(operator)) && (!"$notnull".equals(operator)))
        {
            if (valid) {}
            valid = false;
        }
        if (!valid) {
            throw new FilterSyntaxException("Invalid type of value for filter: " + operator);
        }
    }

    public String toString()
    {
        return " operator: " + this.operator + " expressionValues: " + this.expressionValues + " columnName " + this.columnName + "\n";
    }

    public void accept(ExpressionVisitor visitor)
    {
        visitor.visitFilterExpression(this);
    }
}
