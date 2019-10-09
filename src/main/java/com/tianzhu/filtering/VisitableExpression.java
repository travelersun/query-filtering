package com.tianzhu.filtering;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:29
 * @Modified: 2018/10/9 14:29
 * @Modified By: liaoyq
 */
public abstract interface VisitableExpression {
    public abstract void accept(ExpressionVisitor paramExpressionVisitor);
}
