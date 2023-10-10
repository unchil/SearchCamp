package com.unchil.searchcamp.shared.view

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil.size.Size
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.material3.*
import com.unchil.searchcamp.R
import com.unchil.searchcamp.shared.LocalPermissionsManager
import com.unchil.searchcamp.shared.PermissionsManager


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckPermission( multiplePermissionsState: MultiplePermissionsState){

    val context = LocalContext.current
    val permissionsManager = LocalPermissionsManager.current
    val coroutineScope = rememberCoroutineScope()
    val permissionAction = permissionsManager.action.collectAsState()

    when( permissionAction.value) {

        PermissionsManager.Action.NO_ACTION -> {  }

        PermissionsManager.Action.SHOW_RATIONALE -> {
            PermissionRationaleDialog(
                message = context.resources.getString(R.string.permission_rationale_msg),
                title =  context.resources.getString(R.string.permission_rationale_title),
                primaryButtonText = "REQUEST",
                onOkTapped = {
                    permissionsManager.getPermissionActionNew(multiplePermissionsState, coroutineScope)
                }
            )
        }

        PermissionsManager.Action.SHOW_SETTING -> {
            ShowGotoSettingsDialog(
                title = context.resources.getString(R.string.permission_setting_title),
                message = context.resources.getString(R.string.permission_setting_msg),
                onSettingsTapped = {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:" + context.packageName)
                        context.startActivity(this)
                    }
                }
            )
        }

    }


}


@Composable
fun PermissionRationaleDialog(
    message: String,
    title: String,
    primaryButtonText: String,
    onOkTapped: () -> Unit
) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Text(
                        text = primaryButtonText.uppercase(),
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .clickable { onOkTapped() },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        )

}


@Composable
fun ShowGotoSettingsDialog(
    title: String,
    message: String,
    onSettingsTapped: () -> Unit,
) {

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = {

        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        },
        confirmButton = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = context.resources.getString(R.string.showGotoSettingsDialog_title),
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .clickable { onSettingsTapped() },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}



@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequiredCompose(
    isGranted : Boolean,
    multiplePermissions : List<String>,
    viewType:PermissionRequiredComposeFuncName? = null,
    content: @Composable () -> Unit) {


    if(isGranted){
        content()
    }else{

        val (id:Int, message:String ) = if ( viewType == null) {
            Pair(
                PermissionRequiredComposeFuncName.Weather.getDrawable(),
                "PERMISSION REQUEST"
            )
        } else {
            when (viewType) {


                PermissionRequiredComposeFuncName.Weather -> {
                    Pair(
                        PermissionRequiredComposeFuncName.Weather.getDrawable(),
                        PermissionRequiredComposeFuncName.Weather.getTitle()
                    )
                }
                PermissionRequiredComposeFuncName.MemoMap ->  {
                    Pair(
                        PermissionRequiredComposeFuncName.MemoMap.getDrawable(),
                        PermissionRequiredComposeFuncName.MemoMap.getTitle()
                    )
                }

            }
        }

        val permissionsManager = LocalPermissionsManager.current
        val multiplePermissionsState = rememberMultiplePermissionsState(
            multiplePermissions
        )
        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
                .padding(10.dp)
                .border(width = 1.dp, color = MaterialTheme.colorScheme.onSurface, shape = ShapeDefaults.Small),
            contentAlignment = Alignment.Center
        ) {

            ImageViewer(data = id, size = Size.ORIGINAL)
            Button(
                onClick = {
                    permissionsManager.getPermissionActionNew(multiplePermissionsState, coroutineScope)
                }
            ){
                Text(text = message)
            }
        }



    }
}

enum class PermissionRequiredComposeFuncName {
    Weather, MemoMap
}

fun PermissionRequiredComposeFuncName.getTitle(): String {
    return when(this.name) {
        PermissionRequiredComposeFuncName.Weather.name -> {"REQUEST: LOCATION"}
        PermissionRequiredComposeFuncName.MemoMap.name -> {"REQUEST : LOCATION"}
        else -> {"PERMISSION REQUEST :"}
    }
}

fun PermissionRequiredComposeFuncName.getDrawable(): Int {
    return when(this.name) {

        PermissionRequiredComposeFuncName.Weather.name -> {
            R.drawable.weathercontent}

        PermissionRequiredComposeFuncName.MemoMap.name -> {
            R.drawable.memomap}
        else -> {0}
    }
}
