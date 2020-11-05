package org.openmbee.mms.rdb.repositories;

import java.util.Set;

public abstract class BaseRowMapper {

    protected String getFieldName(String metadata, Set keys) {
        String[] arr = metadata.split("\\.");
        if (arr.length < 2) {
            return getSingleFieldName(arr[0], keys);
        } else {
            return getSingleFieldName(arr[1], keys);
        }
    }

    protected String getSingleFieldName(String field, Set keys) {
        for (Object key : keys) {
            if (key.toString().equalsIgnoreCase(field)) {
                return key.toString();
            }
        }
        return null;
    }
}
