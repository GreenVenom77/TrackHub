package com.skewnexus.trackhub.navigation.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.greenvenom.core_navigation.routes.Destination
import com.skewnexus.trackhub.R
import com.trackhub.feat_hub.presentation.routes.HubDest
import com.trackhub.feat_menu.presentation.routes.MenuDest
import com.trackhub.feat_notifications.presentation.routes.NotificationsDest

enum class BottomDestination(
    @param:DrawableRes val icon: Int,
    @param:StringRes val label: Int,
    val destination: Destination
) {
    MyHubs(
        icon = R.drawable.home_ic,
        label = R.string.my_hubs,
        destination = HubDest.OwnedHubs()
    ),
    SharedHubs(
        icon = R.drawable.shared_ic,
        label = R.string.shared_hubs,
        destination = HubDest.SharedHubs()
    ),
    Notifications(
        icon = R.drawable.notifications_ic,
        label = R.string.notifications,
        destination = NotificationsDest.Notifications
    ),
    Menu(
        icon = R.drawable.menu_ic,
        label = R.string.menu,
        destination = MenuDest.Menu
    );
}