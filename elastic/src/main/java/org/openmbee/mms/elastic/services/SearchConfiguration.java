package org.openmbee.mms.elastic.services;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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

    public BoolQueryBuilder addQueryForField(BoolQueryBuilder query, String field, String value) {
        if("*".equals(field)) {
            query.must(QueryBuilders.multiMatchQuery(value, "*"));
        } else {
            EnumSearchType searchType = config.get(field);
            if(searchType == null) {
                logger.error("Could not find mapping for field " + field);
                return query;
            }
            switch (searchType){
                case TERM:
                    query.must(QueryBuilders.termQuery(field, value));
                    break;
                case MATCH:
                    query.must(QueryBuilders.matchQuery(field, value));
                    break;
                default:
                    break;
            }
        }
        return query;
    }
}
