# Backend
The Backend is desingned as an Mikroservice architecture.
It's splitted in two parts an databank to persist the sessiondata and an serverapp,
which koordinates the communication.
## Database

## C#-Container

## Communication
The Communication is splitted in two parts. There is the stateless part and the part which depends on state.
For the stateless communication an REST-API is used. For the stateful Communication we will use websockets.
### REST-API
At the Moment the REST-Endpoint URLS are:
/api/Rest/AccessRequest/{id}
/api/Rest/Collection/{id}
/api/Rest/Collection/
The Single Endpointfunction will be documented when their implemented.
### Websocket
