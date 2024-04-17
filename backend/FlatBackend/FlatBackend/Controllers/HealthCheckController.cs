using Microsoft.AspNetCore.Mvc;

namespace FlatBackend.Controllers
{
    public class HealthCheckController : ControllerBase
    {
        [Route("healthcheck")]
        [HttpGet]
        public ActionResult HealthCheck()
        {
            return Ok("Hello World. It's me the flat backen. I'm fine. How are you?");
        }
    }
}