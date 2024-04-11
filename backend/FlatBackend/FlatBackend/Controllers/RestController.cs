using FlatBackend.Database;
using FlatBackend.Models;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using Microsoft.VisualBasic;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.IdGenerators;
using MongoDB.Bson.Serialization.Serializers;
using System.Text.Json;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace FlatBackend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class RestController : ControllerBase
    {
        private MongoDBService mongoDBService = new MongoDBService();

        //AccessRequest Handshake
        [HttpGet("AccessRequest/{id}")]
        public async Task<ObjectResult> Get( Guid id )
        {
            try
            {
                var Collection = await mongoDBService.GetCollection(id);
                List<UserModel> users = Collection.requestedAccess;
                var Json = JsonSerializer.Serialize(users);
                return Ok(users);
            }
            catch (Exception ex)
            {
                return NotFound(ex.ToString());
            }
        }

        [HttpPost("AccessRequest/{id}")]
        public async Task<ObjectResult> PostAccessRequestCollection( Guid id, [FromBody] UserModel value )
        {
            try
            {
                var oldCol = await mongoDBService.GetCollection(id);
                if (oldCol.requestedAccess == null)
                { oldCol.requestedAccess = new List<UserModel>(); }

                oldCol.requestedAccess.Add(value);
                mongoDBService.ChangeCollection(oldCol);
                return Ok(new object { });
            }
            catch (Exception ex)
            {
                return NotFound(ex.ToString());
            }
            //call add User to Accessrequestlist
        }

        [HttpPost("AccessConfirmation/{id}")]
        public async Task<ObjectResult> PostAccessConfirmationCollection( Guid id, [FromBody] UserModel value )
        {
            try
            {
                var oldCol = await mongoDBService.GetCollection(id);
                UserModel? user = oldCol.requestedAccess.Find(e => e.clientId == value.clientId);
                oldCol.requestedAccess.Remove(user);
                oldCol.confirmedUsers.Add(user);
                mongoDBService.ChangeCollection(oldCol);
                return Ok(new object { });
            }
            catch (Exception ex)
            {
                return NotFound(ex.ToString());
            }//call add User to AccessrequestConfirmedlist
        }

        //Collection specifik
        // POST api/<RestController>/
        [HttpPost("Collection")]
        public async Task<string> PostOpenCollection( [FromBody] CollectionModel value )
        {
            try
            {
                if (value.collectionArea == null)
                {
                    value.collectionArea = new List<AreaModel>();
                }
                if (value.confirmedUsers == null)
                {
                    value.confirmedUsers = new List<UserModel>();
                }
                if (value.requestedAccess == null)
                {
                    value.requestedAccess = new List<UserModel>();
                }
                mongoDBService.AddCollection(value);
                var result = await mongoDBService.GetCollection(value.id);
                var Json = JsonSerializer.Serialize(result);
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
                var oldCol = await mongoDBService.GetCollection(id);
                foreach (var area in value)
                {
                    oldCol.collectionArea.Add(area);
                }
                mongoDBService.ChangeCollection(oldCol);
                oldCol = await mongoDBService.GetCollection(id);
                return JsonSerializer.Serialize(oldCol);
            }
            catch (Exception ex)
            {
                return ex.ToString();
            }
        }

        // DELETE api/<ValuesController>/Collection/5 CloseCollection
        [HttpDelete("Collection/{id}")]
        public async Task<ObjectResult> DeleteCollection( Guid id )
        {
            try
            {
                mongoDBService.RemoveCollection(id); //and inform all clients
                return Ok(new object { });
            }
            catch (Exception ex)
            {
                return NotFound(ex.ToString());
            }
            //call delete Collection in Database
        }
    }
}