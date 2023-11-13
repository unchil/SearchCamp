package com.unchil.searchcamp.view

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.unchil.gismemo.view.GoogleMapView
import com.unchil.searchcamp.data.RepositoryProvider
import com.unchil.searchcamp.db.LocalSearchCampDB
import com.unchil.searchcamp.db.SearchCampDB
import com.unchil.searchcamp.db.entity.CampSite_TBL
import com.unchil.searchcamp.model.SiteDefaultData
import com.unchil.searchcamp.navigation.SearchCampDestinations
import com.unchil.searchcamp.navigation.resultScreens
import com.unchil.searchcamp.shared.LocalPermissionsManager
import com.unchil.searchcamp.shared.PermissionsManager
import com.unchil.searchcamp.ui.theme.SearchCampTheme
import com.unchil.searchcamp.viewmodel.ResultScreenViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@OptIn( ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navController: NavHostController,
    administrativeDistrictSiDoCode:String,
    administrativeDistrictSiGunGu:String,
    searchTitle:String? = null
){

    val context = LocalContext.current
    val db = LocalSearchCampDB.current

    val viewModel = remember {
        ResultScreenViewModel(
            repository = RepositoryProvider.getRepository().apply { database = db },
            administrativeDistrictSiDoCode ,
            administrativeDistrictSiGunGu,
            searchTitle
            )
    }

    val campSiteStream = viewModel.campSiteListPaging.collectAsLazyPagingItems()

    val lazyListState = rememberLazyListState()

    val sheetState = SheetState(
        skipPartiallyExpanded = false,
        density = LocalDensity.current,
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )

    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val coroutineScope = rememberCoroutineScope()
    val currentCampSiteData: MutableState<SiteDefaultData?> = remember { mutableStateOf(null) }
    val sheetPeekHeightValue by remember { mutableStateOf(30.dp) }
    var isVisibleSiteDescriptionView by remember{ mutableStateOf(false) }
    val density = LocalDensity.current

    var selectedScreen by remember { mutableStateOf(0) }


    val onClickHandlerMap:()->Unit = {

        coroutineScope.launch {
            if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                scaffoldState.bottomSheetState.expand()
            } else {
                scaffoldState.bottomSheetState.partialExpand()
            }
        }
    }

    val onClickHandler:(data:SiteDefaultData?)->Unit = {
        it?.let {
            currentCampSiteData.value = it
        }
        coroutineScope.launch {
            if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                scaffoldState.bottomSheetState.expand()
            } else {
                scaffoldState.bottomSheetState.partialExpand()
            }
        }
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
                    .background(color = Color.LightGray.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier
                        .scale(1f)
                        .clickable { onClickHandler.invoke(null) },
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

/*
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,


 */
            ) {
                currentCampSiteData.value?.let {
                    SiteDetailScreen(it)
                }
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier.padding(horizontal = 0.dp),
            contentAlignment = Alignment.Center,
        ){

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                BottomNavigation(
                    modifier = Modifier
                        .padding(top = 6.dp, bottom = 2.dp)
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(elevation = 1.dp),
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                ) {

                    Spacer( modifier = Modifier.padding(horizontal = 10.dp))

                    resultScreens.forEachIndexed { index, it ->
                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    imageVector = it.icon ?: Icons.Outlined.Info,
                                    contentDescription = context.resources.getString(  it.name  ),
                                    tint = if (selectedScreen == index) Color.Red else MaterialTheme.colorScheme.secondary)
                            },
                            label = {
                                Text( text = context.resources.getString( it.name ) )
                            },
                            selected = selectedScreen == index,
                            onClick = {
                                selectedScreen = index
                            },
                            selectedContentColor = Color.Red,
                            unselectedContentColor = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer( modifier = Modifier.padding(horizontal = 10.dp))

                }


                Box (
                    modifier = Modifier
                ){


                    when(resultScreens[selectedScreen]){
                        SearchCampDestinations.listScreen -> {
                            LazyColumn(
                                modifier = Modifier
                                    .align(Alignment.TopCenter),
                                state = lazyListState,
                                userScrollEnabled = true,
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 2.dp)
                            ) {

                                items(campSiteStream.itemCount) {

                                    campSiteStream[it]?.let {

                                        val siteDefaultData = CampSite_TBL.toSiteDefaultData(it)
                                        SiteDefaultView(
                                            siteData = siteDefaultData,
                                            onClick = {  onClickHandler.invoke(siteDefaultData)  },
                                            onLongClick = {
                                                currentCampSiteData.value = siteDefaultData
                                                isVisibleSiteDescriptionView = true
                                            }
                                        )


                                    }


                                }

                            }

                        }
                        SearchCampDestinations.mapScreen -> {
                            GoogleMapView(
                                onOneClickHandler =   onClickHandlerMap,
                                onLongClickHandler = {
                                    currentCampSiteData.value = it
                                    isVisibleSiteDescriptionView = true
                                },
                                onSetSiteDefaultData = {
                                    currentCampSiteData.value = it
                                }
                            )
                        }
                        else -> {}
                    }



                }

            }// Column

            AnimatedVisibility(visible = isVisibleSiteDescriptionView) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(color = Color.DarkGray.copy(alpha = 0.7f))
                )
            }

            AnimatedVisibility(visible = isVisibleSiteDescriptionView,
                enter = slideInVertically {
                    // Slide in from 40 dp from the top.
                    with(density) { 40.dp.roundToPx() }
                } + expandVertically(
                    // Expand from the top.
                    expandFrom = Alignment.Top
                ) + fadeIn(
                    // Fade in with the initial alpha of 0.3f.
                    initialAlpha = 0.3f
                ),
                exit = slideOutVertically() + shrinkVertically() + fadeOut()
            ) {

                currentCampSiteData.value?.let {

                    Box(
                        modifier = Modifier  .clip(ShapeDefaults.ExtraSmall).padding(horizontal = 10.dp)

                    ){
                        SiteDescriptionView(
                            siteData = it,
                            onEvent = {
                                isVisibleSiteDescriptionView = false
                            })
                    }


                }
            }




        }// Box

    }// BottomSheetScaffold

}




@Preview
@Composable
fun PrevResultScreen(){

    val context = LocalContext.current
    val navController = rememberNavController()
    val permissionsManager = PermissionsManager()
    val searchCampDB = SearchCampDB.getInstance(context.applicationContext)

    SearchCampTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            CompositionLocalProvider(LocalPermissionsManager provides permissionsManager) {
                CompositionLocalProvider(LocalSearchCampDB provides searchCampDB) {

                    ResultScreen(
                        navController =  navController,
                        administrativeDistrictSiDoCode = "0",
                        administrativeDistrictSiGunGu = ""
                    )
                }
            }

        }

    }
}