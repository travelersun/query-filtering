package com.tianzhu.filtering.utils;

import java.util.Map;
import java.util.Set;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:30
 * @Modified: 2018/10/9 14:30
 * @Modified By: liaoyq
 */
public abstract interface SQLFilter {

    public static final String FILTER_PREFIX = "F__QV_";

    public abstract String getParameter(Object paramObject, Set<String> paramSet, Map<String, Object> paramMap);
}
