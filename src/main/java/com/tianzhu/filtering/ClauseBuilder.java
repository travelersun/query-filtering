package com.tianzhu.filtering;

import com.tianzhu.filtering.impl.FilteredSQLQuery;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:26
 * @Modified: 2018/10/9 14:26
 * @Modified By: liaoyq
 */
public abstract interface ClauseBuilder {

    public abstract FilteredSQLQuery filterQuery(String paramString1, String paramString2);

    public abstract FilteredSQLQuery filterQuery(String paramString2);

}
