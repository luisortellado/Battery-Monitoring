package com.batterymonitorapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

class BatteryModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext),
    LifecycleEventListener {

    private var batteryReceiver: BroadcastReceiver? = null

    init {
        reactContext.addLifecycleEventListener(this)
    }

    override fun getName(): String {
        return "BatteryModule"
    }


    @ReactMethod
    fun getBatteryLevel(promise: Promise) {
        try {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = reactApplicationContext.registerReceiver(null, filter)
            val level = batteryStatus?.getIntExtra("level", -1) ?: -1
            val scale = batteryStatus?.getIntExtra("scale", -1) ?: -1
            if (level != -1 && scale != -1) {
                val batteryLevel = (level / scale.toFloat() * 100).toInt()
                promise.resolve(batteryLevel)
            } else {
                promise.reject("ERROR", "Could not retrieve battery level")
            }
        } catch (e: Exception) {
            promise.reject("ERROR", "An error occurred: ${e.message}")
        }
    }

    @ReactMethod
    fun startMonitoringBatteryLevel(promise: Promise) {
        try {
            if (batteryReceiver == null) {
                val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                batteryReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        try {
                            // Extrae los valores del Intent
                            val level = intent.getIntExtra("level", -1)
                            val scale = intent.getIntExtra("scale", -1)

                            // Verifica si los valores son válidos
                            if (level == -1 || scale <= 0) {
                                Log.e("BatteryModule", "Invalid battery level or scale")
                                return
                            }

                            // Calcula el nivel de batería como porcentaje
                            val batteryLevel = (level / scale.toFloat() * 100).toInt()

                            // Emite el evento al lado JavaScript
                            sendEvent("onBatteryLevelChange", batteryLevel)
                        } catch (e: Exception) {
                            Log.e("BatteryModule", "Error processing battery change: ${e.message}")
                        }
                    }
                }
                reactApplicationContext.registerReceiver(batteryReceiver, filter)
                promise.resolve("Battery monitoring started")
            } else {
                promise.resolve("Battery monitoring already active")
            }
        } catch (e: Exception) {
            promise.reject("ERROR", "Failed to start battery monitoring: ${e.message}")
        }
    }

    private fun sendEvent(eventName: String, batteryLevel: Int) {
        val reactContext = reactApplicationContext
        if (reactContext.hasActiveCatalystInstance()) {
            // Crear un WritableMap para los datos
            val params: WritableMap = Arguments.createMap()
            params.putInt("level", batteryLevel)

            // Enviar el evento
            reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(eventName, params)
            Log.d("BatteryModule", "Event emitted: $eventName with level $batteryLevel")
        } else {
            Log.w("BatteryModule", "No active Catalyst instance to send event.")
        }
    }


    @ReactMethod
    fun addListener(eventName: String) {
      
    }

    @ReactMethod
    fun removeListeners(count: Int) {

    }

    override fun onHostResume() {
    
    }

    override fun onHostPause() {
    
    }

    override fun onHostDestroy() {
        batteryReceiver?.let {
            reactApplicationContext.unregisterReceiver(it)
            batteryReceiver = null
        }
    }
}
