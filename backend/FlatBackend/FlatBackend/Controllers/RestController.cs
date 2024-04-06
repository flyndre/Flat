using FlatBackend.Database;
using FlatBackend.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.VisualBasic;
using MongoDB.Bson;
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
        public async Task<string> Get( ObjectId id )
        {
            var Collection = await mongoDBService.GetCollection(id);
            List<UserModel> users = Collection.RequestedAccess;
            var Json = JsonSerializer.Serialize(users);
            return Json;
        }

        [HttpPost("AccessRequest/{id}")]
        public async void PostAccessConfirmationCollection( ObjectId id, [FromBody] UserModel value )
        {
            try
            {
                var oldCol = await mongoDBService.GetCollection(id);
                UserModel? user = oldCol.RequestedAccess.Find(e => e.Id == value.Id);
                oldCol.RequestedAccess.Remove(user);
                oldCol.ConfirmedUsers.Add(user);
                mongoDBService.ChangeCollection(oldCol);
            }
            catch
            {
            }//call add User to AccessrequestConfirmedlist
        }

        //Collection specifik
        // POST api/<RestController>/
        [HttpPost("Collection")]
        public async Task<string> PostOpenCollection( [FromBody] CollectionModel value )
        {
            mongoDBService.AddCollection(value);
            var result = await mongoDBService.GetCollection(value.Id);
            return JsonSerializer.Serialize(result);
        }

        [HttpPost("Collection/{id}")]
        public async void PostAccessRequestCollection( ObjectId id, [FromBody] UserModel value )
        {
            var oldCol = await mongoDBService.GetCollection(id);
            oldCol.RequestedAccess.Add(value);
            mongoDBService.ChangeCollection(oldCol);
            //call add User to Accessrequestlist
        }

        // PUT api/<ValuesController>/Collection/5 ChangeAreaDivision
        [HttpPut("Collection/{id}")]
        public async Task<string> PutSetOrChangeAreaDivision( ObjectId id, [FromBody] List<AreaModel> value )
        {
            var oldCol = await mongoDBService.GetCollection(id);
            foreach (var area in value)
            {
                oldCol.CollectionArea.Add(area);
            }
            mongoDBService.ChangeCollection(oldCol);
            oldCol = await mongoDBService.GetCollection(id);
            return JsonSerializer.Serialize(oldCol);
        }

        // DELETE api/<ValuesController>/Collection/5 CloseCollection
        [HttpDelete("Collection/{id}")]
        public void DeleteCollection( ObjectId id )
        {
            mongoDBService.RemoveCollection(id);
            //call delete Collection in Database
        }
    }
}