package de.flyndre.flat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import de.flyndre.flat.services.ConnectionService
import de.flyndre.flat.services.TrackingService
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import de.flyndre.flat.composables.creategroupscreen.CreateGroupScreen
import de.flyndre.flat.composables.initialscreen.InitialScreen
import de.flyndre.flat.composables.joinscreen.JoinScreen
import de.flyndre.flat.composables.presetscreen.PresetScreen
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.ui.theme.FlatTheme
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    var connectionService = ConnectionService("https:10.0.2.2/ws")
    var trakingService = TrackingService()
    lateinit var db: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread(Runnable {
            while(true){
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
}

@Composable
fun AppEntryPoint(modifier: Modifier, db: AppDatabase){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "initial") {
        composable("initial"){ InitialScreen(modifier = modifier, onNavigateToJoinScreen = {navController.navigate("join")}, onNavigateToCreateGroupScreen = {navController.navigate("creategroup")})}
        composable("join"){JoinScreen(modifier = modifier, onNavigateToInitialScreen = {navController.navigate("initial")})}
        composable("creategroup"){CreateGroupScreen(modifier = modifier, db = db,  onNavigateToInitialScreen = {navController.navigate("initial")}, onNavigateToNewPresetScreen = {navController.navigate("newpreset")}, navController = navController)}
        composable("newpreset"){ PresetScreen(presetId = null, db = db, topBarText = "New Preset", onNavigateToCreateGroupScreen = {navController.navigate("creategroup")})}
        composable("editpreset/{presetId}", arguments = listOf(navArgument("presetId"){type = NavType.IntType})){ backStackEntry -> val presetId = backStackEntry.arguments?.getInt("presetId")
            PresetScreen(presetId = presetId, db = db, topBarText = "Edit Preset", onNavigateToCreateGroupScreen = { navController.navigate("creategroup") })}
    }
}