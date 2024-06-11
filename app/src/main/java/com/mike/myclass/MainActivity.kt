package com.mike.myclass

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mike.myclass.CommonComponents as CC

object Details {
    var email: MutableState<String> = mutableStateOf("")
    var name: MutableState<String> = mutableStateOf("")
    var showdialog: MutableState<Boolean> = mutableStateOf(true)
}

class MainActivity : ComponentActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        setContent {
            NavigationMap()
        }
        createNotificationChannel(this)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Permission already granted
                sharedPreferences.edit().putBoolean("NotificationPermissionGranted", true).apply()
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
                sharedPreferences.edit().putBoolean("NotificationPermissionGranted", true).apply()
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NavigationMap() {
        val context = LocalContext.current

        if (Details.showdialog.value) {
            BasicAlertDialog(
                onDismissRequest = { Details.showdialog.value = false },
                modifier = Modifier.background(
                    Color.Transparent, // Remove background here to avoid double backgrounds
                    RoundedCornerShape(10.dp)
                )
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            GlobalColors.secondaryColor, RoundedCornerShape(10.dp)
                        )
                        .padding(24.dp), // Add padding for better visual spacing
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Enable Notifications",
                        style = CC.titleTextStyle.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ), // Make title bolder
                        modifier = Modifier.padding(bottom = 8.dp) // Add spacing below title
                    )
                    Text(
                        "Please enable notifications to enable realtime updates.",
                        style = CC.descriptionTextStyle,
                        modifier = Modifier.padding(bottom = 16.dp) // Add spacing below description
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                // Call the requestNotificationPermission function from the context
                                (context as MainActivity).requestNotificationPermission()
                                Details.showdialog.value = false
                            }, modifier = Modifier.weight(1f), // Make buttons take equal width
                            colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.primaryColor) // Customize button colors
                        ) {
                            Text("Enable", color = Color.White) // Set text color for contrast
                        }
                        Spacer(modifier = Modifier.width(16.dp)) // Add space between buttons
                        Button(
                            onClick = { Details.showdialog.value = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray) // Customize button colors
                        ) {
                            Text("Cancel", color = Color.Black) // Set text color for contrast
                        }
                    }
                }
            }
        }

        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "colors") {
            composable("login") {
                LoginScreen(navController, context)
            }
            composable("announcements") {
                AnnouncementsScreen(navController, context)
            }
            composable("passwordreset") {
                PasswordReset(navController, context)
            }
            composable("dashboard") {
                Dashboard(navController, context)
            }
            composable("moredetails") {
                MoreDetails(context, navController)
            }
            composable("profile") {
                ProfileScreen(navController)
            }
            composable("manageusers") {
                ManageUsers(navController)
            }
            composable("assignments") {
                AssignmentScreen(navController,context)
            }
            composable("timetable") {
                TimetableScreen(navController,context)
            }
            composable("colors") {
                ColorSettings(navController,context)
            }

        }
    }

    override fun onStart() {
        super.onStart()
        val permissionGranted = sharedPreferences.getBoolean("NotificationPermissionGranted", false)
        if (permissionGranted) {
            // Notification permission already granted, do not show the dialog
            Details.showdialog.value = false
        }
    }
}
