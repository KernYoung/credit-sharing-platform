package com.fanruan.platform.util;

import org.apache.commons.lang.StringUtils;

import java.util.List;

public class SqlUtil {

    public static String getOracleSQLIn(List<String> ids, int count, String field) {
        if(ids!=null && ids.size() > 0 ){
            count = Math.min(count, 1000);
            int len = ids.size();
            int size = len % count;
            if (size == 0) {
                size = len / count;
            } else {
                size = (len / count) + 1;
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < size; i++) {
                int fromIndex = i * count;
                int toIndex = Math.min(fromIndex + count, len);
                String productId = StringUtils.defaultIfEmpty(StringUtils.join( ids.subList(fromIndex, toIndex).iterator(), "','"), "");
                if (i != 0) {
                    builder.append(" or ");
                }
                builder.append(field).append(" in ('").append(productId).append("')");
            }
            return StringUtils.defaultIfEmpty(builder.toString(), field + " in ('')");
        }
        return  null;
    }
}
