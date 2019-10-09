package com.tianzhu.filtering;

import com.tianzhu.filtering.impl.ClauseBuilderProvider;
import com.tianzhu.filtering.impl.ExpressionBuilderProvider;
import com.tianzhu.filtering.impl.FilterExpressionsProvider;
import com.tianzhu.filtering.impl.FilteredSQLQuery;
import com.tianzhu.filtering.utils.SQLFilterProvider;
import org.junit.Test;

import java.util.Map;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 16:30
 * @Modified: 2018/10/9 16:30
 * @Modified By: liaoyq
 */
public class QueryTests {

    public static final ClauseBuilder INSTANCE = new ClauseBuilderProvider(new FilterExpressionsProvider(new ExpressionBuilderProvider()), new SQLFilterProvider());

    @Test
    public void testQuery(){
        String query = "select * from user";
        String jsonfilter = "{\n" +
                "\"SALARY\": {\"$nin\": [1000,2000,3000,4000,5000]} \n " +","+
                "\"$orderby\": {\"SALARY\":  \"ASC\",\"ENAME\":\"DESC\"}"+
                "}";
        FilteredSQLQuery filteredSQLQuery = INSTANCE.filterQuery(query,jsonfilter);
        String sql = filteredSQLQuery.query();
        Iterable<String> parameters = filteredSQLQuery.parameters();
        Map<String, Object> values =  filteredSQLQuery.values();
        System.out.println(filteredSQLQuery);

    }

    @Test
    public void testQuery2(){
        String query = "select * from user";
        String jsonfilter = "{\n" +
                "  \"SALARY\": {\"$date\": \"1989-12-17 08:00:00\"}\n" +","+
                "\"$orderby\": {\"SALARY\":  \"ASC\",\"ENAME\":\"DESC\"}"+
                "}";
        FilteredSQLQuery filteredSQLQuery = INSTANCE.filterQuery(jsonfilter);
        String sql = filteredSQLQuery.query();
        Iterable<String> parameters = filteredSQLQuery.parameters();
        Map<String, Object> values =  filteredSQLQuery.values();
        System.out.println(filteredSQLQuery);

    }

}
