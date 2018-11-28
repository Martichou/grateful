package me.martichou.be.grateful.utilities

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import java.io.IOException
import java.util.*

    private val TAG: String = "GeocodingLocation"

    fun getAddressFromLocation(locationAddress: String,
                               context: Context, handler: Handler) {
        val thread = object : Thread() {
            override fun run() {
                val geocoder = Geocoder(context, Locale.getDefault())
                var result: String? = null
                try {
                    val addressList = geocoder.getFromLocationName(locationAddress, 1)
                    if (addressList != null && addressList.size > 0) {
                        val address = addressList[0]
                        val sb = StringBuilder()
                        sb.append(address.latitude).append(",")
                        sb.append(address.longitude)
                        result = sb.toString()
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Unable to connect to Geocoder", e)
                } finally {
                    val message = Message.obtain()
                    message.target = handler
                    if (result != null) {
                        message.what = 1
                        val bundle = Bundle()
                        bundle.putString("address", result)
                        message.data = bundle
                    } else {
                        message.what = 1
                        val bundle = Bundle()
                        result = "Address: " + locationAddress +
                                "\n Unable to get Latitude and Longitude for this address location."
                        bundle.putString("address", result)
                        message.data = bundle
                    }
                    message.sendToTarget()
                }
            }
        }
        thread.start()
    }