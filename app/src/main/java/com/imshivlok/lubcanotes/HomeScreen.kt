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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imshivlok.lubcanotes.ui.theme.*

@Composable
fun HomeScreen(
    userName: String,
    modifier: Modifier = Modifier
) {
    // Local state tracking to handle sub-navigation inside the Home branch
    // "" = Main Dashboard view, "Notes" = Semester Cards view
    var currentSubView by remember { mutableStateOf("") }

    if (currentSubView == "Notes") {
        // --- NOTES SUB-VIEW (3 ROWS x 2 COLUMNS) ---
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(ClaudeBackground)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header with Back Button arrow alternative
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ClaudeAccent,
                    modifier = Modifier
                        .clickable { currentSubView = "" }
                        .padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = "Select Semester",
                        style = MaterialTheme.typography.headlineMedium,
                        color = ClaudeTextMain
                    )
                    Text(
                        text = "BCA Academic Syllabus",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ClaudeTextMuted
                    )
                }
            }

            // 3 Rows x 2 Columns Flat Grid Structure
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ClaudeGridCard(title = "Semester 1", modifier = Modifier.weight(1f)) { /* Route to Sem 1 PDFs later */ }
                    ClaudeGridCard(title = "Semester 2", modifier = Modifier.weight(1f)) { /* Route to Sem 2 PDFs later */ }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ClaudeGridCard(title = "Semester 3", modifier = Modifier.weight(1f)) { /* Route to Sem 3 PDFs later */ }
                    ClaudeGridCard(title = "Semester 4", modifier = Modifier.weight(1f)) { /* Route to Sem 4 PDFs later */ }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ClaudeGridCard(title = "Semester 5", modifier = Modifier.weight(1f)) { /* Route to Sem 5 PDFs later */ }
                    ClaudeGridCard(title = "Semester 6", modifier = Modifier.weight(1f)) { /* Route to Sem 6 PDFs later */ }
                }
            }
        }
    } else {
        // --- MAIN DASHBOARD VIEW ---
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(ClaudeBackground)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Welcome Header
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Welcome, $userName",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ClaudeTextMain
                )
                Text(
                    text = "Your workspace is up to date",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ClaudeTextMuted
                )
            }

            // 2. Category Grid
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Clicking Notes sets the local state to shift views seamlessly
                    ClaudeGridCard(title = "Notes", modifier = Modifier.weight(1f)) {
                        currentSubView = "Notes"
                    }
                    ClaudeGridCard(title = "PYQ", modifier = Modifier.weight(1f)) {}
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ClaudeGridCard(title = "Calendar", modifier = Modifier.weight(1f)) {}
                    ClaudeGridCard(title = "Time Table", modifier = Modifier.weight(1f)) {}
                }
            }

            // 3. Downloaded Content Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .border(BorderStroke(1.dp, ClaudeBorder), RoundedCornerShape(12.dp))
                    .background(ClaudeSurface, RoundedCornerShape(12.dp))
                    .clickable { }
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = "Downloaded Content", color = ClaudeTextMain, style = MaterialTheme.typography.titleMedium)
                        Text(text = "View saved offline records", color = ClaudeTextMuted, style = MaterialTheme.typography.bodySmall)
                    }
                    Box(modifier = Modifier.border(BorderStroke(4.dp, ClaudeAccent), RoundedCornerShape(50.dp)))
                }
            }

            // 4. Notices & Circulars Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .border(BorderStroke(1.dp, ClaudeBorder), RoundedCornerShape(12.dp))
                    .background(ClaudeSurface, RoundedCornerShape(12.dp))
                    .padding(24.dp)
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
}

@Composable
fun ClaudeGridCard(title: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(100.dp)
            .border(BorderStroke(1.dp, ClaudeBorder), RoundedCornerShape(12.dp))
            .background(ClaudeSurface, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            color = ClaudeTextMain,
            style = MaterialTheme.typography.titleMedium
        )
    }
}