package com.tianzhu.filtering;

import java.util.List;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:29
 * @Modified: 2018/10/9 14:29
 * @Modified By: liaoyq
 */
public abstract interface FilterExpression  extends VisitableExpression
{
    public abstract String getRawOperator();

    public abstract List<Object> getExpressionValues();

    public abstract String getColumnContext();
}
