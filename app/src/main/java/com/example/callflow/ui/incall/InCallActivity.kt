package com.example.callflow.ui.incall

import android.os.Bundle
import android.telecom.Call
import android.telecom.VideoProfile
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.callflow.ui.theme.CallFlowTheme
import com.example.callflow.utils.CustomInCallService
import kotlinx.coroutines.delay

class InCallActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val call = CustomInCallService.currentCall
        if (call == null) {
            finish()
            return
        }

        setContent {
            CallFlowTheme {
                InCallScreen(call) { finish() }
            }
        }
    }
}

@Composable
fun InCallScreen(call: Call, onFinish: () -> Unit) {
    var callState by remember { mutableStateOf(call.state) }
    var duration by remember { mutableStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(false) }
    
    val number = call.details.handle.schemeSpecificPart

    // Listen for call state changes
    DisposableEffect(call) {
        val callback = object : Call.Callback() {
            override fun onStateChanged(call: Call, state: Int) {
                callState = state
                if (state == Call.STATE_DISCONNECTED) {
                    onFinish()
                }
            }
        }
        call.registerCallback(callback)
        onDispose {
            call.unregisterCallback(callback)
        }
    }

    // Timer logic
    LaunchedEffect(Unit) {
        while (true) {
            if (callState == Call.STATE_ACTIVE) {
                duration++
            }
            delay(1000)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = when (callState) {
                        Call.STATE_ACTIVE -> "Live"
                        Call.STATE_RINGING -> "Incoming Call..."
                        Call.STATE_DIALING -> "Calling..."
                        Call.STATE_CONNECTING -> "Connecting..."
                        else -> "Call"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = if (callState == Call.STATE_ACTIVE) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                    fontWeight = if (callState == Call.STATE_ACTIVE) FontWeight.Bold else FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = number,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatDuration(duration),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // Call Controls (Mute, Speaker)
            if (callState == Call.STATE_ACTIVE) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                         IconButton(
                            onClick = {
                                isMuted = !isMuted
                                CustomInCallService.setMuted(isMuted)
                            },
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mute",
                                modifier = Modifier.size(32.dp),
                                tint = if (isMuted) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(text = "Mute", style = MaterialTheme.typography.labelSmall)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                isSpeakerOn = !isSpeakerOn
                                CustomInCallService.setSpeaker(isSpeakerOn)
                            },
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                imageVector = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = "Speaker",
                                modifier = Modifier.size(32.dp),
                                tint = if (isSpeakerOn) Color.Green else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(text = "Speaker", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // Answer/Hangup Buttons
            if (callState == Call.STATE_RINGING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Reject Button
                    FloatingActionButton(
                        onClick = {
                            call.disconnect()
                            onFinish()
                        },
                        containerColor = Color.Red,
                        contentColor = Color.White,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(Icons.Default.CallEnd, contentDescription = "Reject", modifier = Modifier.size(40.dp))
                    }

                    // Answer Button
                    FloatingActionButton(
                        onClick = {
                            call.answer(VideoProfile.STATE_AUDIO_ONLY)
                        },
                        containerColor = Color.Green,
                        contentColor = Color.White,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Answer", modifier = Modifier.size(40.dp))
                    }
                }
            } else {
                FloatingActionButton(
                    onClick = {
                        call.disconnect()
                        onFinish()
                    },
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remSeconds)
}
