package com.tianzhu.filtering.impl;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:32
 * @Modified: 2018/10/9 14:32
 * @Modified By: liaoyq
 */
public class FilterSyntaxException extends FilterException
{
    private static final long serialVersionUID = 343204666081043905L;

    public FilterSyntaxException(String message)
    {
        super(new RuntimeException(message));
    }

    public FilterSyntaxException(Throwable e)
    {
        super(e);
    }
}
