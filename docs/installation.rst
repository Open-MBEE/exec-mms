.. _installation:

============
Installation
============

The MMS, in it's reference implementation configuration currently has two main dependencies\:

  - Relational Database (e.g. Postgresql, MySQL, etc.)
  - Document Store (e.g. Elasticsearch)
  - Optionally, for artifact storage, an S3 compliant object store is necessary. We develop against MinIO and deploy against AWS S3, however, any S3 compliant object store will suffice. (e.g. MinIO)

