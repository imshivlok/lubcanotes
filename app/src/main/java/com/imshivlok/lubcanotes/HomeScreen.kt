package com.imshivlok.lubcanotes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imshivlok.lubcanotes.ui.theme.*

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClaudeBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Welcome Header
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Welcome, User",
                style = MaterialTheme.typography.headlineMedium,
                color = ClaudeTextMain
            )
            Text(
                text = "Your workspace is up to date",
                style = MaterialTheme.typography.bodyMedium,
                color = ClaudeTextMuted
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 2. Claude Minimalist Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ClaudeGridCard(title = "Notes", modifier = Modifier.weight(1f)) {}
            ClaudeGridCard(title = "PYQ", modifier = Modifier.weight(1f)) {}
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ClaudeGridCard(title = "Calendar", modifier = Modifier.weight(1f)) {}
            ClaudeGridCard(title = "Time Table", modifier = Modifier.weight(1f)) {}
        }

        // 3. Downloaded Content Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .border(BorderStroke(1.dp, ClaudeBorder), RoundedCornerShape(12.dp))
                .background(ClaudeSurface, RoundedCornerShape(12.dp))
                .clickable { }
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Downloaded Content", color = ClaudeTextMain, style = MaterialTheme.typography.titleMedium)
                    Text(text = "View saved offline records", color = ClaudeTextMuted, style = MaterialTheme.typography.bodySmall)
                }
                // Small indicator dot in Terracotta
                Box(modifier = Modifier.border(BorderStroke(4.dp, ClaudeAccent), RoundedCornerShape(50.dp)))
            }
        }

        // 4. Notices & Circulars Box (240.dp for content)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .border(BorderStroke(1.dp, ClaudeBorder), RoundedCornerShape(12.dp))
                .background(ClaudeSurface, RoundedCornerShape(12.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Notices & Circulars",
                    color = ClaudeAccent,
                    style = MaterialTheme.typography.titleMedium
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "• B.C.A examination schedules updated for current batches.", color = ClaudeTextMain, style = MaterialTheme.typography.bodyMedium)
                    Text(text = "• Official campus holiday declaration notice appended.", color = ClaudeTextMuted, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun ClaudeGridCard(title: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(100.dp)
            .border(BorderStroke(1.dp, ClaudeBorder), RoundedCornerShape(12.dp))
            .background(ClaudeSurface, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            color = ClaudeTextMain,
            style = MaterialTheme.typography.titleMedium
        )
    }
}