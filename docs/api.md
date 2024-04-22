# API-Docs 📃

The following messages and data are exchanged between the frontends and the backend.

## ✅Implemented

### ⬇️POST `/api/Rest/Collection`: Open collection
Opens a new Collection with the given Information.

**🔵Request-Body**  
- name: `String`  
- clientId: `UUID`      
- collectionArea: `GeoJSON MultiPolygon`    

### ⬆️GET `/api/Rest/Collection/{CollectionId}`: Get collection 
Recieve Collection Information.

**🟢Request-Parameter**  
- userId == clientId: `UUID` 
     
**🟠Path-Variables**   
- CollectionId: `UUID`    
      
### ➡️PUT `/api/Rest/Collection/{collectionId}`: Set or change division of collectionArea
Set and Change the division of the Collection-Area. This Areas has a assigned User and a Name.

**🟠Path-Variables**  
- collectionId: `UUID`    
      
**🔵Request-Body**  
- collectionDivision[] (contains area: `GeoJSON Polygon`, name: `String` and id: `UUID`)
  
          
### ⬇️POST `/api/Rest/AccessRequest/{clientId}`: Access request collection
Request Access for a Collection.

**🟠Path-Variables**  
- clientId: `UUID`  
      
**🔵Request-Body**  
- username: `String`  
- clientId: `UUID`  
- collectionId: `UUID`  
    
### ⬆️GET `/api/Rest/AccessRequest/{CollectionId}`: Access confirmation request to Owner  
- not described -

**🟢Request-Parameter**  
- userId == clientId: `UUID`  
      
**🟠Path-Variables**  
- CollectionId: `UUID`  
      
**🔵Request-Body**  
- username: `String`  

### ⬇️POST `/api/Rest/AccessConfirmation/{clientId}`: Access confirmation request from Owner
Boss affirms the Access-Request of a User with either True or False.

**🟠Path-Variables**  
- clientId: `UUID`  
      
**🔵Request-Body**  
- accepted: `boolean`  

### 🛑DELETE `/api/Rest/Collection/{collectionId}`: Close collection
Deletes a Collection. This can only be performed by an Owner of a Collection
**🟠Path-Variables**  
- collectionId: `UUID`  

 
## 🛑Not yet implemented  

### ➡️Assign collectionDivision (participants to themselves or boss to someone else)  
   **Request-Body**  
      - areaId: `UUID`  
      - clientId: `UUID`  
  
### ➡️Access confirmation to participant  
   **Request-Body**  
      - collectionId: `UUID`  
      - accepted: `boolean`  

### ➡️Send progress in the form of GPS data  
   **Request-Body**  
      - GPX track: `GPX track`  
      - If connection has been interrupted, resend unsent data  
      - Start/stop GPS track  

### ➡️Receive progress of all participants from the server  
   ***Request-Body***  
      - GPX track: `GPX Track`  
      - In the form of a list of GPS tracks  


