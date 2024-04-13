namespace FlatBackend
{
    public class Program
    {
        public static string DBConnectionString;

        public static void Main( string[] args )
        {
            var builder = WebApplication.CreateBuilder(args);
            builder.Services.AddControllers();
            builder.Services.AddEndpointsApiExplorer();
            builder.Services.AddSwaggerGen();

            //builder.WebHost.UseUrls("https://localhost:44380/");
            builder.Services.AddControllers();
            var app = builder.Build();
            DBConnectionString = "mongodb://mongodb:27017";
            if (app.Environment.IsDevelopment())
            {
                DBConnectionString = "mongodb://localhost:27017";
                app.UseSwagger();
                app.UseSwaggerUI();
            }

            var webSocketOptions = new WebSocketOptions
            {
                KeepAliveInterval = TimeSpan.FromMinutes(2)
            };
            app.UseWebSockets(webSocketOptions);

            //app.UseHttpsRedirection();

            //app.UseAuthorization();

            app.MapControllers();

            app.Run();
        }
    }
}