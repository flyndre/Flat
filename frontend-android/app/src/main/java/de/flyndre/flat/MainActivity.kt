package de.flyndre.flat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.flyndre.flat.services.ConnectionService
import de.flyndre.flat.services.TrackingService
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import de.flyndre.flat.composables.creategroupscreen.CreateGroupScreen
import de.flyndre.flat.composables.initialscreen.InitialScreen
import de.flyndre.flat.composables.joinscreen.JoinScreen
import de.flyndre.flat.ui.theme.FlatTheme
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    var connectionService = ConnectionService("https:10.0.2.2/ws")
    var trakingService = TrackingService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread(Runnable {
            while(true){
                trakingService.startTracking()
                Thread.sleep(10000)
                trakingService.stopTracking()
            }
        }).start()
        setContent {
            FlatTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppEntryPoint(modifier = Modifier)
                }
            }
        }

    }
}

@Composable
fun AppEntryPoint(modifier: Modifier){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "initial") {
        composable("initial"){ InitialScreen(modifier = modifier, onNavigateToJoinScreen = {navController.navigate("join")}, onNavigateToCreateGroupScreen = {navController.navigate("creategroup")})}
        composable("join"){JoinScreen(modifier = modifier, onNavigateToInitialScreen = {navController.navigate("initial")})}
        composable("creategroup"){CreateGroupScreen(modifier = modifier, onNavigateToInitialScreen = {navController.navigate("initial")})}
    }
}

/*@Composable
fun Map(){
    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = singapore),
            title = "Singapore",
            snippet = "Marker in Singapore"
        )
    }
}*/