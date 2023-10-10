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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
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
    val sido_state = rememberPickerState(dataList.size, repeatItems = false)
    val sido_contentDescription by remember { derivedStateOf { "${sido_state.selectedOption + 1}" } }

    LaunchedEffect(key1 =sido_state.isScrollInProgress) {
        if (sido_state.isScrollInProgress) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }else {
            onSelectedHandler(VWorldService.LT_C_ADSIDO_INFO, dataList[sido_state.selectedOption].ctprvn_cd, dataList[sido_state.selectedOption].ctp_kor_nm)
        }
    }

    Picker(
        modifier = Modifier
            .clip(shape = ShapeDefaults.ExtraSmall)
            .size(width = 160.dp, height = 100.dp)
            .background(Color.Transparent),
        state = sido_state,
        contentDescription = sido_contentDescription,
        gradientColor = MaterialTheme.colorScheme.secondaryContainer,
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = dataList[it].ctp_kor_nm  ,
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
    val siggState = rememberPickerState(dataList.size, repeatItems = false)
    val sido_contentDescription by remember { derivedStateOf { "${siggState.selectedOption + 1}" } }

    LaunchedEffect(key1 =siggState.isScrollInProgress) {
        if (siggState.isScrollInProgress) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        } else {
            onSelectedHandler(VWorldService.LT_C_ADSIGG_INFO, dataList[siggState.selectedOption].sig_cd, dataList[siggState.selectedOption].sig_kor_nm)
        }
    }

    Picker(
        modifier = Modifier
            .clip(shape = ShapeDefaults.ExtraSmall)
            .size(width = 160.dp, height = 100.dp)
            .background(Color.Transparent),
        state = siggState,
        contentDescription = sido_contentDescription,
        gradientColor = MaterialTheme.colorScheme.secondaryContainer,
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = dataList[it].sig_kor_nm  ,
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
