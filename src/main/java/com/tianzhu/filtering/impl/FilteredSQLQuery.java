package com.tianzhu.filtering.impl;

import java.util.Map;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:33
 * @Modified: 2018/10/9 14:33
 * @Modified By: liaoyq
 */
public class FilteredSQLQuery {
    private final String query;
    private final Iterable<String> parameters;
    private final Map<String, Object> values;

    FilteredSQLQuery(String query, Iterable<String> parameters, Map<String, Object> values)
    {
        this.query = query;
        this.values = values;

        this.parameters = parameters;
    }

    public String query()
    {
        return this.query;
    }

    public Iterable<String> parameters()
    {
        return this.parameters;
    }

    public Map<String, Object> values()
    {
        return this.values;
    }
}
