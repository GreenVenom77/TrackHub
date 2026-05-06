package com.trackhub.feat_navigation.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.trackhub.feat_navigation.R
import com.trackhub.feat_navigation.routes.Screen

enum class BottomDestination(
    @param:DrawableRes val icon: Int,
    @param:StringRes val label: Int,
    val destination: Screen
) {
    MyHubs(
        icon = R.drawable.home_ic,
        label = R.string.my_hubs,
        destination = Screen.OwnedHubs()
    ),
    SharedHubs(
        icon = R.drawable.shared_ic,
        label = R.string.shared_hubs,
        destination = Screen.SharedHubs()
    ),
    Notifications(
        icon = R.drawable.notifications_ic,
        label = R.string.notifications,
        destination = Screen.Notifications
    ),
    Menu(
        icon = R.drawable.menu_ic,
        label = R.string.menu,
        destination = Screen.Menu
    );
}