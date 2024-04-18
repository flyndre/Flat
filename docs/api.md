# API

The following messages and data are exchanged between the frontends and the backend.

## Exchanged Messages

-   **Open collection**
    -   name: `String`
    -   clientId: `UUID`
    -   (ID is generated): `UUID`
    -   collectionArea (Staked out area): `GeoJSON MultiPolygon`

      Was implemented as Rest-API Http-Post-Endpoint
    Endpoint-Url: /api/Rest/Collection

-   **Get collection**
    -   collectionId: `UUID`
    -   clientId: `UUID`
      

      Was implemented as Rest-API Http-Get-Endpoint
    Endpoint-Url: /api/Rest/Collection/{id}?userId={userId}
    
-   **Set/change division of collectionArea**
    -   List of collectionDivision
        -   area: `GeoJSON Polygon`
        -   name: `String`
        -   id: `UUID`

    Was implemented as Rest-API Http-Put-Endpoint
    Endpoint-Url: /api/Rest/Collection/{id}
    
-   **Assign collectionDivision (participants to themselves or boss to someone else)** --> Not Implemented yet
    -   areaId: `UUID`
    -   clientId: `UUID`    
-   **Access request collection**
    -   username: `String`
    -   clientId: `UUID`
    -   collectionId: `UUID`

Was implemented as Rest-API Http-Post-Endpoint
    Endpoint-Url: /api/Rest/AccessRequest/{id}
    
-   **Access confirmation request to boss**
    -   username: `String`
    -   clientId: `UUID`

Was implemented as Rest-API Http-Get-Endpoint
    Endpoint-Url: /api/Rest/AccessRequest/{id}
    
-   **Access confirmation request from boss**
    -   clientId: `UUID`
    -   accepted: `boolean`

Was implemented as Rest-API Http-Post-Endpoint
    Endpoint-Url: /api/Rest/AccessConfirmation/{id}
      
-   **Access confirmation to participant**
    -   collectionId: `UUID`
    -   accepted: `boolean`
-   **Send progress in the form of GPS data**
    -   GPX track: `GPX track`
    -   If connection has been interrupted, resend unsent data
    -   Start/stop GPS track
-   **Receive progress of all participants from the server**
    -   GPX track: `GPX Track`
    -   In the form of a list of GPS tracks
-   **Close collection**
    -   collectionId: `UUID`

 Was implemented as Rest-API Http-Delete-Endpoint
    Endpoint-Url: /api/Rest/Collection/{id}

