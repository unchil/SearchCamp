package com.unchil.searchcamp.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.unchil.searchcamp.LocalUsableHaptic
import com.unchil.searchcamp.R
import com.unchil.searchcamp.navigation.SearchCampDestinations
import com.unchil.searchcamp.navigation.mainScreens
import com.unchil.searchcamp.navigation.navigateTo
import com.unchil.searchcamp.navigation.resultScreens
import kotlinx.coroutines.launch

@Composable
fun ResultNavScreen(
    navController: NavHostController,
    administrativeDistrictSiDoCode:String,
    administrativeDistrictSiGunGu:String,
    searchTitle:String? = null
){


    val configuration = LocalConfiguration.current
    var isPortrait by remember { mutableStateOf(false) }
    isPortrait = when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            true
        }
        else -> {
            false
        }
    }

    var columnWidth by remember { mutableStateOf(1f) }
    var columnHeight by remember { mutableStateOf(1f) }

    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            isPortrait = true
            columnWidth = 1f
            columnHeight = 1f
        }

        else -> {
            isPortrait = false
            columnWidth = 0.9f
            columnHeight = 1f
        }
    }


    val context = LocalContext.current
    val isUsableHaptic = LocalUsableHaptic.current
    val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    fun hapticProcessing(){
        if(isUsableHaptic){
            coroutineScope.launch {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

    var selectedScreen by rememberSaveable { mutableIntStateOf(0) }




    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            if(isPortrait) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    contentAlignment = Alignment.Center
                ) {

                    IconButton(
                        onClick = {
                            hapticProcessing()
                            navController.popBackStack()
                        },
                        modifier = Modifier.align(Alignment.CenterStart),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBackIosNew,
                            contentDescription = "이전화면",
                            tint = Color.LightGray
                        )
                    }


                    Text(
                        text = context.getString(R.string.mainmenu_result),
                        modifier = Modifier.align(Alignment.Center),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                }
            }
        },
        bottomBar = {
            if (isPortrait) {
                BottomNavigation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(elevation = 1.dp),
                    backgroundColor = MaterialTheme.colorScheme.background
                ) {

                    resultScreens.forEachIndexed { index, it ->
                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    imageVector = it.icon ?: Icons.Outlined.Info,
                                    contentDescription = context.resources.getString(it.name),
                                    tint = if (selectedScreen == index) MaterialTheme.colorScheme.onSurface else Color.LightGray
                                )
                            },
                            label = {

                                Text(
                                    context.resources.getString(it.name),
                                    modifier = Modifier,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (selectedScreen == index) MaterialTheme.colorScheme.onSurface else Color.LightGray
                                )

                            },
                            alwaysShowLabel = true,
                            selected = selectedScreen == index,
                            onClick = {
                                hapticProcessing()
                                selectedScreen = index

                            },
                            selectedContentColor = MaterialTheme.colorScheme.onSurface,
                            //      unselectedContentColor = Color.Gray
                        )
                    }
                }


            }
        },
        snackbarHost = {},
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {

        Box(
            modifier = Modifier.padding(it),
            contentAlignment = Alignment.Center,
        ){

            Row(
                modifier = Modifier.fillMaxSize(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {


                if (!isPortrait) {
                    NavigationRail(
                        modifier = Modifier
                            .shadow(elevation = 1.dp)
                            .width(80.dp)
                            .fillMaxHeight(),
                        header = {
                            Column(
                                modifier = Modifier,
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment =  Alignment.CenterHorizontally,
                            ) {

                                IconButton(
                                    onClick = {
                                        hapticProcessing()
                                        navController.popBackStack()
                                    },
                                    modifier = Modifier,
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ArrowBackIosNew,
                                        contentDescription = "이전화면",
                                        tint = Color.LightGray
                                    )
                                }

                                Spacer(modifier = Modifier.size(20.dp))

                                Text(
                                    text = context.getString(R.string.mainmenu_result),
                                    modifier = Modifier,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )

                            }
                        }
                    ) {



                        resultScreens.forEachIndexed { index, it ->
                            NavigationRailItem(
                                icon = {
                                    Icon(
                                        imageVector = it.icon ?: Icons.Outlined.Info,
                                        contentDescription = context.resources.getString(it.name),
                                        tint = if (selectedScreen == index) MaterialTheme.colorScheme.onSurface else Color.LightGray
                                    )
                                },
                                label = {

                                    Text(
                                        context.resources.getString(it.name),
                                        modifier = Modifier,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (selectedScreen == index) MaterialTheme.colorScheme.onSurface else Color.LightGray
                                    )

                                },
                                alwaysShowLabel = true,
                                selected = selectedScreen == index,
                                onClick = {
                                    hapticProcessing()
                                    selectedScreen = index
                                },
                                //      unselectedContentColor = Color.Gray
                            )
                        }


                    }
                }


                Box(
                    modifier = Modifier
                 //       .fillMaxWidth(columnWidth)
                        .fillMaxWidth()
                ) {
                    when (resultScreens[selectedScreen]) {
                        SearchCampDestinations.resultListScreen -> {

                            ResultListScreen(
                                navController,
                                administrativeDistrictSiDoCode,
                                administrativeDistrictSiGunGu,
                                searchTitle
                            )

                        }

                        SearchCampDestinations.resultMapScreen -> {

                            ResultMapScreen(
                                navController,
                                administrativeDistrictSiDoCode,
                                administrativeDistrictSiGunGu,
                                searchTitle
                            )
                        }

                        else -> {}
                    }
                }


            }



        }

       /*
        Box(
            modifier = Modifier.padding(it),
            contentAlignment = Alignment.Center,
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {



                Row(
                    modifier = Modifier.fillMaxSize(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Box(
                        modifier = Modifier.fillMaxWidth(columnWidth)
                    ) {

                        when (resultScreens[selectedScreen]) {
                            SearchCampDestinations.resultListScreen -> {

                                ResultListScreen(
                                    navController,
                                    administrativeDistrictSiDoCode,
                                    administrativeDistrictSiGunGu,
                                    searchTitle
                                )

                            }

                            SearchCampDestinations.resultMapScreen -> {

                                ResultMapScreen(
                                    navController,
                                    administrativeDistrictSiDoCode,
                                    administrativeDistrictSiGunGu,
                                    searchTitle
                                )
                            }

                            else -> {}
                        }
                    }

                    if (!isPortrait) {

                        NavigationRail(
                            modifier = Modifier
                                .shadow(elevation = 1.dp)
                                .width(70.dp)
                                .fillMaxHeight(),
                        ) {

                            NavigationRailItem(
                                icon = {
                                    Icon(
                                        imageVector = Icons.Outlined.ArrowBackIosNew,
                                        contentDescription = "",
                                        tint = Color.LightGray
                                    )
                                },
                                label = {
                                    Text(
                                        "",
                                        modifier = Modifier,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.LightGray
                                    )

                                },
                                alwaysShowLabel = false,
                                selected = false,
                                onClick = {
                                    navController.popBackStack()
                                },
                            )

                            Spacer(modifier = Modifier.fillMaxHeight(0.15f))

                            resultScreens.forEachIndexed { index, it ->
                                NavigationRailItem(
                                    icon = {
                                        Icon(
                                            imageVector = it.icon ?: Icons.Outlined.Info,
                                            contentDescription = context.resources.getString(it.name),
                                            tint = if (selectedScreen == index) MaterialTheme.colorScheme.onSurface else Color.LightGray
                                        )
                                    },
                                    label = {

                                        Text(
                                            context.resources.getString(it.name),
                                            modifier = Modifier,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = if (selectedScreen == index) MaterialTheme.colorScheme.onSurface else Color.LightGray
                                        )

                                    },
                                    alwaysShowLabel = false,
                                    selected = selectedScreen == index,
                                    onClick = {
                                        hapticProcessing()
                                        selectedScreen = index
                                    },
                                    //      unselectedContentColor = Color.Gray
                                )
                            }


                        }
                    }

                }


            }// Column



        }

        */


    }

}