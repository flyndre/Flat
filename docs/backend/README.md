# Backend
The Backend is desingned as an Mikroservice architecture.
It's splitted in two parts an databank to persist the sessiondata and an serverapp,
which koordinates the communication.
## Database

## C#-Container
### How to build the container image
1. Publish the application inside VisualStudio to a folder [here](../..\backend\FlatBackend\FlatBackend\bin\Release\net8.0\publish).
2. copy the folder to the machine on which you want to build the image. scp can be helpful.
3. navigate in the publish folder on the build system
4. run ``docker build -t flatbackendimage -f Dockerfile .`` maybe you nee sudo. You can rename ``flatbackendimage`` to whatever you like. It's the name of the image.
5. Now you can use the image to create a container or rebuild your compose stack with ``docker-compose up -d``
## Docker-Compose config
Used compose file:
```docker-compse
    version: '3.8'
services:
  mongodb:
    image: mongo:latest
    ports:
      - '27017:27017'
    volumes:
      - dbdata:/data/db

  flat:
    image: flatbackendimage
    ports:
      - '8080:8080'
    environment:
      - MONGODBCONNECTIONSTRING=mongodb://192.168.2.82:27017


volumes:
  dbdata:
```

## Communication
The Communication is splitted in two parts. There is the stateless part and the part which depends on state.
For the stateless communication an REST-API is used. For the stateful Communication we will use websockets.
### REST-API
At the Moment the REST-Endpoint URLS are:
 - /api/Rest/AccessRequest/{id}
 - /api/Rest/AccessConfirmation/{id}
 - /api/Rest/Collection/{id}
 - /api/Rest/Collection/

To use the Backend properly you need an running docker-container of MongoDB.
Here the docker-command for Container initiallisation: docker run --name mongodb -p 27017:27017 -d mongodb/mongodb-community-server:latest
next you have to create the Database for the Backend named: CollectionsDatabase with the Collection collections inside the Mongodb.
I used the GUI MongoCompass to create the Database and the Collection.
When the Container is up and running you should can create an Collection and use all Endpoints.
The Single Endpointfunction will be documented when their implemented.
### Websocket
