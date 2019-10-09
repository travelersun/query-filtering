package com.tianzhu.filtering.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tianzhu.filtering.ExpressionBuilder;
import com.tianzhu.filtering.FilterExpression;
import com.tianzhu.filtering.FilterExpressions;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:32
 * @Modified: 2018/10/9 14:32
 * @Modified By: liaoyq
 */
public class FilterExpressionsProvider implements FilterExpressions
{

    private final ExpressionBuilder expressionBuilder;


    public FilterExpressionsProvider(ExpressionBuilder expressionBuilder)
    {

        this.expressionBuilder = expressionBuilder;
    }

    public FilterExpression parse(CharSequence text)
    {
        JSONObject root = (JSONObject)JSON.parse(String.valueOf(text));
        return parse(root);
    }

    public FilterExpression parse(JSONObject root)
    {
        return this.expressionBuilder.getWhereExpressionSet(root, null);
    }
}
