using Microsoft.AspNetCore.Mvc;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace FlatBackend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class RestController : ControllerBase
    {
        //AccessRequest Handshake
        [HttpGet("AccessRequest/{id}")]
        public string Get( int id )
        {
            // send List of AccessRequests
            return "Hey";
        }

        [HttpPost("AccessRequest/{id}")]
        public void PostAccessConfirmationCollection( int id, [FromBody] string value )
        {
            //call add User to Accessrequestlist
        }

        //Collection specifik
        // POST api/<RestController>/
        [HttpPost("Collection")]
        public void PostOpenCollection( [FromBody] string value )
        {
            //call create Database and send Objekt back
        }

        [HttpPost("Collection/{id}")]
        public void PostAccessRequestCollection( int id, [FromBody] string value )
        {
            //call add User to Accessrequestlist
        }

        // PUT api/<ValuesController>/Collection/5 ChangeAreaDivision
        [HttpPut("Collection/{id}")]
        public void PutSetOrChangeAreaDivision( int id, [FromBody] string value )
        {
            //call Change or set AreaDivision in Database
        }

        // DELETE api/<ValuesController>/Collection/5 CloseCollection
        [HttpDelete("Collection/{id}")]
        public void DeleteCollection( int id )
        {
            //call delete Collection in Database
        }
    }
}