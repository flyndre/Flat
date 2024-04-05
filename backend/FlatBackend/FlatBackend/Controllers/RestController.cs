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
        //AccessRequest Handshake
        [HttpGet("AccessRequest/{id}")]
        public string Get( ObjectId id )
        {
            // send List of AccessRequests
            return "Hey " + id;
        }

        [HttpPost("AccessRequest/{id}")]
        public void PostAccessConfirmationCollection( ObjectId id, [FromBody] string value )
        {
            //call add User to Accessrequestlist
        }

        //Collection specifik
        // POST api/<RestController>/
        [HttpPost("Collection")]
        public string PostOpenCollection( [FromBody] CollectionModel value )
        {
            MongoDBService mongoDBService = new MongoDBService();
            mongoDBService.AddCollection(value);
            return JsonSerializer.Serialize(value);
        }

        [HttpPost("Collection/{id}")]
        public void PostAccessRequestCollection( ObjectId id, [FromBody] string value )
        {
            //call add User to Accessrequestlist
        }

        // PUT api/<ValuesController>/Collection/5 ChangeAreaDivision
        [HttpPut("Collection/{id}")]
        public void PutSetOrChangeAreaDivision( ObjectId id, [FromBody] string value )
        {
            //call Change or set AreaDivision in Database
        }

        // DELETE api/<ValuesController>/Collection/5 CloseCollection
        [HttpDelete("Collection/{id}")]
        public void DeleteCollection( ObjectId id )
        {
            //call delete Collection in Database
        }
    }
}