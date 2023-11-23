package com.unchil.searchcamp.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.unchil.searchcamp.LocalUsableHaptic
import com.unchil.searchcamp.R
import com.unchil.searchcamp.model.SnackBarChannelType
import com.unchil.searchcamp.model.snackbarChannelList
import com.unchil.searchcamp.navigation.SearchCampDestinations
import com.unchil.searchcamp.navigation.resultScreens
import com.unchil.searchcamp.viewmodel.SearchScreenViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Composable
fun ResultNavScreen(
    viewModel: SearchScreenViewModel,
    onScaffoldStateHandler:()->Unit
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

    val currentListDataCntStateFlow = viewModel.currentListDataCntStateFlow.collectAsState()

    var selectedScreen by rememberSaveable { mutableIntStateOf(0) }

    val snackBarHostState = remember { SnackbarHostState() }
    val channel = remember { Channel<Int>(Channel.CONFLATED) }

    LaunchedEffect(channel) {

        channel.receiveAsFlow().collect { index ->

            val channelData = snackbarChannelList.first {
                it.channel == index
            }


            val result = snackBarHostState.showSnackbar(
                message =context.getString( channelData.message),
                actionLabel =  channelData.actionLabel,
                withDismissAction = channelData.withDismissAction,
                duration = channelData.duration
            )

            when (result) {
                SnackbarResult.ActionPerformed -> {
                    //     hapticProcessing()
                    //----------
                    when (channelData.channelType) {
                        SnackBarChannelType.SEARCH_RESULT -> {

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

    var isConcealed by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {

        },
        bottomBar = {
            if (isPortrait) {
                BottomNavigation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(elevation = 1.dp),
                    backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    elevation = 2.dp,
                ) {

                    resultScreens.forEachIndexed { index, it ->
                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    imageVector = it.icon ?: Icons.Outlined.Info,
                                    contentDescription = context.resources.getString(it.name),
                                    tint = if (selectedScreen == index) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outline
                                )
                            },
                            label = {

                                Text(
                                    context.resources.getString(it.name),
                                    modifier = Modifier,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (selectedScreen == index) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outline
                                )

                            },
                            alwaysShowLabel = true,
                            selected = selectedScreen == index,
                            onClick = {
                                hapticProcessing()
                                selectedScreen = index

                            },
                            selectedContentColor = MaterialTheme.colorScheme.tertiary,
                            unselectedContentColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    }
                }


            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState){
                Snackbar(
                    snackbarData = it,
                    modifier = Modifier,
                    shape = ShapeDefaults.ExtraSmall,
                    containerColor = Color.Yellow,
                    contentColor = Color.Black,
                    actionColor = Color.Red,

                    dismissActionContentColor = Color.LightGray
                )
            }
        },
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
                        containerColor= MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        header = {
                            Column(
                                modifier = Modifier,
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment =  Alignment.CenterHorizontally,
                            ) {

                                androidx.compose.material3.IconButton(
                                    onClick = {
                                        isConcealed = ! isConcealed
                                        onScaffoldStateHandler()
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.TravelExplore ,
                                        contentDescription = "Localized description"
                                    )
                                }


                                Text(
                                    text = context.getString(R.string.mainmenu_result),
                                    modifier = Modifier,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = "${currentListDataCntStateFlow.value}건",
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
                                        tint =  if (selectedScreen == index) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outline
                                    )
                                },
                                label = {

                                    Text(
                                        context.resources.getString(it.name),
                                        modifier = Modifier,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (selectedScreen == index) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outline
                                    )

                                },
                                alwaysShowLabel = true,
                                selected = selectedScreen == index,
                                onClick = {
                                    hapticProcessing()
                                    selectedScreen = index
                                },

                            )
                        }


                    }
                }


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    when (resultScreens[selectedScreen]) {
                        SearchCampDestinations.resultListScreen -> {
                            ResultListScreen(viewModel = viewModel)
                        }
                        SearchCampDestinations.resultMapScreen -> {
                            ResultMapScreen(viewModel = viewModel)
                        }
                    }
                }


            }



        }


    }


}