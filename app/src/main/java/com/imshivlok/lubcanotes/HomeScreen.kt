package com.imshivlok.lubcanotes

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.imshivlok.lubcanotes.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun HomeScreen(
    userName: String,
    onProfileClick: () -> Unit, // ← Add this callback parameter
    modifier: Modifier = Modifier
) {
    var currentSubView by remember { mutableStateOf("") }
    var selectedSemesterLabel by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var scrapedNotices by remember { mutableStateOf<List<UniversityNotice>>(emptyList()) }
    var isNoticesLoading by remember { mutableStateOf(false) }

    val downloadedLinks = remember { mutableStateListOf<String>() }

    // Persistent Storage Configs
    val sharedPrefs = remember { context.getSharedPreferences("LUBCANotes_Prefs", Context.MODE_PRIVATE) }
    var persistentName by remember { mutableStateOf(userName) }
    var savedImageUriString by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        // Fetch full name from pref, fallback to the passed down userName variable
        val fullName = sharedPrefs.getString("profile_name", userName) ?: userName

        // Extract only the first name cleanly by splitting on spaces
        persistentName = fullName.trim().split("\\s+".toRegex()).firstOrNull()?.takeIf { it.isNotEmpty() } ?: "User"

        savedImageUriString = sharedPrefs.getString("profile_image_uri", "") ?: ""

        val verifiedLinks = NoticeRepository.loadAndVerifyDownloadedLinks(context)
        downloadedLinks.clear()
        downloadedLinks.addAll(verifiedLinks)

        val localCache = NoticeRepository.loadNoticesFromCache(context)
        if (localCache.isNotEmpty()) {
            scrapedNotices = localCache
        } else {
            isNoticesLoading = true
        }

        val freshNotices = NoticeRepository.fetchLatestNotices(context)
        if (freshNotices.isNotEmpty()) {
            scrapedNotices = freshNotices
        }
        isNoticesLoading = false
    }

    when (currentSubView) {
        "Notes" -> {
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

                // Map out the exact subject lists per selected semester
                val subjectsList = when (selectedSemesterLabel) {
                    "Semester 1" -> listOf(
                        "NBCA-101 Fundamentals of Computer and its Applications",
                        "NBCA-102 Programming in C",
                        "NBCA-103 Basics of Information System",
                        "NBCA-104 Mathematics",
                        "NBCA-105 Soft Skills and Personality Development"
                    )
                    "Semester 2" -> listOf(
                        "NBCA-201 Data Structure",
                        "NBCA-202 Database Management System",
                        "NBCA-203 Operating System",
                        "NBCA-204 Discrete Mathematical Structures",
                        "NBCA-205 Digital Electronics and Computer Organization"
                    )
                    "Semester 3" -> listOf(
                        "NBCA-301 Object Oriented Programming Using Java",
                        "NBCA-302 Software Engineering",
                        "NBCA-303 Computer Architecture",
                        "NBCA-304 Python Programming",
                        "NBCA-305 Accounting and Financial Management"
                    )
                    "Semester 4" -> listOf(
                        "NBCA-401 Advance Java Technology",
                        "NBCA-402 Design and Analysis of Algorithm",
                        "NBCA-403 Web Design Concepts",
                        "NBCA-404 Computer Graphics",
                        "NBCA-405 Managerial Economics"
                    )
                    "Semester 5" -> listOf(
                        "NBCA-501 Computer Network",
                        "NBCA-502 Artificial Intelligence",
                        "NBCA-503 Cyber Law",
                        "NBCA-504 Numerical and Reasoning Ability Development",
                        "NBCA-505X Departmental Elective-I"
                    )
                    "Semester 6" -> listOf(
                        "NBCA-601 Machine Learning",
                        "NBCA-602 Multimedia System",
                        "NBCA-603 Software Project Management",
                        "NBCA-604X Departmental Elective-II"
                    )
                    else -> listOf("Subject 1", "Subject 2", "Subject 3", "Subject 4", "Subject 5")
                }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    subjectsList.forEach { subjectName ->
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
                            Text(text = subjectName, color = ClaudeTextMain, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }

        "PYQ" -> {
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

        "DownloadedView" -> {
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
                        Text(text = "Saved Offline Records", style = MaterialTheme.typography.headlineMedium, color = ClaudeTextMain)
                        Text(text = "Verified internally inside LUBCANotes", style = MaterialTheme.typography.bodyMedium, color = ClaudeTextMuted)
                    }
                }

                if (downloadedLinks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No offline copies saved yet.", color = ClaudeTextMuted)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        val itemsToShow = scrapedNotices.filter { downloadedLinks.contains(it.link) }

                        if (itemsToShow.isEmpty()) {
                            downloadedLinks.forEach { linkUrl ->
                                val fileName = NoticeRepository.getFileNameFromUrl(linkUrl)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .border(BorderStroke(1.dp, ClaudeBorder), RoundedCornerShape(12.dp))
                                        .background(ClaudeSurface, RoundedCornerShape(12.dp))
                                        .clickable { launchPdfIntent(context, linkUrl) }
                                        .padding(20.dp)
                                ) {
                                    Text(text = fileName, color = ClaudeTextMain, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        } else {
                            itemsToShow.forEach { notice ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .border(BorderStroke(1.dp, ClaudeBorder), RoundedCornerShape(12.dp))
                                        .background(ClaudeSurface, RoundedCornerShape(12.dp))
                                        .clickable { launchPdfIntent(context, notice.link) }
                                        .padding(20.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(text = notice.title, color = ClaudeTextMain, style = MaterialTheme.typography.bodyMedium)
                                        Text(text = "Saved Local File", color = ClaudeAccent, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                    }
                                }
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
                // 📸 Aligned Header Row: Placing your circular profile icon beautifully to the right side of Welcome text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "Welcome, $persistentName", style = MaterialTheme.typography.headlineMedium, color = ClaudeTextMain)
                        Text(text = "Your workspace is up to date", style = MaterialTheme.typography.bodyMedium, color = ClaudeTextMuted)
                    }

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .border(BorderStroke(1.dp, ClaudeBorder), CircleShape)
                            .background(ClaudeSurface, CircleShape)
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (savedImageUriString.isNotEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(model = Uri.parse(savedImageUriString)),
                                contentDescription = "Dashboard Profile Photo Preview",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                                contentDescription = "Fallback Vector Profile Icon",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .border(BorderStroke(1.dp, ClaudeBorder), RoundedCornerShape(12.dp))
                        .background(ClaudeSurface, RoundedCornerShape(12.dp))
                        .clickable { currentSubView = "DownloadedView" }
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
                            Text(text = "View saved offline records (${downloadedLinks.size})", color = ClaudeTextMuted, style = MaterialTheme.typography.bodySmall)
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(if(downloadedLinks.isNotEmpty()) ClaudeAccent else ClaudeSurface, RoundedCornerShape(50.dp))
                                .border(BorderStroke(1.dp, ClaudeBorder), RoundedCornerShape(50.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (downloadedLinks.isNotEmpty()) {
                                Text(text = "${downloadedLinks.size}", color = ClaudeSurface, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
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
                            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 44.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Notices & Circulars", color = ClaudeAccent, style = MaterialTheme.typography.titleMedium)

                                Text(
                                    text = if (isNoticesLoading) "⏳ Syncing" else "⟳ Refresh",
                                    color = if (isNoticesLoading) ClaudeTextMuted else ClaudeAccent,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clickable(enabled = !isNoticesLoading) {
                                            coroutineScope.launch {
                                                isNoticesLoading = true
                                                val fresh = NoticeRepository.fetchLatestNotices(context)
                                                if (fresh.isNotEmpty()) {
                                                    scrapedNotices = fresh
                                                }
                                                isNoticesLoading = false
                                            }
                                        }
                                        .padding(4.dp)
                                )
                            }

                            if (isNoticesLoading && scrapedNotices.isEmpty()) {
                                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = ClaudeAccent)
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    scrapedNotices.forEach { notice ->
                                        val isDownloaded = downloadedLinks.contains(notice.link)

                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "• ${notice.title}",
                                                color = ClaudeTextMain,
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.padding(start = 12.dp)
                                            ) {
                                                if (!isDownloaded) {
                                                    val buttonLabel = if (notice.size.isEmpty()) {
                                                        "Download PDF"
                                                    } else {
                                                        "Download PDF [${notice.size}]"
                                                    }

                                                    Text(
                                                        text = buttonLabel,
                                                        color = ClaudeAccent,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.Bold,
                                                        textDecoration = TextDecoration.Underline,
                                                        modifier = Modifier.clickable {
                                                            if (notice.link.isNotEmpty()) {
                                                                coroutineScope.launch {
                                                                    val file = NoticeRepository.downloadPdfToFile(context, notice.link)
                                                                    if (file != null) {
                                                                        downloadedLinks.add(notice.link)
                                                                        NoticeRepository.saveDownloadedLinks(context, downloadedLinks)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    )
                                                } else {
                                                    Text(
                                                        text = "Downloaded",
                                                        color = ClaudeTextMuted,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontStyle = FontStyle.Italic
                                                    )

                                                    Text(
                                                        text = "Open",
                                                        color = ClaudeAccent,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.Bold,
                                                        textDecoration = TextDecoration.Underline,
                                                        modifier = Modifier.clickable {
                                                            launchPdfIntent(context, notice.link)
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .offset(y = 18.dp)
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

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

private fun launchPdfIntent(context: android.content.Context, linkUrl: String) {
    try {
        val targetFile = File(NoticeRepository.getTargetFolder(context), NoticeRepository.getFileNameFromUrl(linkUrl))
        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            targetFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, "application/pdf")
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(Intent.createChooser(intent, "Open PDF with"))
    } catch (e: Exception) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl))
        context.startActivity(webIntent)
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