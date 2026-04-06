package com.tomtruyen.orkestr.features.automation.service

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build

data class InstalledAppOption(val packageName: String, val label: String)

class InstalledAppService(private val installedAppsProvider: () -> List<InstalledAppOption>) {
    fun loadInstalledApps(): List<InstalledAppOption> = runCatching {
        installedAppsProvider()
            .distinctBy(InstalledAppOption::packageName)
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, InstalledAppOption::label))
    }.getOrElse { error ->
        if (error is SecurityException) emptyList() else throw error
    }

    companion object {
        fun create(packageManager: PackageManager): InstalledAppService = InstalledAppService(
            installedAppsProvider = {
                queryLauncherApps(packageManager).mapNotNull { resolveInfo ->
                    val packageName = resolveInfo.activityInfo?.packageName ?: return@mapNotNull null
                    InstalledAppOption(
                        packageName = packageName,
                        label = resolveInfo.loadLabel(packageManager).toString().ifBlank { packageName },
                    )
                }
            },
        )

        private fun queryLauncherApps(packageManager: PackageManager): List<ResolveInfo> {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.queryIntentActivities(intent, 0)
            }
        }
    }
}
