package org.openmbee.mms.elastic.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SearchConfiguration.class);

    private static final String TEXT_TYPE = "text";

    private enum EnumSearchType {
        TERM,
        MATCH,
        NONE
    }

    private Map<String, EnumSearchType> config = new HashMap<>();

    public void addField(String field, String type, boolean searchable) {
        if(!searchable) {
            logger.warn("Attempt to search on unsearchable field: " + field);
            config.put(field, EnumSearchType.NONE);
        } else if(TEXT_TYPE.equals(type)) {
            logger.debug("Performing MATCH search for field " + field);
            config.put(field, EnumSearchType.MATCH);
        } else {
            logger.debug("Performing TERM search for field " + field);
            config.put(field, EnumSearchType.TERM);
        }
    }

    /**
     * Adds the clause for one field to the "must" clauses of a bool query.
     */
    public List<Map<String, Object>> addQueryForField(List<Map<String, Object>> must, String field, Object value) {
        if("*".equals(field)) {
            must.add(Map.of("multi_match", Map.of("query", value.toString(), "fields", List.of("*"))));
        } else {
            EnumSearchType searchType = config.get(field);
            if(searchType == null) {
                logger.error("Could not find mapping for field " + field);
                return must;
            }
            switch (searchType){
                case TERM:
                    if (value instanceof List) {
                        must.add(Map.of("terms", Map.of(field, value)));
                    } else if (value instanceof String) {
                        must.add(Map.of("term", Map.of(field, value)));
                    }
                    break;
                case MATCH:
                    if (value instanceof List) {
                        must.add(Map.of("match", Map.of(field, ((List<?>) value).get(0))));
                    } else {
                        must.add(Map.of("match", Map.of(field, value)));
                    }
                    break;
                default:
                    break;
            }
        }
        return must;
    }
}
