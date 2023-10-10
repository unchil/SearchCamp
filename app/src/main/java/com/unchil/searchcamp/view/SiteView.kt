package com.unchil.searchcamp.view

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bungalow
import androidx.compose.material.icons.outlined.Cabin
import androidx.compose.material.icons.outlined.Cottage
import androidx.compose.material.icons.outlined.Countertops
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.NaturePeople
import androidx.compose.material.icons.outlined.OutdoorGrill
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Signpost
import androidx.compose.material.ripple.rememberRipple

import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.compose.rememberNavController
import coil.size.Size
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.unchil.searchcamp.R
import com.unchil.searchcamp.data.GoCampingService
import com.unchil.searchcamp.data.RepositoryProvider
import com.unchil.searchcamp.db.LocalSearchCampDB
import com.unchil.searchcamp.db.SearchCampDB
import com.unchil.searchcamp.model.SiteDefaultData
import com.unchil.searchcamp.shared.LocalPermissionsManager
import com.unchil.searchcamp.shared.PermissionsManager
import com.unchil.searchcamp.shared.checkInternetConnected
import com.unchil.searchcamp.shared.view.CheckPermission
import com.unchil.searchcamp.shared.view.PermissionRequiredCompose
import com.unchil.searchcamp.ui.theme.SearchCampTheme
import com.unchil.searchcamp.viewmodel.SiteImagePagerViewModel
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@SuppressLint("UnrememberedMutableState")
@OptIn( ExperimentalPermissionsApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun SiteDefaultView(siteData:SiteDefaultData,   allowHardware:Boolean = true, onClick:()->Unit,  onLongClick:()->Unit){

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


            Box(
                modifier = Modifier
                    .combinedClickable(
                        interactionSource = interactionSource,
                        indication = rememberRipple(bounded = true),
                        onClick = {
                            onClick.invoke()
                        },
                        onLongClick = {
                            onLongClick.invoke()
                        }
                    )
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(vertical = 2.dp)
                    .clip(ShapeDefaults.ExtraSmall)
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)

                ,
                contentAlignment = Alignment.Center
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center

                    ) {

                        Icon(
                            imageVector = Icons.Outlined.Cabin,
                            contentDescription = "cabin",
                            modifier = Modifier.scale(0.7f)
                        )

                        Spacer(modifier = Modifier.size(10.dp))

                        Text(
                            text = siteData.facltNm,
                            modifier = Modifier,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.bodyMedium
                        )

                    }


                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center

                    ) {


                        Column (
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ){



                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center

                            ) {

                                Icon(
                                    imageVector = Icons.Outlined.Description,
                                    contentDescription = "description",
                                    modifier = Modifier.scale(0.7f)
                                )


                                Text(
                                    text =  siteData.lineIntro,
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start,
                                    style = MaterialTheme.typography.bodySmall
                                )

                            }



                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center

                            ) {

                                Icon(
                                    imageVector = Icons.Outlined.Cottage,
                                    contentDescription = "Cottage",
                                    modifier = Modifier.scale(0.7f)
                                )



                                Text(
                                    text =  siteData.induty,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start,
                                    style = MaterialTheme.typography.bodySmall
                                )

                            }



                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center

                            ) {

                                Icon(
                                    imageVector = Icons.Outlined.NaturePeople,
                                    contentDescription = "NaturePeople",
                                    modifier = Modifier.scale(0.7f)
                                )




                                Text(
                                    text =  siteData.facltDivNm,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start,
                                    style = MaterialTheme.typography.bodySmall
                                )

                            }



                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center

                            ) {

                                Icon(
                                    imageVector = Icons.Outlined.Forest,
                                    contentDescription = "Forest",
                                    modifier = Modifier.scale(0.7f)
                                )

                                Text(
                                    text =  siteData.lctCl,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start,
                                    style = MaterialTheme.typography.bodySmall
                                )

                            }


                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center

                            ) {

                                Icon(
                                    imageVector = Icons.Outlined.Pets,
                                    contentDescription = "Pets",
                                    modifier = Modifier.scale(0.7f)
                                )

                                Text(
                                    text =  siteData.animalCmgCl,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start,
                                    style = MaterialTheme.typography.bodySmall
                                )


                            }



                        }



                        PhotoPreview(
                            data = if(siteData.firstImageUrl.isNotEmpty()){siteData.firstImageUrl} else {
                                R.drawable.forest} ,
                            allowHardware = allowHardware,
                            onPhotoPreviewTapped = { }
                        )


                    }
                }
            }


    }

}



@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SiteDescriptionView(siteData:SiteDefaultData, onEvent: () -> Unit){


    val permissions = listOf(
        Manifest.permission.INTERNET
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

            Box(
                modifier = Modifier
                    .clip(ShapeDefaults.ExtraSmall)
                    .clickable {
                        onEvent()
                    }
                    .fillMaxWidth(1f)
                    .height(500.dp)
            //        .padding(6.dp)
     ,
                contentAlignment = Alignment.Center
            ) {


                Box(modifier = Modifier
                    .background(color = Color.Black.copy(alpha = 0.5f))
                    .padding(1.dp) ){
                    ImageViewer(
                        data = if(siteData.firstImageUrl.isNotEmpty()){siteData.firstImageUrl} else {R.drawable.forest2},
                        size = Size.ORIGINAL,
                        contentScale = ContentScale.Crop
                    )
                }


                Column(
                    modifier = Modifier
                        .clip(ShapeDefaults.ExtraSmall)
                        .fillMaxWidth(0.7f)
                        .fillMaxHeight(0.7f)
                        .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Text(
                        text = siteData.facltNm,
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.size(20.dp))

                    AnimatedVisibility(visible = siteData.tel.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start

                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Phone,
                                contentDescription = "전화",
                                modifier = Modifier.scale(0.7f)
                            )
                            Text(
                                text = siteData.tel,
                                modifier = Modifier,
                                //    fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                    }


                    AnimatedVisibility(visible = siteData.addr1.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Signpost,
                                contentDescription = "주소",
                                modifier = Modifier.scale(0.7f)
                            )
                            Text(
                                text = siteData.addr1,
                                modifier = Modifier,
                                //   fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    AnimatedVisibility(visible = siteData.resveCl.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.EventAvailable,
                                contentDescription = "예약",
                                modifier = Modifier.scale(0.7f)
                            )
                            Text(
                                text = siteData.resveCl,
                                modifier = Modifier,
                                //       fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    AnimatedVisibility(visible = siteData.sbrsCl.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Countertops,
                                contentDescription = "시설",
                                modifier = Modifier.scale(0.7f)
                            )
                            Text(
                                text = siteData.sbrsCl,
                                modifier = Modifier,
                                //     fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                    }


                    AnimatedVisibility(visible = siteData.eqpmnLendCl.isNotEmpty()) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.OutdoorGrill,
                                contentDescription = "대여장비",
                                modifier = Modifier.scale(0.7f)
                            )
                            Text(
                                text = siteData.eqpmnLendCl,
                                modifier = Modifier,
                                //        fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                    }

                    AnimatedVisibility(visible = siteData.glampInnerFclty.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Bungalow,
                                contentDescription = "글램핑시설",
                                modifier = Modifier.scale(0.7f)
                            )
                            Text(
                                text = siteData.glampInnerFclty,
                                modifier = Modifier,
                                //        fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                    }


/*
                    IconButton(
                        onClick = {
                            navController.navigateTo(SearchCampDestinations.createRoute(SearchCampDestinations.detailScreen, contentId = siteData.contentId, url =  siteData.homepage))
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Home,
                            contentDescription = "home",
                            modifier = Modifier
                                .scale(1.5f)
                                .padding(vertical = 10.dp)
                        )
                    }

 */



                }

            }





    }
}


@Composable
fun SiteIntroductionView( siteData:SiteDefaultData){

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Text(
                text = siteData.facltNm,
                modifier = Modifier.padding(vertical = 10.dp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )

            AnimatedVisibility(visible = siteData.firstImageUrl.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(ShapeDefaults.ExtraSmall)
                ) {
                    ImageViewer(
                        data = siteData.firstImageUrl,
                        size = Size.ORIGINAL,
                        contentScale = ContentScale.Crop
                    )
                }

            }

            AnimatedVisibility(visible = siteData.intro.isNotEmpty()) {
                Text(
                    text = siteData.intro,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .verticalScroll(scrollState),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyMedium
                )

            }


        }




}


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalFoundationApi::class)
@Composable
fun SiteImagePagerView( contentId: String){

    val permissions = listOf(
        Manifest.permission.INTERNET,
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


        val db = LocalSearchCampDB.current
        val context = LocalContext.current

        var isConnect by remember { mutableStateOf(context.checkInternetConnected()) }

        LaunchedEffect(key1 = isConnect) {
            while (!isConnect) {
                delay(500)
                isConnect = context.checkInternetConnected()
            }
        }


        val viewModel = remember {
            SiteImagePagerViewModel(
                repository = RepositoryProvider.getRepository().apply { database = db })
        }


        LaunchedEffect(key1 = viewModel){

            if(isConnect) {
                viewModel.onEvent(
                    SiteImagePagerViewModel.Event.RecvGoCampingData(
                        servicetype =  GoCampingService.SITEIMAGE,
                        contentId = contentId
                    )
                )
            }



            viewModel.onEvent(
                SiteImagePagerViewModel.Event.GetImageList(contentId = contentId)
            )
       }

        val siteImageList = viewModel.siteImageListStateFlow.collectAsState()


        val pagerState  =   rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f,
            pageCount = {  siteImageList.value.size } )


        val threePagesPerViewport = object : PageSize {
            override fun Density.calculateMainAxisPageSize(
                availableSpace: Int,
                pageSpacing: Int
            ): Int {
                return (availableSpace - 2 * pageSpacing) / 1
            }
        }


        if( siteImageList.value.isNotEmpty() ) {


            Box (
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ){




                HorizontalPager(
                    modifier = Modifier,
                    state = pagerState,
                    pageSpacing = 0.dp,
                    pageSize = threePagesPerViewport,
                    beyondBoundsPageCount = 6,
                    contentPadding = PaddingValues(start = 60.dp, end = 60.dp),
                ) { page ->

                    Card(
                        Modifier
                            .size(340.dp)
                            .graphicsLayer {
                                // Calculate the absolute offset for the current page from the
                                // scroll position. We use the absolute value which allows us to mirror
                                // any effects for both directions
                                val pageOffset = (
                                        (pagerState.currentPage - page) + pagerState
                                            .currentPageOffsetFraction
                                        ).absoluteValue


                                alpha = lerp(
                                    start = 0.7f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )

                                scaleX = lerp(
                                    start = 0.9f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )

                                scaleY = lerp(
                                    start = 0.9f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )


                            }
                    ) {
                        ImageViewer(
                            data = (siteImageList.value.get(page).imageUrl),
                            size = Size.ORIGINAL,
                            isZoomable = false,
                            contentScale = ContentScale.Fit
                        )

                    }
                }


            }

        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            )
        }




    }
}


@Preview
@Composable
fun PrevSiteView(){

    val context = LocalContext.current
    val permissionsManager = PermissionsManager()
    val searchCampDB = SearchCampDB.getInstance(context.applicationContext)

    val scrollState = rememberScrollState()
    val navController = rememberNavController()

    SearchCampTheme {
        Surface(modifier = Modifier,  color = MaterialTheme.colorScheme.background     ) {


            CompositionLocalProvider(LocalPermissionsManager provides permissionsManager) {
                CompositionLocalProvider(LocalSearchCampDB provides searchCampDB) {

               //     Column(modifier = Modifier.verticalScroll(scrollState)) {



                        //     SiteIntroductionView( navController = navController, contentId = "92")

                    Box(
                        Modifier.fillMaxSize()) {
                        SiteImagePagerView(contentId = "92")
                    }
                        //       SiteWebView(navController = navController, "https://goldenvalleycamping.modoo.at/")
                    }


                //}
            }


        }
    }


}