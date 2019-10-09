package com.tianzhu.filtering;

import java.util.List;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:28
 * @Modified: 2018/10/9 14:28
 * @Modified By: liaoyq
 */
public abstract interface CompoundFilterExpression extends FilterExpression
{
    public abstract List<? extends FilterExpression> getChildExpressions();
}
