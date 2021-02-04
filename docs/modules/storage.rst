.. _storage:

=======
Storage
=======

Storage is the default implementation of the Artifact interface. It is compatible with S3 compliant APIs.

Configuration
-------------

  s3.access_key
    This is the access key for the S3 bucket. Required.

  s3.secret_key
    This is the secret key for the S3 bucket. Required.

  s3.region
    This is the region that the S3 bucket is located in. Required.

  s3.bucket
    This is the name of the S3 bucket. Optional.

    | `Default: mms`