package com.example.taskflow_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToTasks: () -> Unit,
    isAuthenticated: Boolean
) {
    LaunchedEffect(Unit) {
        delay(2000)
        if (isAuthenticated) {
            onNavigateToTasks()
        } else {
            onNavigateToLogin()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo container
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    Color.White,
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.size(80.dp)
            ) {
                // Three horizontal lines representing tasks
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(
                                width = (80 - index * 20).dp,
                                height = 8.dp
                            )
                            .background(
                                color = when (index) {
                                    0 -> MaterialTheme.colorScheme.primary
                                    1 -> Color(0xFF2C2C2C)
                                    else -> Color(0xFF4CAF50)
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    if (index < 2) {
                        Box(modifier = Modifier.size(8.dp))
                    }
                }
            }
        }

        Box(modifier = Modifier.size(24.dp))

        Text(
            "TaskDay",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Box(modifier = Modifier.size(8.dp))

        Text(
            "Organiza tu día",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

