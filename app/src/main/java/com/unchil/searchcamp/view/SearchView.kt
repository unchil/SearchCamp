package com.unchil.searchcamp.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.unchil.searchcamp.data.RepositoryProvider
import com.unchil.searchcamp.data.VWorldService
import com.unchil.searchcamp.db.LocalSearchCampDB
import com.unchil.searchcamp.db.SearchCampDB
import com.unchil.searchcamp.db.entity.SiDo_TBL
import com.unchil.searchcamp.shared.PermissionsManager
import com.unchil.searchcamp.shared.checkInternetConnected
import com.unchil.searchcamp.shared.recognizerIntent
import com.unchil.searchcamp.shared.view.CheckPermission
import com.unchil.searchcamp.shared.view.PermissionRequiredCompose
import com.unchil.searchcamp.ui.theme.SearchCampTheme
import com.unchil.searchcamp.viewmodel.LocationPickerViewModel
import kotlinx.coroutines.delay


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun SearchCampView(
    modifier:Modifier  = Modifier,
    onSearchEventHandler:(   administrativeDistrictSiDoCode:String,
                       administrativeDistrictSiGunGu:String,
                       searchTitle:String?)-> Unit,
    onMessage:(() -> Unit)? = null
){


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


        val viewModel = remember {
            LocationPickerViewModel(
                repository = RepositoryProvider.getRepository().apply { database = db })
        }

        var isConnect by remember { mutableStateOf(context.checkInternetConnected()) }

        LaunchedEffect(key1 = isConnect) {
            while (!isConnect) {
                delay(500)
                isConnect = context.checkInternetConnected()
            }
        }


        val sidoData = viewModel.sidoListStateFlow.collectAsState()
        val siggData = viewModel.sigunguListStateFlow.collectAsState()

        var administrativeDistrictTitle by remember {
            mutableStateOf("행정구역")
        }

        var administrativeDistrictSiDoCode by remember {
            mutableStateOf("0")
        }

        var administrativeDistrictSiDo by remember {
            mutableStateOf("현위치")
        }

        var administrativeDistrictSiGunGu by remember {
            mutableStateOf("")
        }

        var query_title by rememberSaveable {
            mutableStateOf("")
        }


        LaunchedEffect(key1 = viewModel) {

            if (isConnect) {

                viewModel.onEvent(
                    LocationPickerViewModel.Event.RecvAdministrativeDistrict(
                        VWorldService.LT_C_ADSIDO_INFO
                    )
                )

                viewModel.onEvent(
                    LocationPickerViewModel.Event.RecvAdministrativeDistrict(
                        VWorldService.LT_C_ADSIGG_INFO
                    )
                )
            }

            viewModel.onEvent(
                LocationPickerViewModel.Event.GetSiDo
            )

        }

        val hapticFeedback = LocalHapticFeedback.current

        var isHapticProcessing by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = isHapticProcessing) {
            if (isHapticProcessing) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isHapticProcessing = false
            }
        }


        val isVisible: MutableState<Boolean> = remember { mutableStateOf(true) }

        val recognizerIntent = remember { recognizerIntent }

        val startLauncherRecognizerIntent = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {

            if (it.resultCode == Activity.RESULT_OK) {

                val result =
                    it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                query_title = query_title + result?.get(0).toString() + " "

            }
        }

        val onSelectedHandler: (type: VWorldService, code: String, name: String) -> Unit =
            { type, code, name ->
                when (type) {
                    VWorldService.LT_C_ADSIDO_INFO -> {
                        administrativeDistrictSiDoCode = code
                        administrativeDistrictSiDo = name
                        viewModel.onEvent(LocationPickerViewModel.Event.GetSiGunGu(upCode = code))
                    }

                    VWorldService.LT_C_ADSIGG_INFO -> {
                        administrativeDistrictSiGunGu = name
                        administrativeDistrictTitle = administrativeDistrictSiDo + "  " + name
                    }
                }
            }

        LaunchedEffect(key1 = siggData.value) {
            if (siggData.value.isNotEmpty()) {

                administrativeDistrictSiGunGu =  siggData.value.first().sig_kor_nm
                administrativeDistrictTitle =  administrativeDistrictSiDo + " " + siggData.value.first().sig_kor_nm
            }

            if (administrativeDistrictSiDo.equals("현위치")) {
                administrativeDistrictSiGunGu = "현위치"
                administrativeDistrictTitle = "현위치"
            }

        }


        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .then(other = modifier)
                .width(400.dp)
                .height(480.dp)
                .clip(shape = ShapeDefaults.ExtraSmall)
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                text ="Search Camping Site",
                modifier =  Modifier
                    .padding(vertical = 10.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge
            )

            HorizontalDivider()

            WeatherContent()

            HorizontalDivider()

            SearchBar(
                query = query_title,
                onQueryChange = {
                    query_title = it
                },
                onSearch = {query_title ->
                    onSearchEventHandler(
                        administrativeDistrictSiDoCode,
                        administrativeDistrictSiGunGu,
                        query_title
                    )
                },
                active = isVisible.value,
                onActiveChange = {
                    isVisible.value = it
                },
                placeholder = {
                    Text(
                        text = "캠핑장 이름 검색",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier
                    .width(400.dp)
                    .height(70.dp)
                    .clip(shape = ShapeDefaults.ExtraSmall),
                leadingIcon = {
                    IconButton(
                        modifier = Modifier,
                        onClick = {
                            isHapticProcessing = true

                            onSearchEventHandler(
                                administrativeDistrictSiDoCode,
                                administrativeDistrictSiGunGu,
                                query_title
                            )

                        },
                        content = {
                            Icon(
                                modifier = Modifier,
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search"
                            )
                        }
                    )


                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            modifier = Modifier,
                            onClick = {
                                isHapticProcessing = true
                                startLauncherRecognizerIntent.launch(recognizerIntent())
                            },
                            content = {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Outlined.Mic,
                                    contentDescription = "SpeechToText"
                                )
                            }
                        )


                        IconButton(
                            modifier = Modifier,
                            onClick = {
                                isHapticProcessing = true
                                query_title = ""
                                onMessage?.let {
                                    it()
                                }
                            },
                            content = {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Outlined.Replay,
                                    contentDescription = "Clear"
                                )
                            }
                        )


                    }
                },
                tonalElevation = 2.dp,
                colors = SearchBarDefaults.colors(
                  //  containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                    containerColor = Color.Transparent
                )
            ) { }


            HorizontalDivider()

            AnimatedVisibility(visible = sidoData.value.size > 0) {

                val dataList = mutableListOf<SiDo_TBL>()

                dataList.add(
                    SiDo_TBL(
                        ctprvn_cd = "0",
                        ctp_kor_nm = "현위치",
                        ctp_eng_nm = "CurrentLocation"
                    )
                )
                dataList.addAll(1, sidoData.value)

                Column(
                    modifier = Modifier
                        .clip(shape = ShapeDefaults.ExtraSmall)
                        .width(400.dp)
                        .height(160.dp),
               //         .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = administrativeDistrictTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.size(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                        ,
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {


                        AdministrativeDistrictSiDoPicker(
                            //    dataList = sidoData.value,
                            dataList = dataList,
                            onSelectedHandler = onSelectedHandler,
                            //      onEvent = viewModel::onEvent
                        )


                        if (siggData.value.size > 0 && administrativeDistrictSiDo != "현위치") {

                            Spacer(modifier = Modifier.size(20.dp))

                            AdministrativeDistrictSiGunGuPicker(
                                dataList = siggData.value,
                                onSelectedHandler = onSelectedHandler
                            )

                        }

                    }


                }


            }


        }

    }

}


@Preview(showBackground = false, showSystemUi = false)
@Composable
fun SearchViewPreview() {

    val context = LocalContext.current
    val permissionsManager = PermissionsManager()
    val searchCampDB2 = SearchCampDB.getInstance(context.applicationContext)

    val str = "용인시"
    val splitStr = str.split(" ", limit = 2)

    SearchCampTheme {
        Surface( modifier = Modifier.fillMaxSize(),  color = MaterialTheme.colorScheme.background  ) {

            Column {
                splitStr.forEach {
                    Text(text = it)
                }

            }


/*
            CompositionLocalProvider(LocalPermissionsManager provides permissionsManager) {
                CompositionLocalProvider(LocalSearchCampDB provides searchCampDB2) {
                      // SearchCampView()
                }
            }

 */
        }
    }
}
