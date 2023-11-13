package com.unchil.gismemo.view

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.compose.widgets.ScaleBar
import com.unchil.searchcamp.data.RepositoryProvider
import com.unchil.searchcamp.db.LocalSearchCampDB
import com.unchil.searchcamp.db.entity.CampSite_TBL
import com.unchil.searchcamp.model.SiteDefaultData
import com.unchil.searchcamp.shared.LocalPermissionsManager
import com.unchil.searchcamp.shared.PermissionsManager
import com.unchil.searchcamp.shared.view.CheckPermission
import com.unchil.searchcamp.shared.view.PermissionRequiredCompose
import com.unchil.searchcamp.shared.view.PermissionRequiredComposeFuncName
import com.unchil.searchcamp.ui.theme.SearchCampTheme
import com.unchil.searchcamp.view.SiteDefaultView
import com.unchil.searchcamp.viewmodel.GoogleMapViewModel


enum class MapTypeMenu {
    NORMAL,TERRAIN,HYBRID
}

val MapTypeMenuList = listOf(
    MapTypeMenu.NORMAL,
    MapTypeMenu.TERRAIN,
    MapTypeMenu.HYBRID,

    )

fun MapTypeMenu.getDesc():Pair<ImageVector, ImageVector?> {
    return when(this){
        MapTypeMenu.NORMAL -> {
            Pair( Icons.Outlined.Map, null)
        }
        MapTypeMenu.TERRAIN -> {
            Pair( Icons.Outlined.Forest, null)
        }
        MapTypeMenu.HYBRID -> {
            Pair( Icons.Outlined.Public, null)
        }
    }
}

fun Location.toLatLng():LatLng{
    return LatLng(this.latitude, this.longitude)
}

@SuppressLint("MissingPermission", "UnrememberedMutableState")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GoogleMapView(
    onOneClickHandler:()->Unit,
    onLongClickHandler:(SiteDefaultData)->Unit,
    onSetSiteDefaultData:(SiteDefaultData)->Unit,
) {



    val permissions = listOf(
        Manifest.permission.INTERNET
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
        val db = LocalSearchCampDB.current


        val viewModel = remember {
            GoogleMapViewModel(
                repository = RepositoryProvider.getRepository().apply { database = db })
        }

        val currentSiteDataList = viewModel.currentListDataStateFlow.collectAsState()


        val currentLocation by remember {
            mutableStateOf(
                LatLng(
                    currentSiteDataList.value.first().mapY.toDouble(),
                    currentSiteDataList.value.first().mapX.toDouble()
                )
            )
        }


        val markerState = MarkerState(position = currentLocation)
        val defaultCameraPosition = CameraPosition.fromLatLngZoom(currentLocation, 12f)
        var cameraPositionState = CameraPositionState(position = defaultCameraPosition)


        var mapProperties by remember {
            mutableStateOf(
                MapProperties(
                    mapType = MapType.NORMAL,
                    isMyLocationEnabled = true,
                    //  mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle_night)
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
            markerState.position = it
            cameraPositionState =
                CameraPositionState(position = CameraPosition.fromLatLngZoom(it, 12f))
        }

        var mapTypeIndex by rememberSaveable { mutableStateOf(0) }

        var isVisibleSiteDefaultView by remember { mutableStateOf(false) }
        var currentSiteDefaultData:SiteDefaultData? by remember {
            mutableStateOf(null)
        }

        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            topBar = {},
            bottomBar = {},
            snackbarHost = {},
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
        ) {

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(it),
                contentAlignment = Alignment.BottomCenter,


            ) {

                GoogleMap(
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties,
                    uiSettings = uiSettings,
                    onMapLongClick = onMapLongClickHandler,
                    onMapClick = {
                        isVisibleSiteDefaultView = false
                    }
                    ) {

/*
                    Marker(
                        state = markerState,
                        title = "lat/lng:(${
                            String.format(
                                "%.5f",
                                markerState.position.latitude
                            )
                        },${String.format("%.5f", markerState.position.longitude)})",
                    )


 */

                    currentSiteDataList.value.forEach { it ->

                        val state = MarkerState(position = LatLng(it.mapY.toDouble(), it.mapX.toDouble()))
                        Marker(
                            state = state,
                            title =  it.facltNm,
                            onClick = {marker ->
                                isVisibleSiteDefaultView = true
                                currentSiteDefaultData = CampSite_TBL.toSiteDefaultData(it)
                                onSetSiteDefaultData(CampSite_TBL.toSiteDefaultData(it))
                                false
                            },
                            onInfoWindowClick = {
                                isVisibleSiteDefaultView = false
                            }
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
                        .padding(2.dp)
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
                                    //   hapticProcessing()
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
                                .padding(bottom = 30.dp)
                                .align(Alignment.BottomCenter),
                            contentAlignment = Alignment.Center,
                        ) {

                            SiteDefaultView(
                                siteData = it,
                                onClick = {
                                    onOneClickHandler()
                                },
                                onLongClick = {
                                    onLongClickHandler(it)
                                }
                            )


                        }

                    }
                }





            }

        }


    }

}



@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun PrevViewMap(){

    val permissionsManager = PermissionsManager()

    CompositionLocalProvider(LocalPermissionsManager provides permissionsManager) {

        val permissions = listOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val multiplePermissionsState = rememberMultiplePermissionsState(permissions)
        CheckPermission(multiplePermissionsState = multiplePermissionsState)


        var isGranted by mutableStateOf(true)
        permissions.forEach { chkPermission ->
            isGranted =  isGranted && multiplePermissionsState.permissions.find { it.permission == chkPermission }?.status?.isGranted
                ?: false
        }



        PermissionRequiredCompose(
            isGranted = isGranted,
            multiplePermissions = permissions,
            viewType = PermissionRequiredComposeFuncName.Weather
        ) {

            SearchCampTheme {
                Box {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {

                        GoogleMapView(onOneClickHandler =  {}, onLongClickHandler = {}, onSetSiteDefaultData = {})

                    }
                }
            }
        }

    }

}
