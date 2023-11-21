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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.wear.compose.material.Text
import com.unchil.searchcamp.LocalUsableHaptic
import com.unchil.searchcamp.data.VWorldService
import com.unchil.searchcamp.db.entity.SiDo_TBL
import com.unchil.searchcamp.db.entity.SiGunGu_TBL
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue





@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdministrativeDistrictPicker(
    administrativeDistrictType: VWorldService,
    dataList:List<Any>,
    pickerWidth: Dp,
    itemHeight: Dp,
    itemViewCount:Int,
    brushType:Brush = Brush.verticalGradient(listOf(Color.Gray, Color.White, Color.Gray)),
    onSelectedHandler:(VWorldService, String, String)-> Unit,

){

    val pagerState  =   rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = {  dataList.size } )

    LaunchedEffect(key1 = dataList ){
        pagerState.scrollToPage(0)
    }

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


    LaunchedEffect(key1 = pagerState.isScrollInProgress){
        if (!pagerState.isScrollInProgress){

            when(administrativeDistrictType){
                VWorldService.LT_C_ADSIDO_INFO -> {
                    onSelectedHandler(
                        VWorldService.LT_C_ADSIDO_INFO,
                        ( dataList[pagerState.currentPage] as SiDo_TBL).ctprvn_cd,
                        ( dataList[pagerState.currentPage]as SiDo_TBL).ctp_kor_nm
                    )
                }
                VWorldService.LT_C_ADSIGG_INFO -> {
                    onSelectedHandler(
                        VWorldService.LT_C_ADSIGG_INFO,
                        ( dataList[pagerState.currentPage] as SiGunGu_TBL).sig_cd,
                        ( dataList[pagerState.currentPage] as SiGunGu_TBL).sig_kor_nm
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = pagerState.currentPage ){
        hapticProcessing()
    }


    val pickerHeight = itemHeight * itemViewCount + itemHeight / 3
    val  paddingValues = PaddingValues( vertical = pickerHeight /2   -  itemHeight  / 2 )
    val pagesPerViewport = object : PageSize {
        override fun Density.calculateMainAxisPageSize(
            availableSpace: Int,
            pageSpacing: Int
        ): Int {
            return  availableSpace
        }
    }


    Box(
        modifier = Modifier
            .clip(ShapeDefaults.Small)
            .width(pickerWidth)
            .height(pickerHeight)
            .background(brushType) ,
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

            val text = when(administrativeDistrictType){
                VWorldService.LT_C_ADSIDO_INFO -> {
                     (dataList [page] as SiDo_TBL).ctp_kor_nm
                }
                VWorldService.LT_C_ADSIGG_INFO -> {
                    (dataList [page] as SiGunGu_TBL).sig_kor_nm
                }
            }
            androidx.compose.material3.Text(
                modifier = Modifier
                    .height(itemHeight)
                    .width(pickerWidth)
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
                text = text,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

        }

    }

}



