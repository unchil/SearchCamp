package com.unchil.searchcamp.navigation

import android.os.Bundle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.unchil.searchcamp.R

fun NavHostController.navigateTo(route: String) =
    this.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(
            this@navigateTo.graph.findStartDestination().id
        ) {
            saveState = false
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }

fun NavHostController.navigateToNotSave(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateToNotSave.graph.findStartDestination().id
        ) {
            saveState = false
        }
        launchSingleTop = true
        restoreState = false
    }


val resultScreens:List<SearchCampDestinations> = listOf(
    SearchCampDestinations.resultListScreen,
    SearchCampDestinations.resultMapScreen
)

val mainScreens:List<SearchCampDestinations> = listOf(
    SearchCampDestinations.searchScreen,
    SearchCampDestinations.resultNavScreen
)



sealed class SearchCampDestinations(
    val route:String,
    val name:Int = 0,
    val icon: ImageVector? = null,
){


    object searchScreen : SearchCampDestinations(
        route = "searchScreen",
        name = R.string.mainmenu_search,
        icon = Icons.AutoMirrored.Outlined.FormatListBulleted
    )

    object resultScreen : SearchCampDestinations(
        route = "resultScreen?${ARG_NAME_SiDoCode}={$ARG_NAME_SiDoCode}&${ARG_NAME_SiGunGu}={$ARG_NAME_SiGunGu}&${ARG_NAME_SearchTitle}={$ARG_NAME_SearchTitle}",
        name = R.string.mainmenu_result,
        icon = Icons.AutoMirrored.Outlined.Article
    ){
        fun createRoute(
            administrativeDistrictSiDoCode:String,
            administrativeDistrictSiGunGu:String,
            searchTitle:String? = null
        ):String{
         return    "resultScreen?${ARG_NAME_SiDoCode}=${administrativeDistrictSiDoCode}&${ARG_NAME_SiGunGu}=${administrativeDistrictSiGunGu}&${ARG_NAME_SearchTitle}=${searchTitle}"
        }

    }



    object resultNavScreen : SearchCampDestinations(
        route = "resultNavScreen?${ARG_NAME_SiDoCode}={$ARG_NAME_SiDoCode}&${ARG_NAME_SiGunGu}={$ARG_NAME_SiGunGu}&${ARG_NAME_SearchTitle}={$ARG_NAME_SearchTitle}",
        name = R.string.mainmenu_result,
        icon = Icons.AutoMirrored.Outlined.Article
    ){
        fun createRoute(
            administrativeDistrictSiDoCode:String,
            administrativeDistrictSiGunGu:String,
            searchTitle:String? = null
        ):String{
            return    "resultNavScreen?${ARG_NAME_SiDoCode}=${administrativeDistrictSiDoCode}&${ARG_NAME_SiGunGu}=${administrativeDistrictSiGunGu}&${ARG_NAME_SearchTitle}=${searchTitle}"
        }

    }



    object resultListScreen : SearchCampDestinations(
        route = "resultListScreen?${ARG_NAME_SiDoCode}={$ARG_NAME_SiDoCode}&${ARG_NAME_SiGunGu}={$ARG_NAME_SiGunGu}&${ARG_NAME_SearchTitle}={$ARG_NAME_SearchTitle}",
        name = R.string.resultmenu_list,
        icon = Icons.AutoMirrored.Outlined.List
    ){
        fun createRoute(
            administrativeDistrictSiDoCode:String,
            administrativeDistrictSiGunGu:String,
            searchTitle:String? = null
        ):String{
            return    "resultListScreen?${ARG_NAME_SiDoCode}=${administrativeDistrictSiDoCode}&${ARG_NAME_SiGunGu}=${administrativeDistrictSiGunGu}&${ARG_NAME_SearchTitle}=${searchTitle}"
        }

    }

    object resultMapScreen : SearchCampDestinations(
        route = "resultMapScreen?${ARG_NAME_SiDoCode}={$ARG_NAME_SiDoCode}&${ARG_NAME_SiGunGu}={$ARG_NAME_SiGunGu}&${ARG_NAME_SearchTitle}={$ARG_NAME_SearchTitle}",
        name = R.string.resultmenu_map,
        icon = Icons.Outlined.Map
    ){
        fun createRoute(
            administrativeDistrictSiDoCode:String,
            administrativeDistrictSiGunGu:String,
            searchTitle:String? = null
        ):String{
            return    "resultMapScreen?${ARG_NAME_SiDoCode}=${administrativeDistrictSiDoCode}&${ARG_NAME_SiGunGu}=${administrativeDistrictSiGunGu}&${ARG_NAME_SearchTitle}=${searchTitle}"
        }

    }


    companion object {
        const val ARG_NAME_SiDoCode:String = "administrativeDistrictSiDoCode"
        const val ARG_NAME_SiGunGu:String = "administrativeDistrictSiGunGu"
        const val ARG_NAME_SearchTitle:String = "searchTitle"

        fun getSiDoCodeFromArgs(bundle: Bundle?): String {
            return bundle?.getString(ARG_NAME_SiDoCode) ?: ""
        }

        fun getSiGunGuFromArgs(bundle: Bundle?): String {
            return bundle?.getString(ARG_NAME_SiGunGu) ?: ""
        }

        fun getSearchTitleFromArgs(bundle: Bundle?): String {
            return bundle?.getString(ARG_NAME_SearchTitle) ?: ""
        }

    }

}

