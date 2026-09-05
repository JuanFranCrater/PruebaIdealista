package com.juanfbenitez.prueba.idealista.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * How the property detail screen should be presented when a property is tapped from the list.
 */
enum class DetailDisplayMode(val prefValue: String) {
    /** Detail lives inside a bottom drawer/panel on top of the list screen (Compose + XML). */
    DRAWER("drawer"),

    /** Detail opens as its own dedicated full screen (the classic navigation destination). */
    FULL_SCREEN("full_screen");

    companion object {
        val Default = FULL_SCREEN

        fun fromPrefValue(value: String?): DetailDisplayMode =
            entries.find { it.prefValue == value } ?: Default
    }
}

/**
 * Persists the user's chosen [DetailDisplayMode] using [SharedPreferences] and exposes it as a
 * [StateFlow] so any screen currently on display (e.g. the list screen hosting the drawer) can
 * react immediately when the user changes the setting from the toolbar's gear icon menu.
 */
class DetailDisplayPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _mode = MutableStateFlow(readMode())
    val mode: StateFlow<DetailDisplayMode> = _mode.asStateFlow()

    fun setMode(mode: DetailDisplayMode) {
        prefs.edit { putString(KEY_DETAIL_DISPLAY_MODE, mode.prefValue) }
        _mode.value = mode
    }

    private fun readMode(): DetailDisplayMode =
        DetailDisplayMode.fromPrefValue(prefs.getString(KEY_DETAIL_DISPLAY_MODE, null))

    private companion object {
        const val PREFS_NAME = "idealista_prefs"
        const val KEY_DETAIL_DISPLAY_MODE = "detail_display_mode"
    }
}
