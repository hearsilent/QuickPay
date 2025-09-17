package com.hearsilent.quickpay

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile.STATE_INACTIVE
import android.service.quicksettings.TileService
import androidx.core.net.toUri

class TaishinPayTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        qsTile.state = STATE_INACTIVE
        qsTile.updateTile()
    }

    @SuppressLint("StartActivityAndCollapseDeprecated")
    override fun onClick() {
        super.onClick()
        val packageName = "tw.com.taishinbank.ccapp"
        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent!!.data = "cardaily://?stateName=N000002_001&amp;widget=true".toUri()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startActivityAndCollapse(
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                )
            } else {
                startActivityAndCollapse(intent)
            }
        } catch (_: Exception) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = "market://details?id=$packageName".toUri()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startActivityAndCollapse(
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                )
            } else {
                startActivityAndCollapse(intent)
            }
        }
    }
}