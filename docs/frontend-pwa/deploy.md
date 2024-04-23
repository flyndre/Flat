# Deploy

This document describes how the pwa frontend of flat can be deployed.

## With Docker

### Build image

1. clone this repository
2. checkout the main branch
   `git checkout main`
3. pull the newest version (not needed of newly cloned)
   `git pull`
4. change in the pwa directory
   `cd frontend-pwa`
5. build the docker image
   `docker build -t flatfrontendimage -f Dockerfile .`
   you can change the image name `flatfrontendimage` freely to your needs.

### Run the container

-   With comopse:
    1. create a file called `docker-compose.yaml`
    2. copy the following into the file
       ``
       flatfrontend:
       container_name: flatfrontend
       image: flatfrontendimage
       ports:
        - 80:80
          restart: always
    ```
    With this configuration the pwa app will be available under http://<yourServerAddress>/
    You can change all parameter to your need. Please refer for further information to the official docker and docker-compose documentations.
    ```
