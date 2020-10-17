## Elastic

Elasticsearch implementation for json document storage, indexing and search. Implements the `*IndexDAO` and `SearchService` interfaces from `core`

On project creation, will look for a mapping file based on the project schema prefixed to '_node.json' under $classpath/elastic_mappings, otherwise will use elastic_mappins/default_node.json for field mapping