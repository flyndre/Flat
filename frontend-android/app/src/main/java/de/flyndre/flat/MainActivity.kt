package de.flyndre.flat

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import de.flyndre.flat.services.ConnectionService
import de.flyndre.flat.services.TrackingService
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import de.flyndre.flat.composables.collectionareascreen.CollectionAreaScreen
import de.flyndre.flat.composables.creategroupscreen.CreateGroupScreen
import de.flyndre.flat.composables.initialscreen.InitialScreen
import de.flyndre.flat.composables.joinscreen.JoinScreen
import de.flyndre.flat.composables.presetscreen.PresetScreen
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.ui.theme.FlatTheme
import io.github.dellisd.spatialk.geojson.Polygon
import io.github.dellisd.spatialk.geojson.Position

class MainActivity : ComponentActivity() {
    var connectionService = ConnectionService("https:10.0.2.2:44380/api/rest")
    var trakingService = TrackingService()
    lateinit var db: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //request permissions
        requestLocationPermission()

        Thread(Runnable {
            while(true){
                try {
                    connectionService.openCollection("Test", Polygon(listOf(
                        Position(0.0,0.0),
                        Position(0.0,1.0),
                        Position(1.1,1.1)
                    )))
                }catch (ex:Exception){
                    Log.e(this.toString(),ex.toString())
                }
                trakingService.startTracking()
                Thread.sleep(10000)
                trakingService.stopTracking()
            }
        }).start()

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "flat-database").build()

        setContent {
            FlatTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppEntryPoint(modifier = Modifier, db = db)
                }
            }
        }

    }

    private fun requestLocationPermission(){
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (!isGranted) {
                    finishAndRemoveTask()
                }
            }
        if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

@Composable
fun AppEntryPoint(modifier: Modifier, db: AppDatabase){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "initial") {
        composable("initial"){ InitialScreen(modifier = modifier, onNavigateToJoinScreen = {navController.navigate("join")}, onNavigateToCreateGroupScreen = {navController.navigate("creategroup")}, onLukasBUHtton = {})}
        composable("join"){JoinScreen(modifier = modifier, onNavigateToInitialScreen = {navController.navigate("initial")})}
        composable("creategroup"){CreateGroupScreen(modifier = modifier, db = db,  onNavigateToInitialScreen = {navController.navigate("initial")}, onNavigateToNewPresetScreen = {navController.navigate("newpreset")}, navController = navController)}
        composable("newpreset"){ PresetScreen(presetId = null, db = db, navController = navController, topBarText = "New Preset", onNavigateToCreateGroupScreen = {navController.navigate("creategroup")})}
        composable("editpreset/{presetId}", arguments = listOf(navArgument("presetId"){type = NavType.LongType})){ backStackEntry -> val presetId = backStackEntry.arguments?.getLong("presetId")
            PresetScreen(presetId = presetId, db = db, navController = navController, topBarText = "Edit Preset", onNavigateToCreateGroupScreen = { navController.navigate("creategroup") }) }
        composable("collectionarea/{presetId}", arguments = listOf(navArgument("presetId"){type = NavType.LongType})){ backStackEntry -> val presetId = backStackEntry.arguments!!.getLong("presetId")
            CollectionAreaScreen(presetId = presetId, db = db, navController = navController)}
    }
}