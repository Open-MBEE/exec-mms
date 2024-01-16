.. _opensearch:

OpenSearch
-------

OpenSearch implementation for json document storage, indexing and search. Implements the ``*IndexDAO`` and ``SearchService`` interfaces from ``core``

On project creation, will look for a mapping file based on the project schema prefixed to '_node.json' under $classpath/elastic_mappings, otherwise will use elastic_mappins/default_node.json for field mapping

Configuration
^^^^^^^^^^^^^

The following are a list of options to configure the Elastic Module for MMS.

  opensearch.host
    The host name of the Opensearch server or cluster. Required.

  opensearch.port
    The port number of the Opensearch server or cluster. Required.

  opensearch.http
    The transport protocol to use to connect to the Opensearch server or cluster. Required.

  opensearch.limit.result
    The maximum number of results a single search request should return. Optional.

    | `Default: 10000`

  opensearch.limit.term
    The maximum number of terms that a search query should contain. Optional.

    | `Default: 1000`

  opensearch.limit.scrollTimeout
    The maximum time to wait for search requests. Optional.

    | `Default: 1000`

  opensearch.limit.get
    The maximum number of elements that a single get request should return. Optional.

    | `Default: 5000`

  opensearch.limit.index
    The maximum number of elements that will be indexed in a single bulk request. Optional.

    | `Default: 5000`

  opensearch.limit.commit
    The maximum number of elements to limit commit objects. Set this to a reasonable size in order to avoid object size limitations in Opensearch.

    | `Default: 10000`

Elastic Mappings
^^^^^^^^^^^^^^^^

Elastic mappings are necessary to index fields correctly in Opensearch. These mappings are available `here <https://github.com/Open-MBEE/mms/tree/develop/elastic/src/main/resources/elastic_mappings>`_.