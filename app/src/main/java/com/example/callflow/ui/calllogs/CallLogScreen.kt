package com.example.callflow.ui.calllogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallMissed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.callflow.data.CallLogEntry
import com.example.callflow.viewmodel.CallLogViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CallLogScreen(
    viewModel: CallLogViewModel,
    onCallClick: (String) -> Unit
) {
    val callLogs by viewModel.callLogs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCallLogs()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(callLogs) { entry ->
            CallLogItem(entry = entry) {
                onCallClick(entry.number)
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
fun CallLogItem(entry: CallLogEntry, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val callDate = dateFormat.format(Date(entry.date))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (icon, color) = when (entry.type) {
            android.provider.CallLog.Calls.INCOMING_TYPE -> Icons.Default.CallReceived to Color.Blue
            android.provider.CallLog.Calls.OUTGOING_TYPE -> Icons.Default.CallMade to Color.Green
            android.provider.CallLog.Calls.MISSED_TYPE -> Icons.Default.CallMissed to Color.Red
            else -> Icons.Default.CallReceived to Color.Gray
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.name ?: entry.number,
                style = MaterialTheme.typography.bodyLarge
            )
            if (entry.name != null) {
                Text(
                    text = entry.number,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                text = callDate,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Text(
            text = entry.duration,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
