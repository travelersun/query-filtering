package com.tianzhu.filtering.impl;

import com.tianzhu.filtering.CompoundFilterExpression;
import com.tianzhu.filtering.ExpressionVisitor;
import com.tianzhu.filtering.FilterExpression;
import com.tianzhu.filtering.utils.SQLFilter;
import com.tianzhu.filtering.utils.SQLFilterProvider;


import java.util.*;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:33
 * @Modified: 2018/10/9 14:33
 * @Modified By: liaoyq
 */
public class SQLExpressionVisitor implements ExpressionVisitor
{
    StringBuilder query = new StringBuilder();
    Set<String> parameters = new HashSet();
    Map<String, Object> values = new HashMap();

    public SQLExpressionVisitor(Set<String> parameters, Map<String, Object> values)
    {
        if (parameters != null) {
            this.parameters = parameters;
        }
        if (values != null) {
            this.values = values;
        }
    }

    public String getQuery()
    {
        return this.query.toString();
    }

    public void visitFilterExpression(FilterExpression expr)
    {
        String operator = expr.getRawOperator();
        String columnName = expr.getColumnContext();

        String sqlOperator = (String)Expression.simpleOperatorMap.get(operator);
        if (("$instr".equals(operator)) || ("$ninstr".equals(operator)))
        {
            this.query.append(sqlOperator);
            this.query.append("(UPPER(");

            this.query.append(columnName);

            this.query.append("), UPPER(");
            this.query.append("#{");
            this.query.append(SQL_UTILS.getParameter(expr.getExpressionValues().get(0), this.parameters, this.values));

            this.query.append("})) ");
            if ("$instr".equals(operator)) {
                this.query.append("> 0");
            } else {
                this.query.append("= 0");
            }
        }
        else if (("$null".equals(operator)) || ("$notnull".equals(operator)))
        {
            this.query.append(columnName);
            this.query.append(" ");
            this.query.append(sqlOperator);
        }
        else if ("$between".equals(operator))
        {
            if (expr.getExpressionValues().size() != 2) {
                throw new RuntimeException("Invalid number of argument for $between");
            }
            this.query.append(buildBetween(expr, this.parameters, this.values));
        }
        else if ("$in".equals(operator))
        {
            if (expr.getExpressionValues().size() < 1) {
                throw new RuntimeException("Invalid number of argument for $in");
            }
            this.query.append(buildIn(expr, this.parameters, this.values));
        }
        else if ("$nin".equals(operator))
        {
            if (expr.getExpressionValues().size() < 1) {
                throw new RuntimeException("Invalid number of argument for $nin");
            }
            this.query.append(buildNIn(expr, this.parameters, this.values));
        }
        else
        {
            this.query.append(buildGenericOperator(sqlOperator, expr, expr

                    .getExpressionValues().get(0), this.parameters, this.values));
        }
    }

    public void visitCompoundFilterExpression(CompoundFilterExpression expr)
    {
        List<? extends FilterExpression> children = expr.getChildExpressions();
        if (children.isEmpty()) {
            return;
        }
        this.query.append(" (");

        boolean first = true;
        String sqlOperator = (String)Expression.complexOperatorMap.get(expr
                .getRawOperator());
        for (FilterExpression childExpr : children)
        {
            if (!first)
            {
                this.query.append(" ");
                this.query.append(sqlOperator);
                this.query.append(" ");
            }
            childExpr.accept(this);

            first = false;
        }
        this.query.append(") ");
    }

    private String buildBetween(FilterExpression expr, Set<String> parameters, Map<String, Object> valuesMap)
    {
        StringBuilder result = new StringBuilder();
        String operator = expr.getRawOperator();

        String sqlOperator = (String)Expression.simpleOperatorMap.get(operator);
        if ("$between".equals(operator))
        {
            if (expr.getExpressionValues().size() != 2) {
                throw new RuntimeException("Invalid number of argument for $between");
            }
            Object leftValue = expr.getExpressionValues().get(0);
            Object rightValue = expr.getExpressionValues().get(1);
            if ((leftValue == null) && (rightValue == null)) {
                throw new RuntimeException("Both values cannot be null in $between");
            }
            if ((leftValue == null) && (!(rightValue instanceof String)))
            {
                sqlOperator = (String)Expression.simpleOperatorMap.get("$lte");
                result.append(buildGenericOperator(sqlOperator, expr, rightValue, parameters, valuesMap));
            }
            else if ((rightValue == null) && (!(leftValue instanceof String)))
            {
                sqlOperator = (String)Expression.simpleOperatorMap.get("$gte");
                result.append(buildGenericOperator(sqlOperator, expr, leftValue, parameters, valuesMap));
            }
            else if ((leftValue != null) && (rightValue != null))
            {
                result.append(expr.getColumnContext());
                result.append(" ");
                result.append(sqlOperator);
                result.append(" #{");
                result.append(SQL_UTILS.getParameter(leftValue, parameters, valuesMap));
                result.append("}");
                result.append(" AND #{");
                result.append(SQL_UTILS.getParameter(rightValue, parameters, valuesMap));
                result.append("} ");
            }
            else
            {
                throw new RuntimeException("Values must not be null in $between for strings");
            }
        }
        return result.toString();
    }

    private String buildIn(FilterExpression expr, Set<String> parameters, Map<String, Object> valuesMap)
    {
        StringBuilder result = new StringBuilder();
        String operator = expr.getRawOperator();

        String sqlOperator = (String)Expression.simpleOperatorMap.get(operator);
        if ("$in".equals(operator))
        {
            if (expr.getExpressionValues().size() < 1) {
                throw new RuntimeException("Invalid number of argument for $in");
            }

            result.append(expr.getColumnContext());
            result.append(" ");
            result.append(sqlOperator);

            result.append(" ( ");

            List<String> inParamList = new ArrayList<>();

            for(Object inValue :  expr.getExpressionValues()){

                if(inValue != null ){
                    String inItem = "";
                    inItem += " #{";
                    inItem += SQL_UTILS.getParameter(inValue, parameters, valuesMap);
                    inItem += "}";
                    inParamList.add(inItem);
                }
            }

            result.append(String.join(",",inParamList));

            result.append(" ) ");

        }
        return result.toString();
    }

    private String buildNIn(FilterExpression expr, Set<String> parameters, Map<String, Object> valuesMap)
    {
        StringBuilder result = new StringBuilder();
        String operator = expr.getRawOperator();

        String sqlOperator = (String)Expression.simpleOperatorMap.get(operator);
        if ("$nin".equals(operator))
        {
            if (expr.getExpressionValues().size() < 1) {
                throw new RuntimeException("Invalid number of argument for $nin");
            }

            result.append(expr.getColumnContext());
            result.append(" ");
            result.append(sqlOperator);

            result.append(" ( ");

            List<String> inParamList = new ArrayList<>();

            for(Object inValue :  expr.getExpressionValues()){

                if(inValue != null ){
                    String inItem = "";
                    inItem += " #{";
                    inItem += SQL_UTILS.getParameter(inValue, parameters, valuesMap);
                    inItem += "}";
                    inParamList.add(inItem);
                }
            }

            result.append(String.join(",",inParamList));

            result.append(" ) ");

        }
        return result.toString();
    }

    private String buildGenericOperator(String sqlOperator, FilterExpression expr, Object value, Set<String> parameters, Map<String, Object> valuesMap)
    {
        StringBuilder result = new StringBuilder();

        result.append(expr.getColumnContext());
        result.append(" ");

        result.append(sqlOperator);

        result.append(" ");
        result.append("#{");
        result.append(SQL_UTILS.getParameter(value, parameters, valuesMap));
        result.append("}");
        return result.toString();
    }

    private static final SQLFilter SQL_UTILS = new SQLFilterProvider();
}
