using FlatBackend.Database;
using FlatBackend.DTOs;
using FlatBackend.Interfaces;
using FlatBackend.Websocket;
using Microsoft.Extensions.DependencyInjection;
using MongoDB.Driver.Core.Configuration;
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
string mongoConString = "";
if (builder.Environment.IsDevelopment())
{
    mongoConString = builder.Configuration.GetValue<string>("MONGODBCONNECTIONSTRING_DEBUG");
}
else
{
    mongoConString = builder.Configuration.GetValue<string>("MONGODBCONNECTIONSTRING");
}

builder.Services.AddSingleton<IMongoDBService>(new MongoDBService(mongoConString));
builder.Services.AddSingleton<IWebsocketManager>(new WebsocketManager(new MongoDBService(mongoConString)));

var app = builder.Build();
app.Logger.LogInformation($"MongoDbConnectionString: {mongoConString}");

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

var webSocketOptions = new WebSocketOptions
{
    KeepAliveInterval = TimeSpan.FromMinutes(0.5)
};
app.UseWebSockets(webSocketOptions);

//app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

await app.RunAsync();