package de.flyndre.flat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import de.flyndre.flat.services.ConnectionService
import de.flyndre.flat.services.TrackingService
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.google.android.gms.location.LocationServices
import de.flyndre.flat.composables.presetscreen.collectionareascreen.CollectionAreaScreen
import de.flyndre.flat.composables.creategroupscreen.CreateGroupScreen
import de.flyndre.flat.composables.creategroupscreen.CreateGroupScreenViewModel
import de.flyndre.flat.composables.creategroupscreen.CreateGroupScreenViewModelFactory
import de.flyndre.flat.composables.initialscreen.InitialScreen
import de.flyndre.flat.composables.joinscreen.JoinScreen
import de.flyndre.flat.composables.joinscreen.JoinScreenViewModel
import de.flyndre.flat.composables.joinscreen.JoinScreenViewModelFactory
import de.flyndre.flat.composables.presetscreen.PresetScreen
import de.flyndre.flat.composables.presetscreen.PresetScreenViewModel
import de.flyndre.flat.composables.presetscreen.PresetScreenViewModelFactory
import de.flyndre.flat.composables.presetscreen.collectionareascreen.CollectionAreaScreenViewModel
import de.flyndre.flat.composables.settingScreen.CreateSettingScreenViewModelFactory
import de.flyndre.flat.composables.settingScreen.SettingScreen
import de.flyndre.flat.composables.settingScreen.SettingScreenViewModel
import de.flyndre.flat.composables.statisticsscreen.CreateStatisticsScreenViewModelFactory
import de.flyndre.flat.composables.statisticsscreen.StatisticsScreen
import de.flyndre.flat.composables.statisticsscreen.StatisticsScreenViewModel
import de.flyndre.flat.composables.trackingscreen.TrackingScreen
import de.flyndre.flat.composables.trackingscreen.TrackingScreenViewModel
import de.flyndre.flat.composables.trackingscreen.TrackingScreenViewModelFactory
import de.flyndre.flat.composables.trackingscreen.assignmentscreen.AssignmentScreen
import de.flyndre.flat.composables.trackingscreen.assignmentscreen.AssignmentScreenViewModel
import de.flyndre.flat.composables.trackingscreen.assignmentscreen.ParticipantScreenViewModelFactory
import de.flyndre.flat.composables.trackingscreen.participantscreen.CreateParticipantScreenViewModelFactory
import de.flyndre.flat.composables.trackingscreen.participantscreen.ParticipantScreen
import de.flyndre.flat.composables.trackingscreen.participantscreen.ParticipantScreenViewModel
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.interfaces.ILocationService
import de.flyndre.flat.interfaces.ISettingService
import de.flyndre.flat.interfaces.ITrackingService
import de.flyndre.flat.services.LocationService
import de.flyndre.flat.services.SettingService
import de.flyndre.flat.ui.theme.FlatTheme
import java.util.UUID

class MainActivity : ComponentActivity() {
    private lateinit var settingService: ISettingService
    private lateinit var connectionService: IConnectionService
    private lateinit var locationService: ILocationService
    private lateinit var trackingService: ITrackingService
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //request permissions
        requestLocationPermission()
        settingService = SettingService(getPreferences(MODE_PRIVATE))
        connectionService =
            ConnectionService(settingService)
        locationService = LocationService(
            1000,
            LocationServices.getFusedLocationProviderClient(this), this
        )
        trackingService = TrackingService(connectionService, locationService, 10000)
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "flat-database")
            .build()

        setContent {
            FlatTheme {
                // A surface container using the 'background' color from the theme
                val owner = LocalViewModelStoreOwner.current
                owner?.let {
                    val assignmentScreenViewModel: AssignmentScreenViewModel = viewModel(
                        it,
                        "ParticipantScreenViewModel",
                        ParticipantScreenViewModelFactory(connectionService)
                    )
                    val participantScreenViewModel: ParticipantScreenViewModel = viewModel(
                        it,
                        "ParticipantScreenViewModel",
                        CreateParticipantScreenViewModelFactory(connectionService)
                    )
                    val trackingScreenViewModel:TrackingScreenViewModel = viewModel(
                        it,
                        "TrackingScreenViewModel",
                        TrackingScreenViewModelFactory(
                            trackingService,
                            connectionService,
                            assignmentScreenViewModel,
                            participantScreenViewModel,
                            settingService))
                    val collectionAreaScreenViewModel:CollectionAreaScreenViewModel = viewModel()
                    val presetScreenViewModel: PresetScreenViewModel = viewModel(
                        it,
                        "PresetScreenViewModel",
                        PresetScreenViewModelFactory(
                            db,
                            collectionAreaScreenViewModel,
                            trackingScreenViewModel,
                            connectionService
                        )
                    )
                    val createGroupScreenViewModel:CreateGroupScreenViewModel = viewModel(
                        it,
                        "CreateGroupScreenViewModel",
                        CreateGroupScreenViewModelFactory(db,
                            presetScreenViewModel)
                    )
                    val joinScreenViewModel:JoinScreenViewModel = viewModel(
                        it,
                        "JoinScreenViewModel",
                        JoinScreenViewModelFactory(
                            db = db,
                            settingService,
                            trackingScreenViewModel,
                            connectionService = connectionService
                        )
                    )
                    val settingScreenViewModel: SettingScreenViewModel = viewModel(
                        it,
                        "SettingScreenViewModel",
                        CreateSettingScreenViewModelFactory(
                            settingService
                        )
                    )
                    val statisticsScreenViewModel: StatisticsScreenViewModel = viewModel(
                        it,
                        "StatisticsScreenViewModel",
                        CreateStatisticsScreenViewModelFactory()
                    )

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
                            assignmentScreenViewModel,
                            settingScreenViewModel,
                            statisticsScreenViewModel,
                            participantScreenViewModel,
                            settingService.getClientId()
                        ) { x -> shareLink(x) }
                    }
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
    assignmentScreenViewModel: AssignmentScreenViewModel,
    settingScreenViewModel: SettingScreenViewModel,
    statisticsScreenViewModel: StatisticsScreenViewModel,
    participantScreenViewModel: ParticipantScreenViewModel,
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
                onNavigateToSettingScreen = {navController.navigate("settings")}
            )
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
                onNavigateToPresetScreen = { navController.navigate("preset") },
                createGroupScreenViewModel = createGroupScreenViewModel
            )
        }
        composable("preset") {
            PresetScreen(
                navController = navController,
                topBarText = "Edit Preset",
                onNavigateToCreateGroupScreen = { navController.navigate("creategroup") },
                onNavigateToTrackingScreen = { navController.navigate("tracking") },
                onNavigateToCollectionAreaScreen = { navController.navigate("collectionarea") },
                presetScreenViewModel = presetScreenViewModel
            )
        }
        composable("collectionarea") {
            CollectionAreaScreen(
                onNavigateToPresetScreen = {navController.navigate("preset")},
                collectionAreaScreenViewModel = collectionAreaScreenViewModel
            )
        }
        composable("tracking") {
            TrackingScreen(
                trackingScreenViewModel = trackingScreenViewModel,
                onNavigateToInitialScreen = { navController.navigate("initial") },
                onNavigateToAssignmentScreen = { navController.navigate("assignment") },
                onNavigateToParticipantScreen = { navController.navigate("participant") },
                onShareLink = onShareLink
            )
        }
        composable("assignment") {
            AssignmentScreen(
                assignmentScreenViewModel = assignmentScreenViewModel,
                onNavigateToTrackingScreen = { navController.navigate("tracking") })
        }
        composable("settings"){
            SettingScreen(onNavigateToInitialScreen = { navController.navigate("initial") },
                settingScreenViewModel = settingScreenViewModel
            )
        }
        composable("statistics"){
            StatisticsScreen(
                onNavigateToInitialScreen = { navController.navigate("initial") },
                statisticsScreenViewModel = statisticsScreenViewModel
            )
        }
        composable("participant"){
            ParticipantScreen(
                onNavigateToTrackingScreen = { navController.navigate("tracking") },
                participantScreenViewModel = participantScreenViewModel
            )
        }
    }
}


