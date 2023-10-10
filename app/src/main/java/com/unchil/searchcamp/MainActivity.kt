package com.unchil.searchcamp

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.unchil.searchcamp.db.LocalSearchCampDB
import com.unchil.searchcamp.db.SearchCampDB
import com.unchil.searchcamp.navigation.SearchCampDestinations
import com.unchil.searchcamp.navigation.mainScreens
import com.unchil.searchcamp.shared.LocalPermissionsManager
import com.unchil.searchcamp.shared.PermissionsManager
import com.unchil.searchcamp.shared.checkInternetConnected
import com.unchil.searchcamp.shared.view.CheckPermission
import com.unchil.searchcamp.shared.view.PermissionRequiredCompose
import com.unchil.searchcamp.ui.theme.SearchCampTheme
import com.unchil.searchcamp.view.ResultScreen
import com.unchil.searchcamp.view.SearchScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


val LocalUsableHaptic = compositionLocalOf{ true }
val LocalUsableDarkMode = compositionLocalOf{ false }
val LocalUsableDynamicColor = compositionLocalOf{ false }

class MainActivity : ComponentActivity() {

    private val permissionsManager = PermissionsManager()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val context = LocalContext.current
            val searchCampDB = SearchCampDB.getInstance(context.applicationContext)
            var isConnect  by remember { mutableStateOf(context.checkInternetConnected()) }

            val coroutineScope = rememberCoroutineScope()
            val navController = rememberNavController()
            val currentBackStack by navController.currentBackStackEntryAsState()
            var selectedScreen by rememberSaveable { mutableStateOf(0) }

            LaunchedEffect(key1 = currentBackStack){
                val currentScreen = mainScreens.find {
                    it.route ==  currentBackStack?.destination?.route
                }
                selectedScreen =  mainScreens.indexOf(currentScreen)
            }


            LaunchedEffect(key1 = isConnect ){
                while(!isConnect) {
                    delay(500)
                    isConnect = context.checkInternetConnected()
                }
            }


            SearchCampTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {

                    CompositionLocalProvider(LocalPermissionsManager provides permissionsManager) {
                        CompositionLocalProvider(LocalSearchCampDB provides searchCampDB) {

                            Box(modifier = Modifier.fillMaxSize()) {
                                if (isConnect) {
                                    NavHost(
                                        navController = navController,
                                        startDestination = SearchCampDestinations.searchScreen.route
                                    ) {

                                        composable(
                                            route = SearchCampDestinations.searchScreen.route
                                        ){
                                            SearchScreen(navController = navController)
                                        }

                                        composable(
                                            route = SearchCampDestinations.resultScreen.route ,
                                            arguments = listOf(
                                                navArgument(SearchCampDestinations.ARG_NAME_SiDoCode){
                                                    nullable = false
                                                    type = NavType.StringType },
                                                navArgument(SearchCampDestinations.ARG_NAME_SiGunGu){
                                                    nullable = false
                                                    type = NavType.StringType },
                                                navArgument(SearchCampDestinations.ARG_NAME_SearchTitle){
                                                    nullable = true
                                                    type = NavType.StringType }
                                            )
                                        ){
                                            ResultScreen(
                                                navController = navController,
                                                administrativeDistrictSiDoCode = SearchCampDestinations.getSiDoCodeFromArgs(it.arguments),
                                                administrativeDistrictSiGunGu = SearchCampDestinations.getSiGunGuFromArgs(it.arguments),
                                                searchTitle = SearchCampDestinations.getSearchTitleFromArgs(it.arguments)
                                                )
                                        }

                                    }

                                } else {
                                    ChkNetWork(  onCheckState = {   coroutineScope.launch {   isConnect = checkInternetConnected() } }  )
                                }
                            }

                        }
                    }

                }
            }


        }
    }
}






@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChkNetWork(
    onCheckState:()->Unit
){

    val context = LocalContext.current

    val permissions = listOf(Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE)
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions)
    CheckPermission(multiplePermissionsState = multiplePermissionsState)

    var isGranted by mutableStateOf(true)

    val isUsableDarkMode = LocalUsableDarkMode.current
    val colorFilter: ColorFilter? = if(isUsableDarkMode){
        ColorFilter.tint(Color.LightGray, blendMode = BlendMode.Darken)
    }else {
        null
    }

    permissions.forEach { chkPermission ->
        isGranted = isGranted && multiplePermissionsState.permissions.find { it.permission == chkPermission }?.status?.isGranted
                ?: false
    }

    PermissionRequiredCompose(
        isGranted = isGranted,
        multiplePermissions = permissions
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 60.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                text = context.resources.getString(R.string.app_name)
            )

            Image(
                painter =  painterResource(R.drawable.baseline_wifi_off_black_48),
                modifier = Modifier
                    .clip(ShapeDefaults.Medium)
                    .width(160.dp)
                    .height(160.dp),
                contentDescription = "not Connected",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                colorFilter = colorFilter
            )


            Button(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp),
                onClick = {
                    onCheckState()
                }
            ) {
                Text(context.resources.getString(R.string.chkNetWork_msg))
            }


        }


    }

}

