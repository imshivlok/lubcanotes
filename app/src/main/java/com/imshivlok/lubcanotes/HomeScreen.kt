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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.imshivlok.lubcanotes.ui.theme.*

@Composable
fun HomeScreen(
    userName: String,
    modifier: Modifier = Modifier
) {
    // Local routing state tracking inside the Home destination branch
    // "" = Main Dashboard, "Notes" = Semesters Grid, "Subjects" = Subjects List, "PYQ" = PYQ Timeline
    var currentSubView by remember { mutableStateOf("") }
    var selectedSemesterLabel by remember { mutableStateOf("") }

    when (currentSubView) {
        "Notes" -> {
            // --- NOTES SUB-VIEW (3 ROWS x 2 COLUMNS) ---
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(ClaudeBackground)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header with Back Navigation Arrow
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
                        ClaudeGridCard(title = "Semester 1", modifier = Modifier.weight(1f)) {
                            selectedSemesterLabel = "Semester 1"
                            currentSubView = "Subjects"
                        }
                        ClaudeGridCard(title = "Semester 2", modifier = Modifier.weight(1f)) {
                            selectedSemesterLabel = "Semester 2"
                            currentSubView = "Subjects"
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ClaudeGridCard(title = "Semester 3", modifier = Modifier.weight(1f)) {
                            selectedSemesterLabel = "Semester 3"
                            currentSubView = "Subjects"
                        }
                        ClaudeGridCard(title = "Semester 4", modifier = Modifier.weight(1f)) {
                            selectedSemesterLabel = "Semester 4"
                            currentSubView = "Subjects"
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ClaudeGridCard(title = "Semester 5", modifier = Modifier.weight(1f)) {
                            selectedSemesterLabel = "Semester 5"
                            currentSubView = "Subjects"
                        }
                        ClaudeGridCard(title = "Semester 6", modifier = Modifier.weight(1f)) {
                            selectedSemesterLabel = "Semester 6"
                            currentSubView = "Subjects"
                        }
                    }
                }
            }
        }

        "Subjects" -> {
            // --- SUBJECTS LIST VIEW FOR NOTES (5 CARDS, 1 ROW WIDE EACH) ---
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(ClaudeBackground)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "←",
                        style = MaterialTheme.typography.headlineMedium,
                        color = ClaudeAccent,
                        modifier = Modifier
                            .clickable { currentSubView = "Notes" }
                            .padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = selectedSemesterLabel,
                            style = MaterialTheme.typography.headlineMedium,
                            color = ClaudeTextMain
                        )
                        Text(
                            text = "Core Course Modules",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ClaudeTextMuted
                        )
                    }
                }

                // 5 full-width cards stack
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    (1..5).forEach { subjectNum ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .border(BorderStroke(1.dp, ClaudeBorder), RoundedCornerShape(12.dp))
                                .background(ClaudeSurface, RoundedCornerShape(12.dp))
                                .clickable { /* Action to open subject details later */ }
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = "Subject $subjectNum",
                                color = ClaudeTextMain,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }

        "PYQ" -> {
            // --- PYQ SCREEN (6 CARDS, 1 ROW WIDE EACH, LOW FONT WEIGHT EXTENSION TEXT) ---
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(ClaudeBackground)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
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
                            text = "Previous Year Papers",
                            style = MaterialTheme.typography.headlineMedium,
                            color = ClaudeTextMain
                        )
                        Text(
                            text = "University Examination Vault",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ClaudeTextMuted
                        )
                    }
                }

                // 6 full-width cards stack mapping semesters cleanly
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    (1..6).forEach { semNum ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .border(BorderStroke(1.dp, ClaudeBorder), RoundedCornerShape(12.dp))
                                .background(ClaudeSurface, RoundedCornerShape(12.dp))
                                .clickable { /* Action to display target year PDF archive files later */ }
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Semester $semNum ",
                                    color = ClaudeTextMain,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "(end + mid)",
                                    color = ClaudeTextMuted,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Light // Less font weight for bracket content
                                )
                            }
                        }
                    }
                }
            }
        }

        else -> {
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
                        ClaudeGridCard(title = "Notes", modifier = Modifier.weight(1f)) {
                            currentSubView = "Notes"
                        }
                        ClaudeGridCard(title = "PYQ", modifier = Modifier.weight(1f)) {
                            currentSubView = "PYQ"
                        }
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