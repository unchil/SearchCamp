package com.unchil.searchcamp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberPickerState
import com.unchil.searchcamp.data.VWorldService
import com.unchil.searchcamp.db.entity.SiDo_TBL
import com.unchil.searchcamp.db.entity.SiGunGu_TBL
import com.unchil.searchcamp.ui.theme.SearchCampTheme

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




@Preview(showBackground = false, showSystemUi = false)
@Composable
fun LocationPickerPreview() {
    SearchCampTheme {
        Surface( modifier = Modifier.fillMaxSize(),  color = MaterialTheme.colorScheme.background  ) {

       }
    }
}
