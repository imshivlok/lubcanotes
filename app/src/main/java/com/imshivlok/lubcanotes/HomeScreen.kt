package com.imshivlok.lubcanotes

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.imshivlok.lubcanotes.ui.theme.*

@Composable
fun HomeScreen(
    userName: String,
    modifier: Modifier = Modifier
) {
    var currentSubView by remember { mutableStateOf("") }
    var selectedSemesterLabel by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Live state containers for web scraping data stream
    var scrapedNotices by remember { mutableStateOf<List<UniversityNotice>>(emptyList()) }
    var isNoticesLoading by remember { mutableStateOf(true) }

    // Fetch live announcements on launch hook
    LaunchedEffect(Unit) {
        scrapedNotices = NoticeRepository.fetchLatestNotices()
        isNoticesLoading = false
    }

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
                        Text(text = "Select Semester", style = MaterialTheme.typography.headlineMedium, color = ClaudeTextMain)
                        Text(text = "BCA Academic Syllabus", style = MaterialTheme.typography.bodyMedium, color = ClaudeTextMuted)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    (1..5 step 2).forEach { semNum ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ClaudeGridCard(title = "Semester $semNum", modifier = Modifier.weight(1f)) {
                                selectedSemesterLabel = "Semester $semNum"
                                currentSubView = "Subjects"
                            }
                            ClaudeGridCard(title = "Semester ${semNum + 1}", modifier = Modifier.weight(1f)) {
                                selectedSemesterLabel = "Semester ${semNum + 1}"
                                currentSubView = "Subjects"
                            }
                        }
                    }
                }
            }
        }

        "Subjects" -> {
            // --- SUBJECTS LIST VIEW FOR NOTES (6 SUBJECT CARDS, 1 ROW WIDE EACH) ---
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
                        Text(text = selectedSemesterLabel, style = MaterialTheme.typography.headlineMedium, color = ClaudeTextMain)
                        Text(text = "Core Course Modules", style = MaterialTheme.typography.bodyMedium, color = ClaudeTextMuted)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    (1..6).forEach { subjectNum ->
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
                            Text(text = "Subject $subjectNum", color = ClaudeTextMain, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }

        "PYQ" -> {
            // --- PYQ SCREEN (6 CARDS, 1 ROW WIDE EACH) ---
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
                        Text(text = "Previous Year Papers", style = MaterialTheme.typography.headlineMedium, color = ClaudeTextMain)
                        Text(text = "University Examination Vault", style = MaterialTheme.typography.bodyMedium, color = ClaudeTextMuted)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    (1..6).forEach { semNum ->
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Semester $semNum ", color = ClaudeTextMain, style = MaterialTheme.typography.titleMedium)
                                Text(text = "(end + mid)", color = ClaudeTextMuted, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Light)
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
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Welcome, $userName", style = MaterialTheme.typography.headlineMedium, color = ClaudeTextMain)
                    Text(text = "Your workspace is up to date", style = MaterialTheme.typography.bodyMedium, color = ClaudeTextMuted)
                }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ClaudeGridCard(title = "Notes", modifier = Modifier.weight(1f)) { currentSubView = "Notes" }
                        ClaudeGridCard(title = "PYQ", modifier = Modifier.weight(1f)) { currentSubView = "PYQ" }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ClaudeGridCard(title = "Calendar", modifier = Modifier.weight(1f)) {}
                        ClaudeGridCard(title = "Time Table", modifier = Modifier.weight(1f)) {}
                    }
                }

                // Downloaded Content Card
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

                // --- LIVE NOTICES & CIRCULARS CONTAINER ---
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .border(BorderStroke(1.dp, ClaudeBorder), RoundedCornerShape(12.dp))
                            .background(ClaudeSurface, RoundedCornerShape(12.dp))
                            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 36.dp) // Added padding space at bottom for half overlap bounds
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(text = "Notices & Circulars", color = ClaudeAccent, style = MaterialTheme.typography.titleMedium)

                            if (isNoticesLoading) {
                                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = ClaudeAccent)
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                    scrapedNotices.forEach { notice ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(enabled = notice.link.isNotEmpty()) {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(notice.link))
                                                    context.startActivity(intent)
                                                }
                                        ) {
                                            Text(
                                                text = "• ${notice.title}",
                                                color = ClaudeTextMain,
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (notice.date.isNotEmpty()) {
                                                Text(
                                                    text = "  ${notice.date}",
                                                    color = ClaudeTextMuted,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 💊 "View More" Pill Button Container (Centered, half inside, half outside)
                    Box(
                        modifier = Modifier
                            .offset(y = 18.dp) // Offset exactly half of button's height to split position border bounds
                            .height(36.dp)
                            .background(ClaudeSurface, RoundedCornerShape(50.dp))
                            .border(BorderStroke(1.dp, ClaudeBorder), RoundedCornerShape(50.dp))
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.lkouniv.ac.in/en/news?Newslistslug=en-notices&cd=MwAzADcA"))
                                context.startActivity(intent)
                            }
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "View More",
                            color = ClaudeTextMain,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Extra padding bottom space layout placeholder underneath overlapping button boundaries
                Spacer(modifier = Modifier.height(16.dp))
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
        Text(text = title, color = ClaudeTextMain, style = MaterialTheme.typography.titleMedium)
    }
}