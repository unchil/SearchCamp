package com.unchil.gismemo.view

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.web.*
import com.unchil.searchcamp.BuildConfig
import com.unchil.searchcamp.shared.LocalPermissionsManager
import com.unchil.searchcamp.shared.PermissionsManager
import com.unchil.searchcamp.shared.checkInternetConnected
import com.unchil.searchcamp.shared.view.CheckPermission
import com.unchil.searchcamp.shared.view.PermissionRequiredCompose
import com.unchil.searchcamp.ui.theme.SearchCampTheme
import kotlinx.coroutines.delay
import java.net.URLDecoder
import java.net.URLEncoder


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("SetJavaScriptEnabled", "UnrememberedMutableState")
@Composable
fun SiteWebView(  url:String? = null ) {

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

        val context = LocalContext.current

        var isConnect by remember { mutableStateOf(context.checkInternetConnected()) }

        LaunchedEffect(key1 = isConnect) {
            while (!isConnect) {
                delay(500)
                isConnect = context.checkInternetConnected()
            }
        }

        if(!url.isNullOrEmpty()){
            AnimatedVisibility(visible = isConnect) {

                val webViewNavigator = rememberWebViewNavigator()
                val webViewState = rememberWebViewState(url =    URLDecoder.decode(url, "UTF-8"), additionalHttpHeaders = emptyMap())
                val webViewClient = AccompanistWebViewClient()
                val webChromeClient = AccompanistWebChromeClient()

                WebView(
                    modifier = Modifier.fillMaxSize(),
                    state = webViewState,
                    client = webViewClient,
                    chromeClient = webChromeClient,
                    navigator = webViewNavigator,
                    onCreated = { webView ->
                        with(webView) {
                            settings.run {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                javaScriptCanOpenWindowsAutomatically = false
                            }
                        }
                    }
                )


            }
        }





    }

}


@Preview
@Composable
fun PrevImageWebViewer() {

    val context = LocalContext.current
    val permissionsManager = PermissionsManager()
    val navController = rememberNavController()
    SearchCampTheme {
        Surface(modifier = Modifier,  color = MaterialTheme.colorScheme.background     ) {

            CompositionLocalProvider(LocalPermissionsManager provides permissionsManager) {
                SiteWebView( "http://www.naver.com")

            }


        }
    }




}