package com.tianzhu.filtering;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:29
 * @Modified: 2018/10/9 14:29
 * @Modified By: liaoyq
 */
public abstract interface FilterExpressions {

    public abstract FilterExpression parse(CharSequence paramCharSequence);

    public abstract FilterExpression parse(JSONObject paramJSONObject);

}
