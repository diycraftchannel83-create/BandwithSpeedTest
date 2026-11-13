package com.winsmediatama.speedtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF081426)
private val Panel = Color(0xFF12243A)
private val Cyan = Color(0xFF35D7F2)
private val Muted = Color(0xFF9CB0C7)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SpeedTestHome() }
    }
}

@Composable
private fun SpeedTestHome() {
    MaterialTheme(colorScheme = darkColorScheme(
        background = Navy, surface = Panel, primary = Cyan,
        onBackground = Color.White, onSurface = Color.White
    )) {
        Column(
            modifier = Modifier.fillMaxSize().background(Navy).padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("WINS MEDIATAMA", color = Cyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("BANDWIDTH SPEED TEST", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
            Text("Pengukuran koneksi internet secara transparan", color = Muted, fontSize = 12.sp)
            Spacer(Modifier.height(34.dp))
            Box(
                modifier = Modifier.size(248.dp).background(Panel, RoundedCornerShape(140.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("READY", color = Cyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("—", color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.Light)
                    Text("Mbps", color = Muted, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(26.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("DOWNLOAD", "—", Modifier.weight(1f))
                MetricCard("UPLOAD", "—", Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("PING", "— ms", Modifier.weight(1f))
                MetricCard("JITTER", "— ms", Modifier.weight(1f))
            }
            Spacer(Modifier.height(24.dp))
            Text("Engine pengukuran Cloudflare akan dihubungkan pada tahap integrasi berikutnya.",
                color = Muted, fontSize = 12.sp)
            Spacer(Modifier.weight(1f))
            Text("Tidak ada pengali atau markup hasil pengukuran.", color = Cyan, fontSize = 11.sp)
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier.background(Panel, RoundedCornerShape(16.dp)).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}
