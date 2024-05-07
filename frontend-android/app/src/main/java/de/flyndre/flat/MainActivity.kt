package de.flyndre.flat

import android.Manifest
import android.content.Context
import android.content.Intent
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
import androidx.core.content.edit
import androidx.navigation.NavType
import de.flyndre.flat.services.ConnectionService
import de.flyndre.flat.services.TrackingService
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.google.android.gms.location.LocationServices
import de.flyndre.flat.composables.presetscreen.collectionareascreen.CollectionAreaScreen
import de.flyndre.flat.composables.creategroupscreen.CreateGroupScreen
import de.flyndre.flat.composables.creategroupscreen.CreateGroupScreenViewModel
import de.flyndre.flat.composables.initialscreen.InitialScreen
import de.flyndre.flat.composables.joinscreen.JoinScreen
import de.flyndre.flat.composables.joinscreen.JoinScreenViewModel
import de.flyndre.flat.composables.presetscreen.PresetScreen
import de.flyndre.flat.composables.presetscreen.PresetScreenViewModel
import de.flyndre.flat.composables.presetscreen.collectionareascreen.CollectionAreaScreenViewModel
import de.flyndre.flat.composables.trackingscreen.TrackingScreen
import de.flyndre.flat.composables.trackingscreen.TrackingScreenViewModel
import de.flyndre.flat.composables.trackingscreen.participantscreen.ParticipantScreen
import de.flyndre.flat.composables.trackingscreen.participantscreen.ParticipantScreenViewModel
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.interfaces.ILocationService
import de.flyndre.flat.interfaces.ITrackingService
import de.flyndre.flat.services.LocationService
import de.flyndre.flat.ui.theme.FlatTheme
import java.util.UUID

class MainActivity : ComponentActivity() {
    private lateinit var connectionService: IConnectionService
    private lateinit var locationService: ILocationService
    private lateinit var trackingService: ITrackingService
    private lateinit var db: AppDatabase
    private val userIdKey = "USERID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //request permissions
        requestLocationPermission()
        val preference = getPreferences(Context.MODE_PRIVATE)
        if (!preference.contains(userIdKey)) {
            preference.edit { putString(userIdKey, UUID.randomUUID().toString()) }
        }
        val userId = UUID.fromString(preference.getString(userIdKey, ""))
        connectionService =
            ConnectionService("https:flat.buhss.de/api/rest", "wss:flat.buhss.de/api/ws", userId)
        locationService = LocationService(
            1000,
            LocationServices.getFusedLocationProviderClient(this), this
        )

        trackingService = TrackingService(connectionService, locationService, 10000)
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "flat-database")
            .build()
        val participantScreenViewModel = ParticipantScreenViewModel(connectionService)
        val trackingScreenViewModel =
            TrackingScreenViewModel(db = db, trackingService, connectionService, participantScreenViewModel)
        val collectionAreaScreenViewModel = CollectionAreaScreenViewModel()
        val createGroupScreenViewModel = CreateGroupScreenViewModel(db = db)
        val presetScreenViewModel = PresetScreenViewModel(
            db = db,
            collectionAreaScreenViewModel = collectionAreaScreenViewModel,
            trackingScreenViewModel,
            connectionService
        )
        val joinScreenViewModel = JoinScreenViewModel(
            db = db,
            trackingScreenViewModel,
            connectionService = connectionService
        )

        setContent {
            FlatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppEntryPoint(
                        modifier = Modifier,
                        intent,
                        createGroupScreenViewModel,
                        presetScreenViewModel,
                        collectionAreaScreenViewModel,
                        joinScreenViewModel,
                        trackingScreenViewModel,
                        participantScreenViewModel,
                        trackingService,
                        userId
                    ) { x -> shareLink(x) }
                }
            }
        }

    }

    private fun requestLocationPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (!isGranted) {
                    finishAndRemoveTask()
                }
            }
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun shareLink(link:String){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)

    }
}

@Composable
fun AppEntryPoint(
    modifier: Modifier,
    intent: Intent,
    createGroupScreenViewModel: CreateGroupScreenViewModel,
    presetScreenViewModel: PresetScreenViewModel,
    collectionAreaScreenViewModel: CollectionAreaScreenViewModel,
    joinScreenViewModel: JoinScreenViewModel,
    trackingScreenViewModel: TrackingScreenViewModel,
    participantScreenViewModel: ParticipantScreenViewModel,
    trackingService: ITrackingService,
    userId: UUID,
    onShareLink: ((String)->Unit)
) {
    val navController = rememberNavController()
    var _startDestination = "initial"
    if (intent.action.equals(Intent.ACTION_VIEW)) {
        val url = intent.data
        if (url != null && url.pathSegments[0].equals("join")) {
            _startDestination = "join"
            joinScreenViewModel.updateJoinLink(url.toString())
        }
    }

    NavHost(navController = navController, startDestination = _startDestination) {
        composable("initial") {
            InitialScreen(
                modifier = modifier,
                onNavigateToJoinScreen = { navController.navigate("join") },
                onNavigateToCreateGroupScreen = { navController.navigate("creategroup") },
                onLukasBUHtton = {
                    if (trackingService.isTracking) {
                        trackingService.stopTracking()
                    } else {
                        trackingService.startTracking()
                    };Log.d("Button", "Pressed!")
                })
        }
        composable("join") {
            JoinScreen(
                modifier = modifier,
                onNavigateToInitialScreen = { navController.navigate("initial") },
                onNavigateToTrackingScreen = { navController.navigate("tracking") },
                joinScreenViewModel = joinScreenViewModel
            )
        }
        composable("creategroup") {
            CreateGroupScreen(
                modifier = modifier,
                onNavigateToInitialScreen = { navController.navigate("initial") },
                onNavigateToNewPresetScreen = { navController.navigate("newpreset") },
                navController = navController,
                createGroupScreenViewModel = createGroupScreenViewModel
            )
        }
        composable("newpreset") {
            presetScreenViewModel.newEmptyPreset()
            PresetScreen(
                presetId = null,
                navController = navController,
                topBarText = "New Preset",
                onNavigateToCreateGroupScreen = { navController.navigate("creategroup") },
                onNavigateToTrackingScreen = { navController.navigate("tracking") },
                presetScreenViewModel = presetScreenViewModel
            )
        }
        composable(
            "editpreset/{presetId}",
            arguments = listOf(navArgument("presetId") { type = NavType.LongType })
        ) { backStackEntry ->
            val presetId = backStackEntry.arguments?.getLong("presetId")
            if (presetId!! != 0.toLong()) {//0 is the way to signalize to the navController that no new values need to be loaded from database
                presetScreenViewModel.setPresetId(presetId = presetId)
            }
            PresetScreen(
                presetId = presetId,
                navController = navController,
                topBarText = "Edit Preset",
                onNavigateToCreateGroupScreen = { navController.navigate("creategroup") },
                onNavigateToTrackingScreen = { navController.navigate("tracking") },
                presetScreenViewModel = presetScreenViewModel
            )
        }
        composable("collectionarea") {
            CollectionAreaScreen(
                navController = navController,
                collectionAreaScreenViewModel = collectionAreaScreenViewModel
            )
        }
        composable("tracking") {
            TrackingScreen(
                trackingScreenViewModel = trackingScreenViewModel,
                onNavigateToInitialScreen = { navController.navigate("initial") },
                onNavigateToParticipantScreen = { navController.navigate("participant") },
                onShareLink = onShareLink,
                userId = userId
            )
        }
        composable("participant") {
            ParticipantScreen(
                participantScreenViewModel = participantScreenViewModel,
                onNavigateToTrackingScreen = { navController.navigate("tracking") })
        }
    }
}


