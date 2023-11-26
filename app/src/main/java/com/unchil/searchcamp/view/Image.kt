package com.unchil.searchcamp.view

import android.R.attr.rotation
import android.R.attr.scaleX
import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.unchil.searchcamp.db.LocalSearchCampDB
import com.unchil.searchcamp.db.SearchCampDB
import com.unchil.searchcamp.shared.LocalPermissionsManager
import com.unchil.searchcamp.shared.PermissionsManager
import com.unchil.searchcamp.ui.theme.SearchCampTheme


@SuppressLint("SuspiciousIndentation")
@Composable
fun ImageViewer(
    data:Any,
    size: Size,
    isZoomable:Boolean = false,
    contentScale:ContentScale = ContentScale.Crop,
    allowHardware:Boolean = true){


    val scale = remember { mutableStateOf(1f) }
    val rotationState = remember { mutableStateOf(0f) }

    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale.value *=  zoomChange
        rotationState.value += rotationChange
        offset += offsetChange
    }


    val boxModifier:Modifier = when(isZoomable) {
            true -> {
                Modifier
                    .fillMaxSize()
                    .transformable(state = state)
            }
            false -> Modifier.fillMaxSize()
        }

    val imageModifier:Modifier  = when(isZoomable) {
        true -> {
            Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY =  scale.value,
                    rotationZ = rotationState.value,
                    translationX = offset.x,
                    translationY = offset.y
                )
        }
        false -> Modifier.fillMaxSize()
    }


        Box(
            contentAlignment = Alignment.Center,
            modifier = boxModifier

        ){
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(data)
                    .size(size)
                    .crossfade(true)
                    .allowHardware(allowHardware)
                    .build()
            )

            when(painter.state){

                is AsyncImagePainter.State.Loading -> {

                    CircularProgressIndicator(
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )


                }
                is AsyncImagePainter.State.Success  -> {

                    Image(

                        painter = painter,
                        contentDescription = null,
                        contentScale = contentScale,
                        modifier = imageModifier

                    )
                }
                is AsyncImagePainter.State.Empty -> {

                }
                is AsyncImagePainter.State.Error -> {

                    Image(

                        imageVector = Icons.Outlined.ImageNotSupported,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = imageModifier

                    )

                }
            }
        }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoPreview(
    modifier: Modifier = Modifier,
    data:Any,
    allowHardware:Boolean = true,
    onClick: (()->Unit )? = null ,
    onLongClick:(()->Unit )? = null
) {


    Box(
        modifier = Modifier
            .then(modifier)
            .height(100.dp)
            .width(100.dp)
            .border(width = 1.dp, color = Color.Black, shape = ShapeDefaults.Small)
            .clip(shape = ShapeDefaults.Small)
            .combinedClickable(
                onLongClick = { onLongClick?.invoke() },
                onClick = { onClick?.invoke() }
            )
    ,
        contentAlignment = Alignment.Center

    ) {
            ImageViewer(data = data, size = Size(600, 600), isZoomable = false, contentScale = ContentScale.Crop, allowHardware = allowHardware)
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PrevViewMemoData(
    modifier: Modifier = Modifier,
){
    val context = LocalContext.current
    val permissionsManager = PermissionsManager()
    val searchCampDB = SearchCampDB.getInstance(context.applicationContext)


    CompositionLocalProvider(LocalPermissionsManager provides permissionsManager) {
        CompositionLocalProvider(LocalSearchCampDB provides searchCampDB) {


            val url1 =
                "https://images.unsplash.com/photo-1544735716-392fe2489ffa?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop"


            SearchCampTheme {
                Surface(
                    modifier = Modifier.background(color = Color.White)
                ) {

                    ImageViewer(data = url1, size = Size.ORIGINAL, isZoomable = false, contentScale = ContentScale.Crop)


                }
            }

        }

    }

}