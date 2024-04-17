using Microsoft.AspNetCore.Mvc;

namespace FlatBackend.Controllers
{
    [Route("api")]
    public class HealthCheckController : ControllerBase
    {
        [Route("healthcheck")]
        public ActionResult HealthCheck()
        {
            return Ok("Hello World. It's me the flat backen. I'm fine. How are you?");
        }
    }
}
