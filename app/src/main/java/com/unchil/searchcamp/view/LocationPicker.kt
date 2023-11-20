package com.unchil.searchcamp.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.wear.compose.material.Text
import com.unchil.searchcamp.data.VWorldService
import com.unchil.searchcamp.db.entity.SiDo_TBL
import com.unchil.searchcamp.db.entity.SiGunGu_TBL
import kotlin.math.absoluteValue

/*
@Composable
fun AdministrativeDistrictSiDoPicker(
    dataList:List<SiDo_TBL>,
    onSelectedHandler:(VWorldService, String, String)-> Unit,
) {

    val hapticFeedback = LocalHapticFeedback.current

    val configuration = LocalConfiguration.current

    val sido_state = rememberPickerState(
        initialNumberOfOptions =dataList.size,
        initiallySelectedOption= 0,
        repeatItems = false)



    LaunchedEffect (key1 = configuration.orientation) {
        sido_state.scrollToOption(0)
    }



    LaunchedEffect(key1 =sido_state.isScrollInProgress) {
        if (sido_state.isScrollInProgress) {
    //        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }else {

            val  administrativeDistrictSiDo:Pair<String, String>  = if(dataList.size <= sido_state.selectedOption){
                Pair(  dataList.last().ctprvn_cd, dataList.last().ctp_kor_nm)
            }else {
                Pair(dataList[sido_state.selectedOption].ctprvn_cd , dataList[sido_state.selectedOption].ctp_kor_nm )
            }

            onSelectedHandler(
                VWorldService.LT_C_ADSIDO_INFO,
                administrativeDistrictSiDo.first,
                administrativeDistrictSiDo.second
            )
        }
    }

    Picker(
        modifier = Modifier
            .clip(shape = ShapeDefaults.ExtraSmall)
            .size(width = 160.dp, height = 100.dp),
        state = sido_state,
        contentDescription = null,
        gradientColor =  MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
    ) {

        val  text  = if(dataList.size <= it){
            dataList.last().ctp_kor_nm
        }else {
            dataList[it].ctp_kor_nm
        }


        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = text  ,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge,
        )

    }

}

 */


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdministrativeDistrictSiDoPicker(
    dataList:List<SiDo_TBL>,
    onSelectedHandler:(VWorldService, String, String)-> Unit,
){

    val pagerState  =   rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = {  dataList.size } )

    LaunchedEffect(key1 = pagerState.isScrollInProgress){
        if (!pagerState.isScrollInProgress){
            onSelectedHandler(
                VWorldService.LT_C_ADSIDO_INFO,
                dataList[pagerState.currentPage].ctprvn_cd,
                dataList[pagerState.currentPage].ctp_kor_nm
            )
        }
    }


    val itemHeight = 20.dp
    val itemViewCount = 5
    val boxWidth = 160.dp
    val boxHeight = itemHeight * itemViewCount + itemHeight / 3
    val  paddingValues = PaddingValues( vertical = boxHeight /2   -  itemHeight  / 2 )
    val pagesPerViewport = object : PageSize {
        override fun Density.calculateMainAxisPageSize(
            availableSpace: Int,
            pageSpacing: Int
        ): Int {
            return  availableSpace
        }
    }

    val colorStops = arrayOf(
        0.3f to Color.White,
        0.5f to Color.LightGray,
        1f to Color.Gray.copy(alpha = 0.3f)
    )

    Box(
        modifier = Modifier
            .clip(ShapeDefaults.Small)
            .width(boxWidth)
            .height(boxHeight)
            .background(Brush.radialGradient(colorStops = colorStops)),
        contentAlignment = Alignment.Center
    ){

        VerticalPager(
            modifier = Modifier,
            state = pagerState,
            pageSpacing = 0.dp,
            pageSize = pagesPerViewport,
            beyondBoundsPageCount = 15,
            contentPadding = paddingValues,
        ) {page ->

            Card(
                modifier = Modifier
                    .height(itemHeight)
                    .width(boxWidth)
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
                            start = 0.7f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )

                        scaleY = lerp(
                            start = 0.7f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )


                    },
                shape =  ShapeDefaults.ExtraSmall,
                colors = CardColors(
                    containerColor =  Color.Transparent,
                    contentColor = Color.Black,
                    disabledContainerColor =  Color.Gray,
                    disabledContentColor = Color.Gray )
            ) {

                Text(
                    modifier = Modifier
                        .fillMaxSize(),
                    text = dataList[page].ctp_kor_nm,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

        }

    }


}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdministrativeDistrictSiGunGuPicker(
    dataList:List<SiGunGu_TBL>,
    onSelectedHandler:(VWorldService, String, String)-> Unit
) {



    val pagerState  =   rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = {  dataList.size } )


    LaunchedEffect(key1 = dataList ){
        pagerState.scrollToPage(0)
    }


    LaunchedEffect(key1 = pagerState.isScrollInProgress){
        if (!pagerState.isScrollInProgress){
            onSelectedHandler(
                VWorldService.LT_C_ADSIGG_INFO,
                dataList[pagerState.currentPage].sig_cd,
                dataList[pagerState.currentPage].sig_kor_nm
            )
        }
    }

    val itemHeight = 20.dp
    val itemViewCount = 5
    val boxWidth = 160.dp
    val boxHeight = itemHeight * itemViewCount + itemHeight / 3
    val  paddingValues = PaddingValues( vertical = boxHeight /2   -  itemHeight  / 2 )
    val pagesPerViewport = object : PageSize {
        override fun Density.calculateMainAxisPageSize(
            availableSpace: Int,
            pageSpacing: Int
        ): Int {
            return  availableSpace
        }
    }

    val colorStops = arrayOf(
        0.3f to Color.White,
        0.5f to Color.LightGray,
        1f to Color.Gray.copy(alpha = 0.3f)
    )

    Box(
        modifier = Modifier
            .clip(ShapeDefaults.Small)
            .width(boxWidth)
            .height(boxHeight)
            .background(Brush.radialGradient(colorStops = colorStops)),
        contentAlignment = Alignment.Center
    ){

        VerticalPager(
            modifier = Modifier,
            state = pagerState,
            pageSpacing = 0.dp,
            pageSize = pagesPerViewport,
            beyondBoundsPageCount = 15,
            contentPadding = paddingValues,
        ) {page ->

            Card(
                modifier = Modifier
                    .height(itemHeight)
                    .width(boxWidth)
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
                            start = 0.7f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )

                        scaleY = lerp(
                            start = 0.7f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )


                    },
                shape =  ShapeDefaults.ExtraSmall,
                colors = CardColors(
                    containerColor =  Color.Transparent,
                    contentColor = Color.Black,
                    disabledContainerColor =  Color.Gray,
                    disabledContentColor = Color.Gray )
            ) {

                Text(
                    modifier = Modifier
                        .fillMaxSize(),
                    text = dataList[page].sig_kor_nm,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

        }

    }



}

/*

@Composable
fun AdministrativeDistrictSiGunGuPicker(
    dataList:List<SiGunGu_TBL>,
    onSelectedHandler:(VWorldService, String, String)-> Unit
) {

    val hapticFeedback = LocalHapticFeedback.current
    val configuration = LocalConfiguration.current

    val siggState = rememberPickerState(
        initialNumberOfOptions =dataList.size,
        initiallySelectedOption= 0,
        repeatItems = false)

    LaunchedEffect (key1 = configuration.orientation) {
        siggState.scrollToOption(0)
    }


    LaunchedEffect(key1 =siggState.isScrollInProgress) {
        if (siggState.isScrollInProgress) {
       //     hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        } else {


            val  administrativeDistrictSiGunGu:Pair<String, String>  = if(dataList.size <= siggState.selectedOption){
                Pair(  dataList.last().sig_cd, dataList.last().sig_kor_nm)
            }else {
                Pair(dataList[siggState.selectedOption].sig_cd , dataList[siggState.selectedOption].sig_kor_nm )
            }



            onSelectedHandler(
                VWorldService.LT_C_ADSIGG_INFO,
                administrativeDistrictSiGunGu.first,
                administrativeDistrictSiGunGu.second
            )
        }
    }

    Picker(
        modifier = Modifier
            .clip(shape = ShapeDefaults.ExtraSmall)
            .size(width = 160.dp, height = 100.dp),
        state = siggState,
        contentDescription = null,
        gradientColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
    ) {

        // 빠른 recomposable 에 의 한 dataList  size  와  initialNumberOfOptions 의 불일치 로 인한 outofbound index
        val  text  = if(dataList.size <= it){
            dataList.last().sig_kor_nm
        }else {
            dataList[it].sig_kor_nm
        }

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = text  ,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge,
        )


    }

}


 */



@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = false, showSystemUi = false)
@Composable
fun LocationPickerPreview() {



}
