package com.solerforge.lumeria.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import com.solerforge.lumeria.BuildConfig
import java.io.File
import java.security.MessageDigest

object SecurityUtils {

    /**
     * Checks if the device is rooted by looking for the 'su' binary and common root apps.
     */
    fun isDeviceRooted(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }

    /**
     * Basic check for common emulator indicators.
     */
    fun isRunningOnEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator")
    }

    /**
     * Checks if a debugger is attached or if ADB is enabled (if needed).
     */
    fun isDebuggerAttached(): Boolean {
        return android.os.Debug.isDebuggerConnected()
    }

    /**
     * Checks if the app was signed with the expected certificate.
     * Replace EXPECTED_SIGNATURE with your release SHA-256 fingerprint.
     */
    fun isAppSignatureValid(context: Context): Boolean {
        if (BuildConfig.DEBUG) return true

        val currentSignature = getAppSignature(context)
        Log.d("SecurityUtils", "Current App Signature: $currentSignature")
        
        // TODO: Replace with your REAL release SHA-256 Base64 encoded signature
        val RELEASE_SIGNATURE = "REPLACE_WITH_RELEASE_SIGNATURE"
        return currentSignature == RELEASE_SIGNATURE
    }

    private fun getAppSignature(context: Context): String? {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            signatures?.firstOrNull()?.let {
                val md = MessageDigest.getInstance("SHA-256")
                md.update(it.toByteArray())
                Base64.encodeToString(md.digest(), Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Returns true if any security threat is detected.
     */
    fun isEnvironmentInsecure(context: Context): Boolean {
        // We log them separately but return a single status
        val rooted = isDeviceRooted()
        val emulator = isRunningOnEmulator()
        val debugger = isDebuggerAttached()
        
        return rooted || emulator || debugger
    }
}
