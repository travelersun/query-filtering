package com.tianzhu.filtering.impl;

import com.tianzhu.filtering.FilterExpression;

import java.util.*;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:31
 * @Modified: 2018/10/9 14:31
 * @Modified By: liaoyq
 */
public abstract class Expression implements FilterExpression
{
    protected String operator;
    protected String columnName;
    protected List<Object> expressionValues = new ArrayList();
    public static Map<String, String> simpleOperatorMap = new HashMap();
    public static Map<String, String> complexOperatorMap = new HashMap();

    static
    {
        simpleOperatorMap.put("$gt", ">");
        simpleOperatorMap.put("$lt", "<");
        simpleOperatorMap.put("$gte", ">=");
        simpleOperatorMap.put("$lte", "<=");
        simpleOperatorMap.put("$eq", "=");
        simpleOperatorMap.put("$ne", "<>");
        simpleOperatorMap.put("$instr", "INSTR");
        simpleOperatorMap.put("$ninstr", "INSTR");
        simpleOperatorMap.put("$like", "LIKE");
        simpleOperatorMap.put("$between", "BETWEEN");
        simpleOperatorMap.put("$in", "IN");
        simpleOperatorMap.put("$nin", "NOT IN");
        simpleOperatorMap.put("$null", " IS NULL ");
        simpleOperatorMap.put("$notnull", " IS NOT NULL ");

        complexOperatorMap.put("$and", " AND ");
        complexOperatorMap.put("$or", " OR ");
    }

    public Object getFirstExpression()
    {
        return this.expressionValues.get(0);
    }

    public String getRawOperator()
    {
        return this.operator;
    }

    public List<Object> getExpressionValues()
    {
        return Collections.unmodifiableList(this.expressionValues);
    }

    public String getColumnContext()
    {
        return this.columnName;
    }

    public static boolean isExpression(String key)
    {
        boolean ixExpression = false;
        if (simpleOperatorMap.containsKey(key)) {
            ixExpression = true;
        }
        return ixExpression;
    }

    public static void validateColumnName(String columnName)
    {
        boolean valid = columnName.matches("\\p{Alpha}([[\\p{Alnum}]#$_])*$");
        if (!valid) {
            throw new FilterSyntaxException("Invalid column name: " + columnName);
        }
    }
}
