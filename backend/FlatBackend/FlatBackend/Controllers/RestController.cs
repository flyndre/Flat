using Microsoft.AspNetCore.Mvc;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace FlatBackend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class RestController : ControllerBase
    {
        // GET: api/<ValuesController>
        //[HttpGet]
        //public IEnumerable<string> Get()
        //{
        //    return new string[] { "value1", "value2" };
        //}

        //// GET api/<ValuesController>/5
        //[HttpGet("{id}")]
        //public string Get( int id )
        //{
        //    return "value";
        //}

        // POST api/<ValuesController>
        //[HttpPost]
        //public void Post( [FromBody] string value )
        //{
        //}

        [HttpPost]
        public void PostOpenCollection( [FromBody] string value )
        {
            //call create Database and send Objekt back
        }

        // PUT api/<ValuesController>/5
        [HttpPut("Collection/{id}")]
        public void PutSetOrChangeAreaDivision( int id, [FromBody] string value )
        {
            //call Change or set AreaDivision in Database
        }

        // DELETE api/<ValuesController>/5 CloseCollection
        [HttpDelete("Collection/{id}")]
        public void DeleteCollection( int id )
        {
            //call delete Collection in Database
        }
    }
}