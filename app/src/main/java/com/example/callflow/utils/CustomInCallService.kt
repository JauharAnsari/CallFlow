package com.example.callflow.utils

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService
import com.example.callflow.ui.incall.InCallActivity

class CustomInCallService : InCallService() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        
        // When a call is added, we want to show our own custom call screen
        val intent = Intent(this, InCallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            // Pass the call object ID or store it globally (for simplicity, we use a global listener)
            currentCall = call
        }
        startActivity(intent)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        if (currentCall == call) {
            currentCall = null
        }
    }

    companion object {
        var currentCall: Call? = null
        var instance: CustomInCallService? = null
        
        fun setMuted(muted: Boolean) {
            instance?.setMuted(muted)
        }
        
        fun setSpeaker(enabled: Boolean) {
            val route = if (enabled) android.telecom.CallAudioState.ROUTE_SPEAKER 
                        else android.telecom.CallAudioState.ROUTE_EARPIECE
            instance?.setAudioRoute(route)
        }
    }
}
