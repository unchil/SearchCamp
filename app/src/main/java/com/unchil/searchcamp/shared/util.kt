package com.unchil.searchcamp.shared

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.graphics.RectF
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.speech.RecognizerIntent
import android.util.DisplayMetrics
import android.view.WindowManager
import java.util.Locale
import kotlin.math.roundToInt


val recognizerIntent =  {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

    intent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
    )

    intent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE,
        Locale.getDefault().language
    )

    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk")

}

val chromeIntent: (context: Context, url:String)-> Unit = {context, url ->
    val intent = Intent(Intent.ACTION_VIEW)

    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    intent.setPackage("com.android.chrome")
    intent.data = Uri.parse(url)
    context.startActivity(intent)
}



const val MILLISEC_CHECK = 9999999999
const val MILLISEC_DIGIT = 1L
const val MILLISEC_CONV_DIGIT = 1000L
const val yyyyMMdd = "yyyyMMdd"
const val yyyyMMddHHmmE = "yyyy/MM/dd HH:mm E"
const val yyyyMMddHHmmssE = "yyyy/MM/dd HH:mm:ss E"
const val EEEHHmmss = "EEE HH:mm:ss"
const val yyyyMMddHHmm = "yyyy/MM/dd HH:mm"
const val HHmmss = "HH:mm:ss"


@SuppressLint("SimpleDateFormat")
fun UnixTimeToString(time: Long, format: String): String{
    val UNIXTIMETAG_SECTOMILI
            = if( time > MILLISEC_CHECK) MILLISEC_DIGIT else MILLISEC_CONV_DIGIT

    return SimpleDateFormat(format)
        .format(time * UNIXTIMETAG_SECTOMILI )
        .toString()
}

