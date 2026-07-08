package com.imshivlok.lubcanotes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.imshivlok.lubcanotes.ui.theme.*

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    // In-memory profile data holder
    var name by remember { mutableStateOf("Shivlok Sharma") }
    var course by remember { mutableStateOf("BCA") }
    var semester by remember { mutableStateOf("Semester 1") }
    var college by remember { mutableStateOf("Lucknow University") }

    // Navigation toggle state within profile branch
    var isEditing by remember { mutableStateOf(false) }

    if (!isEditing) {
        // --- VIEW MODE ---
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(ClaudeBackground)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Round Center Top Dummy Profile Icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(ClaudeSurface)
                    .border(BorderStroke(1.dp, ClaudeBorder), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_account_box),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(48.dp),
                    tint = ClaudeTextMuted
                )
            }

            // 2. Profile Meta Details
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = name, style = MaterialTheme.typography.headlineSmall, color = ClaudeTextMain)
                Text(text = "$course • $semester", style = MaterialTheme.typography.bodyMedium, color = ClaudeTextMuted)
                Text(text = college, style = MaterialTheme.typography.bodyMedium, color = ClaudeTextMuted)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Circular Rectangle Edit Profile Pill Button
            Button(
                onClick = { isEditing = true },
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ClaudeSurface),
                border = BorderStroke(1.dp, ClaudeBorder),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "✏️", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Edit Profile", color = ClaudeTextMain, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    } else {
        // --- EDIT MODE SCREEN ---
        var tempName by remember { mutableStateOf(name) }
        var tempCourse by remember { mutableStateOf(course) }
        var tempSemester by remember { mutableStateOf(semester) }
        var tempCollege by remember { mutableStateOf(college) }

        var courseDropdownExpanded by remember { mutableStateOf(false) }
        var semDropdownExpanded by remember { mutableStateOf(false) }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(ClaudeBackground)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(text = "Edit Profile Info", style = MaterialTheme.typography.titleLarge, color = ClaudeTextMain)

            // Circular editable profile avatar action area at the top center of edit page
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(ClaudeSurface)
                        .border(BorderStroke(1.dp, ClaudeBorder), CircleShape)
                        .clickable { /* Action item loop to open device local image storage picker goes here later */ },
                    contentAlignment = Alignment.Center
                ) {
                    // Base profile vector asset
                    Icon(
                        painter = painterResource(id = R.drawable.ic_account_box),
                        contentDescription = "Edit Profile Picture Container",
                        modifier = Modifier.size(48.dp),
                        tint = ClaudeTextMuted.copy(alpha = 0.4f)
                    )

                    // Semi-transparent overlay with indicator text trigger element
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📸",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }

            // Name Input Field
            OutlinedTextField(
                value = tempName,
                onValueChange = { tempName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ClaudeAccent)
            )

            // Course & Semester Dropdown Layout in a Single Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Course Select Dropdown
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = tempCourse,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Course") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(disabledBorderColor = ClaudeBorder, disabledLabelColor = ClaudeTextMuted, disabledTextColor = ClaudeTextMain)
                    )
                    DropdownMenu(
                        expanded = courseDropdownExpanded,
                        onDismissRequest = { courseDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(text = { Text("BCA") }, onClick = { tempCourse = "BCA"; courseDropdownExpanded = false })
                        DropdownMenuItem(text = { Text("BCA (NEP)") }, onClick = { tempCourse = "BCA (NEP)"; courseDropdownExpanded = false })
                    }
                    Box(modifier = Modifier.matchParentSize().clickable { courseDropdownExpanded = true })
                }

                // Semester Select Dropdown
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = tempSemester,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Semester") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(disabledBorderColor = ClaudeBorder, disabledLabelColor = ClaudeTextMuted, disabledTextColor = ClaudeTextMain)
                    )
                    DropdownMenu(
                        expanded = semDropdownExpanded,
                        onDismissRequest = { semDropdownExpanded = false }
                    ) {
                        (1..6).forEach { semNum ->
                            DropdownMenuItem(
                                text = { Text("Semester $semNum") },
                                onClick = { tempSemester = "Semester $semNum"; semDropdownExpanded = false }
                            )
                        }
                    }
                    Box(modifier = Modifier.matchParentSize().clickable { semDropdownExpanded = true })
                }
            }

            // College / University Text Box Input
            OutlinedTextField(
                value = tempCollege,
                onValueChange = { tempCollege = it },
                label = { Text("University / College") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ClaudeAccent)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Save and Cancel Actions Group Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { isEditing = false },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", color = ClaudeTextMuted)
                }
                Button(
                    onClick = {
                        name = tempName
                        course = tempCourse
                        semester = tempSemester
                        college = tempCollege
                        isEditing = false
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ClaudeAccent)
                ) {
                    Text("Save Changes", color = Color.White)
                }
            }
        }
    }
}