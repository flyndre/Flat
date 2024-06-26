﻿using FlatBackend.Database;
using FlatBackend.DTOs;
using FlatBackend.Interfaces;
using FlatBackend.Models;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using Microsoft.VisualBasic;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.IdGenerators;
using MongoDB.Bson.Serialization.Serializers;

//using System.Text.Json;
using Newtonsoft.Json;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace FlatBackend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class RestController : ControllerBase
    {
        private readonly IMongoDBService _MongoDBService;
        private readonly IWebsocketManager _WebsocketManager;

        public RestController( IMongoDBService mongoDBService, IWebsocketManager websocketManager )
        {
            _MongoDBService = mongoDBService;
            _WebsocketManager = websocketManager;
        }

        //AccessRequest Handshake
        [HttpGet("AccessRequest/{id}")]
        public async Task<string> GetAccessRequest( Guid id, Guid userId )
        {
            try
            {
                var Collection = await _MongoDBService.GetCollection(id);
                if (Collection == null) return NotFound().ToString();
                var validUser = Collection.confirmedUsers.Find(x => x.clientId == userId);
                if (validUser.accepted)
                {
                    List<UserModel> users = Collection.requestedAccess;
                    var Json = JsonConvert.SerializeObject(users);
                    return Json;
                }
                else
                {
                    return Problem(
                            type: "/docs/errors/forbidden",
                            title: "Authenticated user is not authorized.",
                            detail: $"User '{validUser}' must have been Confirmed.",
                            statusCode: StatusCodes.Status403Forbidden,
                            instance: HttpContext.Request.Path
                            ).ToString();
                }
            }
            catch (Exception ex)
            {
                return NotFound().ToString();
            }
        }

        [HttpPost("AccessRequest/{id}")]
        public async Task<string> PostAccessRequestCollection( Guid id, [FromBody] UserModel value )
        {
            try
            {
                var oldCol = await _MongoDBService.GetCollection(id);
                if (oldCol == null) return NotFound().ToJson();
                var validUserList = oldCol.confirmedUsers.Where(x => x.clientId == value.clientId).ToList();
                if (validUserList.Count > 0)
                {
                    if (validUserList[0].accepted)
                    {
                        string resultJson;
                        var resultAccessResult = new ResultAccessRequest() { accepted = true, collection = oldCol };
                        resultJson = JsonConvert.SerializeObject(resultAccessResult);
                        return resultJson;
                    }
                }

                if (oldCol.requestedAccess == null)
                { oldCol.requestedAccess = new List<UserModel>(); }

                oldCol.requestedAccess.Add(value);
                _MongoDBService.ChangeCollection(oldCol);
                var result = await _MongoDBService.GetCollection(id);
                string Json = JsonConvert.SerializeObject(result.requestedAccess.Find(x => x.clientId == value.clientId));
                var accessRequest = new AccessRequestDto();
                accessRequest.clientId = value.clientId;
                accessRequest.collectionId = id;
                accessRequest.username = value.username;
                var confirmedUser = await _WebsocketManager.sendAccessRequestToBoss(accessRequest);
                if (confirmedUser != null)
                {
                    var validUser = oldCol.confirmedUsers.Find(x => x.clientId == value.clientId);
                    if (validUser == null)
                    {
                        oldCol.requestedAccess.Remove(value);
                        oldCol.confirmedUsers.Add(confirmedUser);
                    }
                    else
                    {
                        var index = oldCol.confirmedUsers.IndexOf(validUser);
                        oldCol.confirmedUsers[index] = confirmedUser;
                    }
                    _MongoDBService.ChangeCollection(oldCol);
                    //_WebsocketManager.sendAccessConfirmationToUser(new DTOs.AccessConfirmationDto() { collectionId = id, clientId = value.clientId, accepted = value.accepted });
                    _WebsocketManager.sendUpdateCollection(id);
                    if (confirmedUser.accepted)
                    {
                        var resultAccessResult = new ResultAccessRequest() { accepted = true, collection = oldCol };
                        oldCol.confirmedUsers.Remove(confirmedUser);
                        Json = JsonConvert.SerializeObject(resultAccessResult);
                        return Json;
                    }
                    else
                    {
                        Json = JsonConvert.SerializeObject(new ResultAccessRequest() { accepted = confirmedUser.accepted, collection = null });
                        return Json;
                    }
                }
                else
                {
                    return NotFound("Error the Boss didn't answered the request.").ToString();
                }
            }
            catch (Exception ex)
            {
                return NotFound(ex.ToString()).ToJson();
            }
        }

        [HttpPost("AccessConfirmation/{id}")]
        public async Task<string> PostAccessConfirmationCollection( Guid id, [FromBody] UserModel value )
        {
            try
            {
                _WebsocketManager.setAccessConfirmationWaiting(new AccessConfirmationDto() { clientId = value.clientId, collectionId = id, accepted = value.accepted });
                var result = await _MongoDBService.GetCollection(id);

                string Json = JsonConvert.SerializeObject(result);
                return Json;
            }
            catch (Exception ex)
            {
                return ex.ToString();
            }//call add User to AccessrequestConfirmedlist
        }

        //Collection specifik

        [HttpGet("Collection/{id}")]
        public async Task<string> GetCollection( Guid id, Guid userid )
        {
            try
            {
                var result = await _MongoDBService.GetCollection(id);

                var user = result.confirmedUsers.Find(x => x.clientId == userid);
                if (user != null || result.clientId == userid)
                {
                    var Json = JsonConvert.SerializeObject(result);
                    return Json;
                }
                else
                {
                    return Problem(
                            type: "/docs/errors/forbidden",
                            title: "Authenticated user is not authorized.",
                            detail: $"User '{user}' must have been Confirmed.",
                            statusCode: StatusCodes.Status403Forbidden,
                            instance: HttpContext.Request.Path
                            ).ToString();
                }
            }
            catch (Exception ex)
            {
                return NotFound().ToString();
            }
        }

        // POST api/<RestController>/
        [HttpPost("Collection")]
        public async Task<string> PostOpenCollection( [FromBody] CollectionModel value )
        {
            try
            {
                if (value.collectionDivision == null)
                {
                    value.collectionDivision = new List<AreaModel>();
                }
                if (value.confirmedUsers == null) value.confirmedUsers = new List<UserModel>();
                if (value.confirmedUsers.Count == 0)
                {
                    value.confirmedUsers = new List<UserModel>() { new UserModel() { clientId = value.clientId, username = "admin", accepted = true } };
                }
                if (value.requestedAccess == null)
                {
                    value.requestedAccess = new List<UserModel>();
                }
                await _MongoDBService.AddCollection(value);
                var result = await _MongoDBService.GetCollection(value.id);
                _WebsocketManager.sendUpdateCollection(value.id);
                var Json = JsonConvert.SerializeObject(result);
                return Json;
            }
            catch (Exception ex)
            {
                return ex.ToString();
            }
        }

        // PUT api/<ValuesController>/Collection/5 ChangeAreaDivision
        [HttpPut("Collection/{id}")]
        public async Task<string> PutSetOrChangeAreaDivision( Guid id, [FromBody] List<AreaModel> value )
        {
            try
            {
                var oldCol = await _MongoDBService.GetCollection(id);
                foreach (var area in value)
                {
                    if (area == null) { continue; }
                    var oldArea = oldCol.collectionDivision.Find(x => x.id == area.id);

                    if (oldArea != null)
                    {
                        var index = oldCol.collectionDivision.IndexOf(oldArea);
                        oldCol.collectionDivision[index] = area;
                    }
                    else
                    {
                        oldCol.collectionDivision.Add(area);
                    }
                }
                _MongoDBService.ChangeCollection(oldCol);
                var result = await _MongoDBService.GetCollection(id);
                _WebsocketManager.sendUpdateCollection(id);
                return JsonConvert.SerializeObject(result);
            }
            catch (Exception ex)
            {
                return ex.ToString();
            }
        }

        // POST api/<ValuesController>/RemoveUser/{id}
        [HttpPost("RemoveUser/{id}")]
        public async Task<string> RemoveUser( Guid id, Guid clientId, Guid bossId )
        {
            try
            {
                var oldCol = await _MongoDBService.GetCollection(id);

                if (oldCol != null && bossId != null)
                {
                    if (oldCol.clientId != bossId) return "An Error accured. The collection wasn't found or your not allowed to do this";
                    var usersRequestsList = oldCol.requestedAccess.Where(x => x.clientId == clientId).ToList();
                    var validUsersList = oldCol.confirmedUsers.Where(x => x.clientId == clientId).ToList();
                    var leavingUser = validUsersList.First();
                    var usersDivisionsList = oldCol.collectionDivision.Where(x => x.clientId == clientId).ToList();
                    if (usersRequestsList != null)
                    {
                        foreach (var user in usersRequestsList)
                        {
                            oldCol.requestedAccess.Remove(user);
                        }
                    }
                    if (validUsersList != null)
                    {
                        foreach (var user in validUsersList)
                        {
                            oldCol.confirmedUsers.Remove(user);
                        }
                    }
                    if (usersDivisionsList != null)
                    {
                        foreach (var division in usersDivisionsList)
                        {
                            var index = oldCol.collectionDivision.IndexOf(division);
                            oldCol.collectionDivision[index].clientId = null;
                        }
                    }
                    _WebsocketManager.informKickedUser(clientId, id);
                    _WebsocketManager.removeWebsocketUser(clientId, id);
                    _WebsocketManager.informBossOverLeavingOfUser(id, leavingUser);
                    _MongoDBService.ChangeCollection(oldCol);
                    _WebsocketManager.sendUpdateCollection(oldCol.id);
                }
                return "Success";
            }
            catch (Exception ex) { return NotFound(ex.ToString()).ToString(); }
        }

        // POST api/<ValuesController>/LeaveCollection/{id}
        [HttpPost("LeaveCollection/{id}")]
        public async Task<string> LeaveCollection( Guid id, Guid clientId )
        {
            try
            {
                var oldCol = await _MongoDBService.GetCollection(id);

                if (oldCol != null)
                {
                    var usersRequestsList = oldCol.requestedAccess.Where(x => x.clientId == clientId).ToList();
                    var validUsersList = oldCol.confirmedUsers.Where(x => x.clientId == clientId).ToList();
                    var leavingUser = validUsersList.First();
                    var usersDivisionsList = oldCol.collectionDivision.Where(x => x.clientId == clientId).ToList();
                    if (usersRequestsList != null)
                    {
                        foreach (var user in usersRequestsList)
                        {
                            oldCol.requestedAccess.Remove(user);
                        }
                    }
                    if (validUsersList != null)
                    {
                        foreach (var user in validUsersList)
                        {
                            oldCol.confirmedUsers.Remove(user);
                        }
                    }
                    if (usersDivisionsList != null)
                    {
                        foreach (var division in usersDivisionsList)
                        {
                            var index = oldCol.collectionDivision.IndexOf(division);
                            oldCol.collectionDivision[index].clientId = null;
                        }
                    }

                    _WebsocketManager.removeWebsocketUser(clientId, id);
                    _WebsocketManager.informBossOverLeavingOfUser(id, leavingUser);
                    _MongoDBService.ChangeCollection(oldCol);
                    _WebsocketManager.sendUpdateCollection(oldCol.id);
                }
                return "Success";
            }
            catch (Exception ex) { return NotFound(ex.ToString()).ToString(); }
        }

        // DELETE api/<ValuesController>/Collection/5 CloseCollection
        [HttpDelete("Collection/{id}")]
        public async Task<IActionResult> DeleteCollection( Guid id )
        {
            try
            {
                await _WebsocketManager.sendCollectionClosedInformation(id);
                _MongoDBService.RemoveCollection(id); //and inform all clients
                return Ok();
            }
            catch (Exception ex)
            {
                return NotFound(ex.ToString());
            }
            //call delete Collection in Database
        }
    }
}