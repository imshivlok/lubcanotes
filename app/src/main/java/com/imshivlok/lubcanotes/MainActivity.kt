package com.imshivlok.lubcanotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.imshivlok.lubcanotes.ui.theme.LUBCANotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LUBCANotesTheme {
                LUBCANotesApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun LUBCANotesApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    // Dynamic Profile States for the end user (Starts as a clean slate)
    var profileName by rememberSaveable { mutableStateOf("") }
    var profileCourse by rememberSaveable { mutableStateOf("Select Course") }
    var profileSemester by rememberSaveable { mutableStateOf("Select Semester") }
    var profileCollege by rememberSaveable { mutableStateOf("") }

    // Dynamically calculate first name or fallback to generic "User" if field is unassigned
    val firstName = remember(profileName) {
        profileName.trim().split("\\s+".toRegex()).firstOrNull()?.takeIf { it.isNotEmpty() } ?: "User"
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            when (currentDestination) {
                AppDestinations.HOME -> {
                    HomeScreen(
                        userName = firstName,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppDestinations.PROFILE -> {
                    ProfileScreen(
                        name = profileName,
                        course = profileCourse,
                        semester = profileSemester,
                        college = profileCollege,
                        onProfileChanged = { updatedName, updatedCourse, updatedSemester, updatedCollege ->
                            profileName = updatedName
                            profileCourse = updatedCourse
                            profileSemester = updatedSemester
                            profileCollege = updatedCollege
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HOME("Home", R.drawable.ic_home),
    PROFILE("Profile", R.drawable.ic_account_box),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LUBCANotesTheme {
        Greeting("Android")
    }
}