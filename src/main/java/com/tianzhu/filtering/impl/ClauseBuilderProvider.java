package com.tianzhu.filtering.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.tianzhu.filtering.ClauseBuilder;
import com.tianzhu.filtering.FilterExpression;
import com.tianzhu.filtering.FilterExpressions;
import com.tianzhu.filtering.utils.SQLFilter;
import com.tianzhu.filtering.utils.SQLFilterProvider;
import com.tianzhu.filtering.utils.Timestamps;

import java.math.BigDecimal;

import java.util.*;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:31
 * @Modified: 2018/10/9 14:31
 * @Modified By: liaoyq
 */
public class ClauseBuilderProvider implements ClauseBuilder
{

    private final FilterExpressions filtering;
    private final SQLFilter sqlFilter;

    public ClauseBuilderProvider(FilterExpressions filtering, SQLFilter sqlFilter)
    {

        this.filtering = filtering;
        this.sqlFilter = sqlFilter;
    }

    public FilteredSQLQuery filterQuery(String query, String jsonFilters)
    {
        HashSet<String> parameters = new HashSet();
        HashMap<String, Object> bindMap = new HashMap();

        JSONObject jsonRoot = null;

        String whereClause = null;
        String orderByClause = null;
        String asOfClause = null;
        try
        {
            jsonRoot = (JSONObject)JSON.parse(jsonFilters);

            whereClause = getWhereClause(jsonRoot, parameters, bindMap);

            orderByClause = getOrderByClauseGson(jsonFilters);

            asOfClause = getAsOfClause(jsonRoot, parameters, bindMap);
        }
        catch (Throwable e)
        {
            throw new FilterSyntaxException(e);
        }
        StringBuilder filteredQuery = new StringBuilder();
        filteredQuery.append("SELECT MAIN_QUERY.* FROM (");
        filteredQuery.append(query);
        filteredQuery.append(") ");
        filteredQuery.append(asOfClause);
        filteredQuery.append("MAIN_QUERY");
        filteredQuery.append(whereClause);
        filteredQuery.append(orderByClause);

        return new FilteredSQLQuery(filteredQuery.toString(), parameters, bindMap);
    }

    public FilteredSQLQuery filterQuery(String jsonFilters)
    {
        HashSet<String> parameters = new HashSet();
        HashMap<String, Object> bindMap = new HashMap();

        JSONObject jsonRoot = null;

        String whereClause = null;
        String orderByClause = null;
        String asOfClause = null;
        try
        {
            jsonRoot = (JSONObject)JSON.parse(jsonFilters);

            whereClause = getWhereClause(jsonRoot, parameters, bindMap);

            orderByClause = getOrderByClauseGson(jsonFilters);

            asOfClause = getAsOfClause(jsonRoot, parameters, bindMap);
        }
        catch (Throwable e)
        {
            throw new FilterSyntaxException(e);
        }
        StringBuilder filteredQuery = new StringBuilder();
        /*filteredQuery.append("SELECT MAIN_QUERY.* FROM (");
        filteredQuery.append(query);
        filteredQuery.append(") ");
        filteredQuery.append(asOfClause);
        filteredQuery.append("MAIN_QUERY");*/
        filteredQuery.append(whereClause);
        filteredQuery.append(orderByClause);

        return new FilteredSQLQuery(filteredQuery.toString(), parameters, bindMap);
    }

    private String getWhereClause(JSONObject root, Set<String> parameters, Map<String, Object> values)
    {
        FilterExpression filterExpression = this.filtering.parse(root);
        SQLExpressionVisitor visitor = new SQLExpressionVisitor(parameters, values);

        filterExpression.accept(visitor);
        String booleanClause = visitor.getQuery();

        StringBuilder whereClause = new StringBuilder();
        if (isNotEmptyBooleanClause(booleanClause))
        {
            whereClause.append(" WHERE ");
            whereClause.append(booleanClause);
        }
        return whereClause.toString();
    }

    private boolean isNotEmptyBooleanClause(String booleanClause)
    {
        return booleanClause.length() > 0;
    }

    private String getOrderByClause(JSONObject root)
    {
        JSONObject orderBy;
        boolean first;
        StringBuilder result = new StringBuilder();
        for (String propertyName : root.keySet()) {
            if ("$orderby".equals(propertyName))
            {
                Object orderByObject = root.get(propertyName);
                if ((orderByObject instanceof JSONObject))
                {
                    orderBy = (JSONObject)orderByObject;

                    first = true;
                    for (String columnName : orderBy.keySet())
                    {
                        String columnOrder = orderBy.get(columnName).toString().toUpperCase();

                        Expression.validateColumnName(columnName);
                        if (!first) {
                            result.append(", ");
                        }
                        result.append(columnName);
                        result.append(" ");
                        if (("ASC".equals(columnOrder)) || ("DESC".equals(columnOrder))) {
                            result.append(columnOrder);
                        } else if ("-1".equals(columnOrder)) {
                            result.append("ASC");
                        } else {
                            result.append("DESC");
                        }
                        first = false;
                    }
                }
            }
        }

        if (result.length() > 0) {
            result.insert(0, " ORDER BY ");
        }
        return result.toString();
    }

    //保持排序是有序的
    private String getOrderByClauseGson(String jsonFilters)
    {
        JSONObject jsonObject;

        try{
            jsonObject = JSONObject.parseObject(jsonFilters, Feature.OrderedField);
        }catch (Throwable e)
        {
            throw new FilterSyntaxException(e);
        }
        boolean first;
        StringBuilder result = new StringBuilder();
        for (java.util.Map.Entry<String,Object> entry:jsonObject.entrySet()) {
            String propertyName = entry.getKey();
            if ("$orderby".equals(propertyName))
            {
                Object orderByObject = entry.getValue();
                if ((orderByObject instanceof JSONObject))
                {
                    JSONObject orderByObjectTmp = (JSONObject)orderByObject;
                    first = true;
                    for (java.util.Map.Entry<String,Object> columnEntry:orderByObjectTmp.entrySet())
                    {
                        String columnName = columnEntry.getKey();
                        String columnOrder = columnEntry.getValue().toString().toUpperCase();

                        Expression.validateColumnName(columnName);
                        if (!first) {
                            result.append(", ");
                        }
                        result.append(columnName);
                        result.append(" ");
                        if (("ASC".equals(columnOrder)) || ("DESC".equals(columnOrder))) {
                            result.append(columnOrder);
                        } else if ("-1".equals(columnOrder)) {
                            result.append("ASC");
                        } else {
                            result.append("DESC");
                        }
                        first = false;
                    }
                }
            }
        }

        if (result.length() > 0) {
            result.insert(0, " ORDER BY ");
        }
        return result.toString();
    }

    private Date buildDateValue(Object dateProperty)
    {
        String rawDate = (String)dateProperty;
        Date date = null;
        if (Timestamps.isTimestamp(rawDate)) {
            date = new Date(Timestamps.valueOf(rawDate));
        } else {
            throw new RuntimeException("Invalid date: " + rawDate);
        }
        return date;
    }

    private String getAsOfClause(JSONObject root, Set<String> parameters, Map<String, Object> values)
    {
        StringBuilder result = new StringBuilder();
        for (String propertyName : root.keySet()) {
            if ("$asof".equals(propertyName))
            {
                Object asOfObject = root.get(propertyName);
                Object dateObject = null;
                if ((asOfObject instanceof JSONObject))
                {
                    JSONObject asOf = (JSONObject)asOfObject;

                    Boolean hasProperty = Boolean.valueOf(asOf.keySet().iterator().hasNext());
                    if (hasProperty.booleanValue())
                    {
                        String timeType = (String)asOf.keySet().iterator().next();
                        if ("$date".equals(timeType))
                        {
                            dateObject = buildDateValue(asOf.get("$date"));
                            result.append(" TIMESTAMP (");
                        }
                        else if ("$scn".equals(timeType))
                        {
                            dateObject = new Long(asOf.get("$scn").toString());
                            result.append(" SCN (");
                        }
                    }
                }
                else if ((asOfObject instanceof String))
                {
                    dateObject = buildDateValue(asOfObject);
                    result.append(" TIMESTAMP (");
                }
                else if ((asOfObject instanceof BigDecimal))
                {
                    dateObject = new Long(asOfObject.toString());
                    result.append(" SCN (");
                }
                else
                {
                    throw new RuntimeException("Invalid asof value");
                }
                if (dateObject != null)
                {
                    String bindName = this.sqlFilter.getParameter(dateObject, parameters, values);

                    result.append(":");
                    result.append(bindName);
                    result.append(") ");

                    break;
                }
                result = new StringBuilder();

                break;
            }
        }
        if (result.length() > 0) {
            result.insert(0, " AS OF ");
        }
        return result.toString();
    }

    public static final ClauseBuilder INSTANCE = new ClauseBuilderProvider(new FilterExpressionsProvider(new ExpressionBuilderProvider()), new SQLFilterProvider());
}
