package com.relay42.monitor.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.TimeZone;

public final class QueryUtil {

    public static String queryBuilder(String selectClause, String orderByClause, Map<String, Object> args) {
        StringBuilder stringBuilder = new StringBuilder(selectClause);
        boolean where = true;
        for (Map.Entry<String, Object> param : args.entrySet()) {
            if (where) {
                stringBuilder.append(" WHERE ");
                stringBuilder.append(createClause(param));
                where = false;
            } else {
                if (param != null) {
                    stringBuilder.append(" AND ");
                    stringBuilder.append(createClause(param));
                }
            }
        }
        return stringBuilder.toString() + (orderByClause == null ? " " : orderByClause) + "  ALLOW FILTERING";
    }

    private static String createClause(Map.Entry<String, Object> param) {
        if (param.getKey() == "devices") {
            return " c_device IN (" + makeInClause((String[]) param.getValue()) + ") ";
        } else if (param.getKey() == "from") {
            return " c_date >= '" + convertToUTCString(param.getValue()) + "' ";
        } else if (param.getKey() == "to") {
            return " c_date <= '" + convertToUTCString(param.getValue()) + "' ";
        } else {
            return null;
        }
    }

    private static String convertToUTCString(Object value) {
        SimpleDateFormat utcDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            utcDF.setTimeZone(TimeZone.getTimeZone("UTC"));//my serve timezone is set to UTC
            return utcDF.format(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String makeInClause(String[] value) {
        return "'" + String.join("','", value) + "'";
    }

    public static double Median(long[] values) {
        // sort array
        Arrays.sort(values);
        double median;
        // get count of scores
        int totalElements = values.length;
        // check if total number of scores is even
        if (totalElements % 2 == 0) {
            long sumOfMiddleElements = values[totalElements / 2] +
                    values[totalElements / 2 - 1];
            // calculate average of middle elements
            median = ((double) sumOfMiddleElements) / 2;
        } else {
            // get the middle element
            median = (double) values[values.length / 2];
        }
        return median;
    }
}

