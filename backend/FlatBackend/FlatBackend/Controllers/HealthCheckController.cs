using Microsoft.AspNetCore.Mvc;

namespace FlatBackend.Controllers
{
    public class HealthCheckController : ControllerBase
    {
        [Route("healthcheck")]
        public ActionResult HealthCheck()
        {
            return Ok("Hello World. MongoConnStr: "+ Environment.GetEnvironmentVariable("MONGODBCONNECTIONSTRING"));
        }
    }
}
