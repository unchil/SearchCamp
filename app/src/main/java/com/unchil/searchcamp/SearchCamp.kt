package com.unchil.searchcamp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.unchil.searchcamp.api.UnsplashSizingInterceptor

class SearchCamp : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(UnsplashSizingInterceptor)
            }
            .build()

    }
}
