package com.unchil.searchcamp.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.unchil.searchcamp.R
import com.unchil.searchcamp.data.GoCampingService
import com.unchil.searchcamp.data.RepositoryProvider
import com.unchil.searchcamp.db.LocalSearchCampDB
import com.unchil.searchcamp.db.SearchCampDB
import com.unchil.searchcamp.model.SnackBarChannelType
import com.unchil.searchcamp.model.snackbarChannelList
import com.unchil.searchcamp.navigation.SearchCampDestinations
import com.unchil.searchcamp.navigation.navigateTo
import com.unchil.searchcamp.shared.LocalPermissionsManager
import com.unchil.searchcamp.shared.PermissionsManager
import com.unchil.searchcamp.shared.checkInternetConnected
import com.unchil.searchcamp.shared.view.CheckPermission
import com.unchil.searchcamp.shared.view.PermissionRequiredCompose
import com.unchil.searchcamp.ui.theme.SearchCampTheme
import com.unchil.searchcamp.viewmodel.SearchScreenViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow

@SuppressLint("UnrememberedMutableState", "MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
){


    val permissions = listOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val multiplePermissionsState = rememberMultiplePermissionsState( permissions)

    CheckPermission(multiplePermissionsState = multiplePermissionsState)

    var isGranted by mutableStateOf(true)

    permissions.forEach { chkPermission ->
        isGranted =   isGranted && multiplePermissionsState.permissions.find { it.permission == chkPermission  }?.status?.isGranted ?: false
    }

    PermissionRequiredCompose(
        isGranted = isGranted,
        multiplePermissions = permissions
    ) {


        val context = LocalContext.current
        val db = LocalSearchCampDB.current


        val viewModel = remember {
            SearchScreenViewModel(
                repository = RepositoryProvider.getRepository().apply { database = db })
        }

        val fusedLocationProviderClient = remember {
            LocationServices.getFusedLocationProviderClient(context)
        }
        var isConnect by remember { mutableStateOf(context.checkInternetConnected()) }

        LaunchedEffect(key1 = isConnect) {
            while (!isConnect) {
                delay(500)
                isConnect = context.checkInternetConnected()
            }
        }

        val currentListDataCntStateFlow = viewModel.currentListDataCntStateFlow.collectAsState()

        val channel = remember { Channel<Int>(Channel.CONFLATED) }

        val snackBarHostState = remember { SnackbarHostState() }

        var administrativeDistrictSiDoCode by remember {
            mutableStateOf("")
        }

        var administrativeDistrictSiGunGu by remember {
            mutableStateOf("")
        }

        val searchTitle:MutableState<String?> = remember {
            mutableStateOf(null)
        }

        LaunchedEffect(key1 = viewModel){
            if(isConnect) {

                fusedLocationProviderClient.lastLocation.addOnCompleteListener(context.mainExecutor) { task ->
                    if (task.isSuccessful && task.result != null) {
                        viewModel.onEvent(
                            SearchScreenViewModel.Event.RecvGoCampingData(
                                GoCampingService.NEARCAMPSITE,
                                mapX = task.result.longitude.toString(),
                                mapY = task.result.latitude.toString()
                            )
                        )
                    }
                }

                viewModel.onEvent(
                    SearchScreenViewModel.Event.RecvGoCampingData(
                        GoCampingService.CAMPSITE
                    )
                )

            }

            viewModel.effect.collect{
                when(it){
                    is SearchScreenViewModel.Effect.QueryResultCount -> {
                        channel.trySend(snackbarChannelList.first {
                            it.channelType == SnackBarChannelType.SEARCH_RESULT
                        }.channel)
                    }
                }
            }

        }

        var isVisibleResult by remember { mutableStateOf(false)}


        LaunchedEffect(channel) {

            channel.receiveAsFlow().collect { index ->

                val channelData = snackbarChannelList.first {
                    it.channel == index
                }

                //----------
                val message = when (channelData.channelType) {
                    SnackBarChannelType.SEARCH_RESULT -> {
                        val resultString =  if(currentListDataCntStateFlow.value  == 0){"가 존재하지 않습니다."} else {" [${currentListDataCntStateFlow.value}]"}
                        context.resources.getString( channelData.message) +   resultString
                    }
                    else -> {
                        context.resources.getString( channelData.message)
                    }
                }


                val actionLabel = if(channelData.channelType == SnackBarChannelType.SEARCH_RESULT
                    && currentListDataCntStateFlow.value == 0){""} else {channelData.actionLabel }
                //----------


                val result = snackBarHostState.showSnackbar(
                    message = message,
                    actionLabel =  actionLabel,
                    withDismissAction = channelData.withDismissAction,
                    duration = channelData.duration
                )
                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        //     hapticProcessing()
                        //----------
                        when (channelData.channelType) {
                            SnackBarChannelType.SEARCH_RESULT -> {
                                   if(currentListDataCntStateFlow.value > 0){
                                       //-> nav
                              //         isVisibleResult = true

                                       navController.navigateTo(SearchCampDestinations.resultScreen.createRoute(
                                           administrativeDistrictSiDoCode = administrativeDistrictSiDoCode,
                                           administrativeDistrictSiGunGu = administrativeDistrictSiGunGu,
                                           searchTitle = searchTitle.value
                                       ))



                                   }
                            }
                            else -> {}
                        }
                        //----------
                    }

                    SnackbarResult.Dismissed -> {
                        //      hapticProcessing()

                    }
                }
            }
        }

        val onSearchEventHandler:(siDoCode:String, siGunGuName:String, siteName:String?)->Unit
                = {  siDoCode, siGunGuName, siteName ->
                        administrativeDistrictSiDoCode = siDoCode
                        administrativeDistrictSiGunGu = siGunGuName
                        searchTitle.value = siteName

                        viewModel.onEvent(
                            SearchScreenViewModel.Event.Search(
                                siDoCode,
                                siGunGuName,
                                siteName
                            )
                        )
                }




        Box(
            modifier = Modifier.fillMaxSize()
        )  {

            Image(
                painter = painterResource(R.drawable.forest),
                contentDescription = "bg",
                modifier = Modifier.fillMaxSize(),
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
            )



            SearchCampView(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 10.dp),
                onSearchEventHandler = onSearchEventHandler
            )


            Box(modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 10.dp),
                contentAlignment = Alignment.Center,
            ){
                SnackbarHost(hostState = snackBarHostState){
                    Snackbar(
                        snackbarData = it,
                        modifier = Modifier,
                        shape = ShapeDefaults.ExtraSmall,
                        containerColor = Color.Yellow,
                        contentColor = Color.Black,
                        actionColor =Color.Red,

                        dismissActionContentColor = Color.LightGray
                    )
                }
            }


        }


    }
}




@Preview
@Composable
fun PrevSearchScreen(){

    val context = LocalContext.current
    val permissionsManager = PermissionsManager()
    val searchCampDB = SearchCampDB.getInstance(context.applicationContext)



    SearchCampTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CompositionLocalProvider(LocalPermissionsManager provides permissionsManager) {
                CompositionLocalProvider(LocalSearchCampDB provides searchCampDB) {
                    SearchScreen(navController = rememberNavController())
                }
            }
        }
    }


}