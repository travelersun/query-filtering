package com.tianzhu.filtering;

import com.alibaba.fastjson.JSONObject;
import com.tianzhu.filtering.impl.Expression;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:28
 * @Modified: 2018/10/9 14:28
 * @Modified By: liaoyq
 */
public abstract interface ExpressionBuilder {

    public abstract Expression getWhereExpressionSet(JSONObject paramJSONObject, String paramString);

}
