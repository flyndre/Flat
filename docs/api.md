# API

The following messages and data are exchanged between the frontends and the backend.

## Exchanged Messages

-   **Open collection**
    -   name: `String`
    -   clientId: `UUID`
    -   (ID is generated): `UUID`
    -   collectionArea (Staked out area): `GeoJSON MultiPolygon`
-   **Set/change area division**
    -   List of collectionArea
        -   area: `GeoJSON MultiPolygon`
        -   id: `UUID`
-   **Assign areas (participants to themselves or boss to someone else)**
    -   areaId: `UUID`
    -   clientId: `UUID`
-   **Access request collection**
    -   username: `String`
    -   clientId: `UUID`
    -   collectionId: `UUID`
-   **Access confirmation request to boss**
    -   username: `String`
    -   clientId: `UUID`
-   **Access confirmation request from boss**
    -   clientId: `UUID`
    -   accepted: `boolean`
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
