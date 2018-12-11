package aepsapp.easypay.com.aepsandroid.common

import android.content.Context
import android.content.pm.PackageManager
import com.chrisplus.rootmanager.RootManager
import com.scottyab.rootbeer.RootBeer
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.util.*

/**
 *
 *
 * Created by Viral on 07-07-2017.
 */

object DeviceRootUtils {


    private fun isDeviceRooted(context: Context): Boolean {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3() || checkMethod4(context)
    }

    fun isDeviceRootedFromRootBeer(context: Context): Boolean {
        return (RootBeer(context).isRootedWithoutBusyBoxCheck || isDeviceRooted(context) || RootManager.getInstance().hasRooted()
                || checkForFrameworks() || checkForJarsNLibs())
    }

    private fun checkRootMethod1(): Boolean {
        val buildTags = android.os.Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkRootMethod2(): Boolean {
        val paths = arrayOf("/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su")
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }

    private fun checkRootMethod3(): Boolean {
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val `in` = BufferedReader(InputStreamReader(process!!.inputStream))
            return `in`.readLine() != null
        } catch (t: Throwable) {
            return false
        } finally {
            if (process != null) process.destroy()
        }
    }

    private fun checkMethod4(context: Context): Boolean {
        var isDetacted = false
        val packageManager = context.packageManager
        val appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        for (applicationInfo in appList) {
            if (applicationInfo.packageName == "de.robv.android.xposed.installer") {
                Log.e("HookDetection", "Xposed found on the system.")
                isDetacted = true
            }
            if (applicationInfo.packageName == "com.saurik.substrate") {
                Log.e("HookDetection", "Substrate found on the system.")
                isDetacted = true
            }
        }
        return isDetacted
    }

    private fun checkForFrameworks(): Boolean {
        var isValid = false
        try {
            throw Exception("blah")
        } catch (e: Exception) {
            var zygoteInitCallCount = 0
            for (stackTraceElement in e.stackTrace) {
                if (stackTraceElement.className == "com.android.internal.os.ZygoteInit") {
                    zygoteInitCallCount++
                    if (zygoteInitCallCount == 2) {
                        Log.e("HookDetection", "Substrate is active on the device.")
                        isValid = true
                    }
                }
                if (stackTraceElement.className == "com.saurik.substrate.MS$2" && stackTraceElement.methodName == "invoked") {
                    Log.e("HookDetection", "A method on the stack trace has been hooked using Substrate.")
                    isValid = true
                }
                if (stackTraceElement.className == "de.robv.android.xposed.XposedBridge" && stackTraceElement.methodName == "main") {
                    Log.e("HookDetection", "Xposed is active on the device.")
                    isValid = true
                }
                if (stackTraceElement.className == "de.robv.android.xposed.XposedBridge" && stackTraceElement.methodName == "handleHookedMethod") {
                    Log.e("HookDetection", "A method on the stack trace has been hooked using Xposed.")
                    isValid = true
                }
            }
        }

        return isValid
    }

    private fun checkForJarsNLibs(): Boolean {
        var isDetected = false
        try {
            val libraries = HashSet<String>()
            val mapsFilename = "/proc/" + android.os.Process.myPid() + "/maps"
            val reader = BufferedReader(FileReader(mapsFilename))
            var line = reader.readLine()
            while (line != null) {
                if (line.endsWith(".so") || line.endsWith(".jar")) {
                    val n = line.lastIndexOf(" ")
                    libraries.add(line.substring(n + 1))
                }
                line = reader.readLine()
            }
            for (library in libraries) {
                if (library.contains("com.saurik.substrate")) {
                    Log.e("HookDetection", "Substrate shared object found: $library")
                    isDetected = true
                }
                if (library.contains("XposedBridge.jar")) {
                    Log.e("HookDetection", "Xposed JAR found: $library")
                    isDetected = true
                }
            }
            reader.close()
        } catch (e: Exception) {
            Log.e("HookDetection", e.toString())
        }

        return isDetected
    }
}