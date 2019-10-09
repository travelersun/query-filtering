package com.tianzhu.filtering.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 14:30
 * @Modified: 2018/10/9 14:30
 * @Modified By: liaoyq
 */
public class SQLFilterProvider implements SQLFilter
{
    public String getParameter(Object value, Set<String> parameters, Map<String, Object> values)
    {
        StringBuilder parameterName = new StringBuilder();

        Iterator<String> iter = values.keySet().iterator();
        while (iter.hasNext())
        {
            String key = (String)iter.next();
            if (key.startsWith("F__QV_"))
            {
                Object mapValue = values.get(key);
                if (value.equals(mapValue))
                {
                    parameterName.append(key);
                    break;
                }
            }
        }
        if (parameterName.length() == 0)
        {
            parameterName.append("F__QV_");
            parameterName.append(values.size());

            String param = parameterName.toString();

            values.put(param, value);

            parameters.add(param);
        }
        return parameterName.toString();
    }
}
