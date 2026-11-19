package com.example.gymlog

import androidx.annotation.StringRes

enum class GymLogScreen(@StringRes val title: Int) {
    Home(title = R.string.screen_home),
    History(title = R.string.screen_history),
    Detail(title = R.string.screen_detail),
    AddEdit(title = R.string.screen_add_edit),
    LiveSession(title = R.string.screen_live_session),
    Camera(title = R.string.screen_camera),
    Settings(title = R.string.screen_settings)
}