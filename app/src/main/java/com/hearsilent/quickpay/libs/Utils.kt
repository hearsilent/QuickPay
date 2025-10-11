package com.hearsilent.quickpay.libs

import android.content.Context
import android.content.pm.PackageManager

object Utils {

    /**
     * @return Application's version name from the `PackageManager`.
     */
    fun getAppVersionName(context: Context): String? {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (_: PackageManager.NameNotFoundException) {
            null
        }
    }
}