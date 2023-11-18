package com.unchil.searchcamp.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BedtimeOff
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.ModeOfTravel
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.widgets.ScaleBar
import com.unchil.gismemo.view.MapTypeMenuList
import com.unchil.gismemo.view.getDesc
import com.unchil.gismemo.view.toLatLng
import com.unchil.searchcamp.LocalUsableHaptic
import com.unchil.searchcamp.R
import com.unchil.searchcamp.data.GoCampingService
import com.unchil.searchcamp.data.RepositoryProvider
import com.unchil.searchcamp.db.LocalSearchCampDB
import com.unchil.searchcamp.db.entity.CampSite_TBL
import com.unchil.searchcamp.model.SiteDefaultData
import com.unchil.searchcamp.shared.checkInternetConnected
import com.unchil.searchcamp.shared.view.CheckPermission
import com.unchil.searchcamp.shared.view.PermissionRequiredCompose
import com.unchil.searchcamp.viewmodel.GoogleMapViewModel
import com.unchil.searchcamp.viewmodel.ResultScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState", "MissingPermission")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class,
    MapsComposeExperimentalApi::class
)
@Composable
fun ResultMapScreen(
    navController: NavHostController,
    administrativeDistrictSiDoCode:String,
    administrativeDistrictSiGunGu:String,
    searchTitle:String? = null
){


    val permissions = listOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val multiplePermissionsState = rememberMultiplePermissionsState( permissions)

    CheckPermission(multiplePermissionsState = multiplePermissionsState)

    var isGranted by mutableStateOf(true)

    val  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }

    permissions.forEach { chkPermission ->
        isGranted =   isGranted && multiplePermissionsState.permissions.find { it.permission == chkPermission  }?.status?.isGranted ?: false
    }

    PermissionRequiredCompose(
        isGranted = isGranted,
        multiplePermissions = permissions
    ) {

        val context = LocalContext.current
        var isConnect by remember { mutableStateOf(context.checkInternetConnected()) }

        LaunchedEffect(key1 = isConnect) {
            while (!isConnect) {
                delay(500)
                isConnect = context.checkInternetConnected()
            }


        }


        val configuration = LocalConfiguration.current
        var isPortrait by remember { mutableStateOf(false) }
        isPortrait = when (configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                true
            }
            else ->{
                false
            }
        }

        var columnWidth by remember { mutableStateOf(1f) }
        var bottomPadding by remember { mutableStateOf(0.dp) }

        when (configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                isPortrait = true
                columnWidth = 9f
                bottomPadding = 70.dp
            }
            else ->{
                isPortrait = false
                columnWidth = 0.5f
                bottomPadding = 10.dp
            }
        }



        val db = LocalSearchCampDB.current
        val isUsableHaptic = LocalUsableHaptic.current
        val hapticFeedback = LocalHapticFeedback.current
        val coroutineScope = rememberCoroutineScope()

        var isDarkMode by remember { mutableStateOf(false) }

        /*
        // Event Handler 와 충돌
        var isHapticProcessing by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = isHapticProcessing) {
            if (isHapticProcessing) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isHapticProcessing = false
            }
        }

         */


        fun hapticProcessing(){
            if(isUsableHaptic){
                coroutineScope.launch {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            }
        }



        val fusedLocationProviderClient = remember {
            LocationServices.getFusedLocationProviderClient(context)
        }

        val viewModel = remember {
            ResultScreenViewModel(
                repository = RepositoryProvider.getRepository().apply { database = db },
                administrativeDistrictSiDoCode,
                administrativeDistrictSiGunGu,
                searchTitle
            )
        }
        val currentSiteDataList = viewModel.currentListDataStateFlow.collectAsState()

        var isSetCurrentLocation by remember { mutableStateOf(false) }

        var currentLocation by remember {
            mutableStateOf(LatLng(0.0,0.0))
        }


        LaunchedEffect( key1 =  currentLocation){
            if( currentLocation == LatLng(0.0,0.0)) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener( context.mainExecutor) { task ->
                    if (task.isSuccessful && task.result != null ) {
                        currentLocation = task.result.toLatLng()
                        isSetCurrentLocation = true
                    }
                }
            }else {
                isSetCurrentLocation = true
            }
        }

        var isGoCampSiteLocation by remember { mutableStateOf(false) }

        val  campSiteBound: (List<CampSite_TBL>)-> LatLngBounds = {

            val latitudes = it.map {
                it.mapY.toDouble()
            }

            val longitude = it.map {
                it.mapX.toDouble()
            }

            LatLngBounds(
                LatLng((latitudes.min()), longitude.min()),  // SW bounds
                LatLng((latitudes.max()), longitude.max()) // NE bounds
            )

        }


        // No ~~~~ remember
        val markerState =  MarkerState( position = currentLocation )
        val defaultCameraPosition =  CameraPosition.fromLatLngZoom( currentLocation, 12f)
        val cameraPositionState =  CameraPositionState(position = defaultCameraPosition)


        val isUsableDarkMode by remember { mutableStateOf(false) }

        var mapProperties by remember {
            mutableStateOf(
                MapProperties(
                    mapType = MapType.NORMAL,
                    isMyLocationEnabled = true,
                    mapStyleOptions = if(isUsableDarkMode) {
                        MapStyleOptions.loadRawResourceStyle(
                            context,
                            R.raw.mapstyle_night
                        )
                    } else { null }
                )
            )
        }

        val uiSettings by remember {
            mutableStateOf(
                MapUiSettings(
                    compassEnabled = true,
                    myLocationButtonEnabled = true,
                    mapToolbarEnabled = true,
                    zoomControlsEnabled = false

                )
            )
        }


        val onMapLongClickHandler: (LatLng) -> Unit = {
            //       markerState.position = it
            //        cameraPositionState =   CameraPositionState(position = CameraPosition.fromLatLngZoom(it, 12f))
        }

        var mapTypeIndex by rememberSaveable { mutableStateOf(0) }

        var isVisibleSiteDefaultView by remember { mutableStateOf(false) }
        var currentSiteDefaultData: SiteDefaultData? by remember {
            mutableStateOf(null)
        }

        val sheetState = SheetState(
            skipPartiallyExpanded = false,
            density = LocalDensity.current,
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )

        val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
        val currentCampSiteData: MutableState<SiteDefaultData?> = remember { mutableStateOf(null) }
        val sheetPeekHeightValue by remember { mutableStateOf(0.dp) }

        val dragHandlerAction:()->Unit = {
            coroutineScope.launch {
                if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                    scaffoldState.bottomSheetState.expand()
                } else {
                    scaffoldState.bottomSheetState.partialExpand()
                }
            }
        }


        var isFirstTab by mutableStateOf(true)

        val onClickHandler: (data: SiteDefaultData) -> Unit = {
            hapticProcessing()
            currentCampSiteData.value = it
            isFirstTab = true
            dragHandlerAction.invoke()
        }


        val onClickPhotoHandler: (data: SiteDefaultData) -> Unit = {
            hapticProcessing()
            currentCampSiteData.value = it
            if (isConnect) {
                viewModel.onEvent(
                    ResultScreenViewModel.Event.RecvGoCampingData(
                        servicetype = GoCampingService.SITEIMAGE,
                        contentId = it.contentId
                    )
                )
            }
            isFirstTab = false
            dragHandlerAction.invoke()
        }




        BottomSheetScaffold(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            scaffoldState = scaffoldState,
            sheetPeekHeight = sheetPeekHeightValue,
            sheetShape = ShapeDefaults.Small,
            sheetDragHandle = {
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth()
                        .background(color = Color.LightGray.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier
                            .scale(1f)
                            .clickable {
                                dragHandlerAction.invoke()
                            },
                        imageVector =
                        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded)
                            Icons.Outlined.KeyboardArrowDown
                        else Icons.Outlined.KeyboardArrowUp,
                        contentDescription = "SiteDetailScreen",
                    )
                }
            },
            sheetContent = {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(1f),
                    contentAlignment = Alignment.Center

                ) {
                    currentCampSiteData.value?.let {
                        if (isFirstTab) {
                            SiteIntroductionView(it)
                        } else {
                            SiteImagePagerView(viewModel = viewModel)
                        }
                    }
                }
            }
        ) { innerPadding ->

                Box(
                    modifier = Modifier.padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {


                    GoogleMap(
                        cameraPositionState = cameraPositionState,
                        properties = mapProperties,
                        uiSettings = uiSettings,
                        onMapLongClick = onMapLongClickHandler,
                        onMapClick = {
                            isVisibleSiteDefaultView = false }
                    ) {

                        Marker(
                            state = markerState,
                            title = "lat/lng:(${String.format("%.5f", markerState.position.latitude)},${String.format("%.5f", markerState.position.longitude)})",
                        )


                        MapEffect(key1 = isGoCampSiteLocation, key2 = isSetCurrentLocation){ googleMap ->

                            if(isSetCurrentLocation){

                                val cameraPosition = CameraPosition.Builder()
                                    .target(campSiteBound(currentSiteDataList.value).center)
                                    .zoom(10f)
                                    .bearing(0f)
                                    .tilt(30f)
                                    .build()

                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                                isSetCurrentLocation = false
                            }

                            if(isGoCampSiteLocation) {

                                val cameraPosition = CameraPosition.Builder()
                                    .target(campSiteBound(currentSiteDataList.value).center)
                                    .zoom(10f)
                                    .bearing(0f)
                                    .tilt(30f)
                                    .build()

                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                                isGoCampSiteLocation = false
                            }

                        }



                        currentSiteDataList.value.forEach { it ->

                            val state = MarkerState(position = LatLng(it.mapY.toDouble(), it.mapX.toDouble()))
                            Marker(
                                state = state,
                                title =  it.facltNm,
                                onClick = {marker ->
                                    isVisibleSiteDefaultView = true
                                    currentSiteDefaultData = CampSite_TBL.toSiteDefaultData(it)
                                    false
                                },
                                onInfoWindowClick = {
                                    isVisibleSiteDefaultView = false
                                }
                            )

                        }

                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 10.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
                                shape = ShapeDefaults.ExtraSmall
                            )
                    ) {


                        IconButton(
                            onClick = {
                                hapticProcessing()
                                isDarkMode = !isDarkMode
                                if (isDarkMode) {
                                    mapProperties = mapProperties.copy(
                                        mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                                            context,
                                            R.raw.mapstyle_night
                                        )
                                    )
                                } else {
                                    mapProperties = mapProperties.copy(mapStyleOptions = null)
                                }
                            }
                        ) {
                            Icon(
                                modifier = Modifier.scale(1f),
                                imageVector = if (isDarkMode) Icons.Outlined.BedtimeOff else Icons.Outlined.DarkMode,
                                contentDescription = "DarkMode",
                            )
                        }


                        IconButton(
                            onClick = {
                                hapticProcessing()
                                isGoCampSiteLocation = true
                            }
                        ) {
                            Icon(
                                modifier = Modifier.scale(1f),
                                imageVector = Icons.Outlined.ModeOfTravel,
                                contentDescription = "GoCampSiteLocationl",
                            )
                        }


                    }


                    ScaleBar(
                        modifier = Modifier
                            .padding(bottom = 30.dp)
                            .align(Alignment.BottomStart),
                        cameraPositionState = cameraPositionState
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 10.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
                                shape = ShapeDefaults.ExtraSmall
                            )
                    ) {
                        MapTypeMenuList.forEachIndexed { index, it ->
                            AnimatedVisibility(
                                visible = true,
                            ) {
                                IconButton(
                                    onClick = {
                                        hapticProcessing()
                                        val mapType = MapType.values().first { mapType ->
                                            mapType.name == it.name
                                        }
                                        mapProperties = mapProperties.copy(mapType = mapType)
                                        mapTypeIndex = index

                                    }) {

                                    Icon(
                                        imageVector = it.getDesc().first,
                                        contentDescription = it.name,
                                    )
                                }
                            }
                        }
                    }

                    currentSiteDefaultData?.let {
                        AnimatedVisibility(visible = isVisibleSiteDefaultView) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(columnWidth)
                                    .padding(bottom = bottomPadding)
                                    .padding(horizontal = 20.dp)
                                    .align(Alignment.BottomCenter),
                                contentAlignment = Alignment.Center,
                            ) {

                                SiteDefaultView(
                                    siteData = it,
                                    onClick = {

                                        onClickHandler.invoke(it)
                                    },
                                    onClickPhoto = {

                                      onClickPhotoHandler.invoke(it)
                                    },
                                    onLongClick = {

                                        onClickHandler.invoke(it)
                                    }
                                )


                            }

                        }
                    }






                }



        }//BottomSheetScaffold


    }// permission
}