.. _cameo:

Cameo
-----

This modules adds behavior for ``cameo`` projects that mimics behavior from `mms 3.x <https://github.com/Open-MBEE/mms-alfresco>`_ AKA Donbot

For projects created using schema ``cameo``\ , this will:


* create holding bin elements for the project
* determine element types such as ``view``\ , ``document``\ , ``group``\ , etc and adds endpoints for getting them (as used by `ve <https://github.com/Open-MBEE/ve>`_ and `mdk <https://github.com/Open-MBEE/mdk>`_\ )
* will follow mounted projects when getting elements
