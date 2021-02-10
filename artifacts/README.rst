.. _artifacts:

Artifacts
---------

Allows artifacts (binary objects) to be attached to elements. Attached artifact info are added to an element object under the ``_artifacts`` key and versioned as part of element data.

.. code-block:: JSON

       {
           "id": "elementId",
           "name": "example element with artifact",
           "otherKeys": "other values",
           "_artifacts": [
               {
                   "location": "string depends on storage impl",
                   "locationType": "internal",
                   "mimetype": "image/svg+xml",
                   "extension": "svg"
               }
           ]
       }


Adds endpoints for getting and uploading binary content

Adds ArtifactStorage interface - this allows different implementations to be used if desired given the right module

.. code-block:: java

       public interface ArtifactStorage {

           byte[] get(String location, ElementJson element, String mimetype);
           //returns location
           String store(byte[] data, ElementJson element, String mimetype);
       }


see :ref:`storage` for an example reference implementation using s3
