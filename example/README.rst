
Quick Start
-----------

Docker
^^^^^^

Installation instructions are found here: `Docker documentation <https://docs.docker.com/>`_


#. At repo's root level, run ``docker-compose up --build`` to create and start all the services from the configuration. This uses the ``test`` Spring profile
#. Swagger ui at `http://localhost:8080/v3/swagger-ui.html <http://localhost:8080/v3/swagger-ui.html>`_
#. Use the command ``docker-compose down`` to stop any containers from running and to remove the containers, networks, and images created by the ``docker-compose up`` command. This command should always be done before any new attempts to restart the services from the configuration. 

Run command line api test
-------------------------


#. 
   run example app on localhost


   #. 
      if using docker-compose to bring up example app, can do the following to run the tests instead of installing node

      .. code-block::

          docker run -v $PWD:/etc/newman -t --network container:mms postman/newman:alpine run crud.postman_collection.json --environment="test-env.json" --delay-request=300

   #. 
      otherwise `install Node.js <https://nodejs.org/en/download/>`_\ , if not already installed 

   #. 
      copy localhost-env.json.example to localhost-env.json, change values accordingly

      .. code-block::

          npm install -g newman

          newman run crud.postman_collection.json -e localhost-env.json --delay-request 300

Swagger UI of running app
-------------------------

Swagger 3 UI at http://localhost:8080/v3/swagger-ui.html
