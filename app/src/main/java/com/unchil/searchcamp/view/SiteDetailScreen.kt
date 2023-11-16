package com.unchil.searchcamp.view


//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unchil.searchcamp.data.GoCampingService
import com.unchil.searchcamp.data.RepositoryProvider
import com.unchil.searchcamp.db.LocalSearchCampDB
import com.unchil.searchcamp.db.SearchCampDB
import com.unchil.searchcamp.model.SiteDefaultData
import com.unchil.searchcamp.navigation.SearchCampDestinations
import com.unchil.searchcamp.navigation.detailScreens
import com.unchil.searchcamp.shared.LocalPermissionsManager
import com.unchil.searchcamp.shared.PermissionsManager
import com.unchil.searchcamp.shared.checkInternetConnected
import com.unchil.searchcamp.ui.theme.SearchCampTheme
import com.unchil.searchcamp.viewmodel.SiteImagePagerViewModel
import kotlinx.coroutines.delay


@SuppressLint("UnrememberedMutableState")
@Composable
fun SiteDetailScreen(data:SiteDefaultData) {

    val selectedScreen =  mutableStateOf(0)
    val context = LocalContext.current
    val db = LocalSearchCampDB.current

    val viewModel =
        SiteImagePagerViewModel(
            repository = RepositoryProvider.getRepository().apply { database = db })


    var isConnect by remember { mutableStateOf(context.checkInternetConnected()) }

    LaunchedEffect(key1 = isConnect) {
        while (!isConnect) {
            delay(500)
            isConnect = context.checkInternetConnected()
        }
    }


    LaunchedEffect(key1 = viewModel){

        if(isConnect) {
            viewModel.onEvent(
                SiteImagePagerViewModel.Event.RecvGoCampingData(
                    servicetype =  GoCampingService.SITEIMAGE,
                    contentId = data.contentId
                )
            )
        }
    }

    val hapticFeedback = LocalHapticFeedback.current

    var isHapticProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = isHapticProcessing) {
        if (isHapticProcessing) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            isHapticProcessing = false
        }
    }


    Scaffold(
        topBar = {
            BottomNavigation(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(elevation = 1.dp),
                backgroundColor = MaterialTheme.colorScheme.background,
            ) {
                detailScreens.forEachIndexed { index, it ->

                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    imageVector = it.icon ?: Icons.Outlined.Info,
                                    contentDescription = context.resources.getString(it.name),
                                    tint = if (selectedScreen.value == index) MaterialTheme.colorScheme.onSurface else Color.LightGray
                                )
                            },
                            label = {

                                Text(  context.resources.getString(it.name),
                                    modifier = Modifier,
                                    textAlign = TextAlign.Center,
                                    style  = MaterialTheme.typography.titleSmall,
                                    color =  if (selectedScreen.value == index) MaterialTheme.colorScheme.onSurface else Color.LightGray
                                )


                            },
                            alwaysShowLabel = false,
                            selected = selectedScreen.value == index,
                            onClick = {

                                selectedScreen.value  = index
                           //     isHapticProcessing = true
                            },
                            selectedContentColor = MaterialTheme.colorScheme.onSurface,
                        //    unselectedContentColor = MaterialTheme.colorScheme.secondary
                        )

                }
            }

        },
        bottomBar = {    }
    ) {
        Box(
            Modifier
                .padding(it)
        ) {

            when(detailScreens[selectedScreen.value]){
                SearchCampDestinations.introductionScreen -> {
                    SiteIntroductionView(data)
                }
                SearchCampDestinations.imageScreen -> {
                    SiteImagePagerView(viewModel)
                }
                else -> {}
            }

        }

    }



}



@Preview
@Composable
fun PrevSiteDetailView(){
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

                    SiteDetailScreen(data = SiteDefaultData.setInitValue())
                }
            }

        }

    }
}