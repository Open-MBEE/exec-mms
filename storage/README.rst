.. _storage:

Storage
-------

This is an implementation of the ``artifacts`` interface using s3 and should work with any S3 compatible storage service.

`MinIO <https://min.io/product/overview>`_ is an open source s3 compatible object storage, it can be used standalone or can add a s3 api layer on top of existing providers using MinIO Gateway, for example, `NAS <https://docs.min.io/docs/minio-gateway-for-nas.html>`_

If s3.access_key or s3.secret_key are omitted, will follow the `default credentials chain <https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html>`_

Configuration
^^^^^^^^^^^^^

  s3.access_key
    This is the access key for the S3 bucket. Required.

  s3.secret_key
    This is the secret key for the S3 bucket. Required.

  s3.region
    This is the region that the S3 bucket is located in. Required.

  s3.bucket
    This is the name of the S3 bucket. Optional.

    | `Default: mms`