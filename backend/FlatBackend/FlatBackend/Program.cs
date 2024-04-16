using FlatBackend.Database;
using FlatBackend.Interfaces;
using System.Diagnostics.Eventing.Reader;
using System.Net;
using System.Net.WebSockets;
using System.Runtime.CompilerServices;
using System.Text;

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

//builder.WebHost.UseUrls("*.44381/");
builder.Services.AddControllers();

var mongoConString = builder.Configuration.GetValue<string>("MONGODBCONNECTIONSTRING");
builder.Services.AddSingleton<IMongoDBService>(new MongoDBService(mongoConString));

var app = builder.Build();
app.Logger.LogInformation($"MongoDbConnectionString: {mongoConString}");


if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

            var webSocketOptions = new WebSocketOptions
            {
                KeepAliveInterval = TimeSpan.FromMinutes(2)
            };
            app.UseWebSockets(webSocketOptions);

//app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

await app.RunAsync();