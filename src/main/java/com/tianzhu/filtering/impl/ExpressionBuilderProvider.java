package com.tianzhu.filtering.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tianzhu.filtering.ExpressionBuilder;
import com.tianzhu.filtering.utils.Timestamps;

import java.util.*;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:32
 * @Modified: 2018/10/9 14:32
 * @Modified By: liaoyq
 */
public class ExpressionBuilderProvider implements ExpressionBuilder
{

    public Expression getWhereExpressionSet(JSONObject firstObject, String columnContext)
    {
        Expression result = null;

        JSONArray array = new JSONArray();
        for (String name : firstObject.keySet()) {
            if ((Expression.isExpression(name)) ||
                    (Expression.complexOperatorMap.containsKey(name)) ||
                    (!name.startsWith("$")))
            {
                Object value = firstObject.get(name);
                JSONObject valueValue = new JSONObject();
                valueValue.put(name, value);
                array.add(valueValue);
            }
        }
        JSONArray implicitArray = array;

        result = constructANDExpression(implicitArray, null);

        return result;
    }

    private Expression constructANDExpression(JSONArray array, String columnContext)
    {
        return constructComplexExpression(array, columnContext, "$and");
    }

    private Expression constructORExpression(JSONArray array, String columnContext)
    {
        return constructComplexExpression(array, columnContext, "$or");
    }

    private Expression getComplexChild(Object obj, String columnContext)
    {
        Expression result = null;
        if ((obj instanceof JSONObject))
        {
            JSONObject objectValue = (JSONObject)obj;

            String childName = objectValue.keySet().iterator().next();
            Object childValue = objectValue.get(childName);

            String columnCtx = (childName != null) && (!childName.startsWith("$")) ? childName : columnContext;

            boolean childIsArray = childValue instanceof JSONArray;
            if (childIsArray)
            {
                JSONArray grandChildrenArray = (JSONArray)childValue;
                if ("$or".equals(childName)) {
                    result = constructORExpression(grandChildrenArray, columnCtx);
                } else if ("$and".equals(childName)) {
                    result = constructANDExpression(grandChildrenArray, columnCtx);
                } else if (childName.startsWith("$")) {
                    result = constructMultiValueExpression(grandChildrenArray, columnCtx, childName);
                } else {
                    result = constructANDExpression(grandChildrenArray, columnCtx);
                }
            }
            else
            {
                String propertyName = childName;

                columnCtx = (childName != null) && (!childName.startsWith("$")) ? childName : columnContext;

                columnCtx = (propertyName != null) && (!propertyName.startsWith("$")) ? propertyName : columnContext;

                Object value = childValue;
                if ((value instanceof JSONObject))
                {
                    JSONObject child2 = (JSONObject)value;
                    String child2PropertyName = child2.keySet().iterator().next();
                    Object child2PropertyValue = child2.get(child2PropertyName);
                    if (isDateProperty(child2PropertyName))
                    {
                        Date timestamp = buildDateValue(child2PropertyValue);
                        result = getSimpleExpression(propertyName, timestamp, columnCtx);
                    }
                    else
                    {
                        result = getComplexChild(value, columnCtx);
                    }
                }
                else
                {
                    result = getSimpleExpression(propertyName, value, columnCtx);
                }
            }
        }
        return result;
    }

    private Expression constructMultiValueExpression(JSONArray rawValues, String columnContext, String operator)
    {
        if (!"$between".equals(operator) && !"$in".equals(operator) && !"$nin".equals(operator)) {
            throw new FilterSyntaxException("Expected scalar children for: " + operator);
        }
        List<Object> processedValues = new ArrayList();

        Iterator<Object> iterator = rawValues.iterator();
        while (iterator.hasNext())
        {
            Object value = iterator.next();
            Object finalValue = null;
            if ((value instanceof JSONObject))
            {
                JSONObject wrapper = (JSONObject)value;

                String wrapperPropertyName = wrapper.keySet().iterator().next();
                Object wrapperPropertyValue = wrapper.get(wrapperPropertyName);
                if (isDateProperty(wrapperPropertyName)) {
                    finalValue = buildDateValue(wrapperPropertyValue);
                } else {
                    throw new FilterSyntaxException("Invalid value for: " + operator);
                }
            }
            else
            {
                finalValue = value;
            }
            processedValues.add(finalValue);
        }
        return new SimpleExpression(operator, processedValues, columnContext);
    }

    private Date buildDateValue(Object dateProperty)
    {
        String rawDate = (String)dateProperty;
        Date date = null;
        if (Timestamps.isTimestamp(rawDate)) {
            date = new Date(Timestamps.valueOf(rawDate));
        } else {
            throw new FilterSyntaxException("Invalid date: " + rawDate);
        }
        return date;
    }

    private boolean isDateProperty(String dateProperty)
    {
        return "$date".equals(dateProperty);
    }

    private Expression constructComplexExpression(JSONArray array, String columnContext, String operator)
    {
        List<Expression> children = new ArrayList();
        for (Object obj : array) {
            children.add(getComplexChild(obj, columnContext));
        }
        return new ComplexExpression(children, columnContext, operator);
    }

    private Expression getSimpleExpression(String operator, Object filterValue, String columnName)
    {
        Expression result = null;
        if (Expression.isExpression(operator)) {
            result = new SimpleExpression(operator, filterValue, columnName);
        } else if (!operator.startsWith("$")) {
            result = new SimpleExpression("$eq", filterValue, operator);
        } else {
            throw new FilterSyntaxException("Invalid property for " + operator);
        }
        return result;
    }
}
