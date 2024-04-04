using System.Diagnostics.Eventing.Reader;
using System.Net;
using System.Net.WebSockets;
using System.Text;

var builder = WebApplication.CreateBuilder(args);
builder.WebHost.UseUrls("https://localhost:44380/");
builder.Services.AddControllers();
var app = builder.Build();
app.UseWebSockets();



app.MapControllers();
await app.RunAsync();