package com.example.taskflow_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
fun ProfileScreen(
    username: String,
    email: String,
    totalTasks: Int,
    completedTasks: Int,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    rememberMe: Boolean,
    onRememberMeChange: (Boolean) -> Unit,
    themeColorName: String,
    onThemeColorChange: (String) -> Unit,
    profileImageUri: String? = null,
    onProfileImageChange: (String) -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditProfile by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onProfileImageChange(it.toString()) }
    }
    
    val themeColorMap = mapOf(
        "Azul" to Color(0xFF304471),
        "Verde" to Color(0xFF2E7D32),
        "Rojo" to Color(0xFFC62828),
        "Morado" to Color(0xFF6A1B9A),
        "Naranja" to Color(0xFFEF6C00),
        "Cian" to Color(0xFF00838F)
    )
    
    val selectedThemeColor = themeColorMap[themeColorName] ?: Color(0xFF304471)

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas cerrar la sesión actual?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    }
                ) {
                    Text("Cerrar Sesión", color = Color(0xFFEF5350), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = Color.White
        )
    }

    if (showEditProfile) {
        AlertDialog(
            onDismissRequest = { showEditProfile = false },
            title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Personaliza tu experiencia", fontSize = 14.sp, color = Color.Gray)
                    
                    // Selección de Color
                    Column {
                        Text("Color del Tema", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            themeColorMap.forEach { (name, color) ->
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .clickable { onThemeColorChange(name) }
                                        .border(
                                            width = if (themeColorName == name) 3.dp else 0.dp,
                                            color = Color.Black.copy(alpha = 0.3f),
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }

                }
            },
            confirmButton = {
                Button(
                    onClick = { showEditProfile = false },
                    colors = ButtonDefaults.buttonColors(containerColor = selectedThemeColor)
                ) {
                    Text("Guardar Cambios")
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Rectangular (Sin redondeo abajo para que "rellene")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = selectedThemeColor)
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Avatar con estilo premium
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (profileImageUri != null) {
                                AsyncImage(
                                    model = profileImageUri,
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            } else {
                                Text(
                                    username.firstOrNull()?.uppercase() ?: "U",
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Black,
                                    color = selectedThemeColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        username,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Text(
                        email,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Sección de Estadísticas
                Text(
                    "Resumen de Actividad",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = "Total",
                        value = totalTasks.toString(),
                        containerColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Completadas",
                        value = completedTasks.toString(),
                        containerColor = Color(0xFFE8F5E9),
                        contentColor = Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Pendientes",
                        value = (totalTasks - completedTasks).toString(),
                        containerColor = Color(0xFFFFF3E0),
                        contentColor = Color(0xFFE65100),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Ajustes
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        ProfileField(label = "Rol de Usuario", value = "USER")

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = Color(0xFFF0F0F0)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Mantener sesión",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A)
                                )
                                Text(
                                    "Recordar credenciales",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = rememberMe,
                                onCheckedChange = onRememberMeChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = selectedThemeColor
                                )
                            )
                        }
                    }
                }

                // Acciones
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { showEditProfile = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = selectedThemeColor)
                    ) {
                        Text("Editar Perfil", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF5350)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF5350))
                    ) {
                        Text("Cerrar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Footer con derechos reservados
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    "© 2026 Alejandro Mella. Todos los derechos reservados.",
                    fontSize = 12.sp,
                    color = Color.Gray.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    containerColor: Color,
    modifier: Modifier = Modifier,
    contentColor: Color = Color(0xFF1A1A1A)
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = contentColor
            )
            Text(
                label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Column {
        Text(
            label,
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            value,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
