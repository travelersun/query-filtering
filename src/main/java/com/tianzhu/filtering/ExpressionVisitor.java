package com.tianzhu.filtering;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:28
 * @Modified: 2018/10/9 14:28
 * @Modified By: liaoyq
 */
public abstract interface ExpressionVisitor {

    public abstract void visitFilterExpression(FilterExpression paramFilterExpression);

    public abstract void visitCompoundFilterExpression(CompoundFilterExpression paramCompoundFilterExpression);

}
