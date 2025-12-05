package com.example.levelup.ui.theme.appDrawer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.levelup.ui.theme.HomeScreen
import com.example.levelup.ui.theme.category.CategoryScreen
import com.example.levelup.ui.theme.profile.ProfileScreen
import com.example.levelup.ui.theme.users.UserScreen
import com.example.levelup.ui.theme.mainTopBar.MainTopBar
import com.example.levelup.ui.theme.platform.PlatformScreen
import com.example.levelup.ui.theme.product.ProductScreen
import com.example.levelup.ui.theme.auth.LoginScreen
import com.example.levelup.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerWithAuth() {
    val context = LocalContext.current
    val authViewModel = remember { AuthViewModel(context) }
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val userEmail by authViewModel.userEmail.collectAsState()
    
    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = {
                // Login successful, state will update automatically
            }
        )
    } else {
        AuthenticatedDrawer(authViewModel, userEmail)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticatedDrawer(
    authViewModel: AuthViewModel,
    userEmail: String?
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedScreen by remember { mutableStateOf("home") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                
                // User info
                userEmail?.let { email ->
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Bienvenido",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Divider()
                Spacer(Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("Dashboard") },
                    selected = selectedScreen == "home",
                    onClick = { selectedScreen = "home" },
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) }
                )

                NavigationDrawerItem(
                    label = { Text("Ordenes") },
                    selected = selectedScreen == "orders",
                    onClick = { selectedScreen = "orders" },
                    icon = { Icon(Icons.Filled.ShoppingBag, contentDescription = null) }
                )

                NavigationDrawerItem(
                    label = { Text("Productos") },
                    selected = selectedScreen == "products",
                    onClick = { selectedScreen = "products" },
                    icon = { Icon(Icons.Filled.VideogameAsset, contentDescription = null) }
                )

                NavigationDrawerItem(
                    label = { Text("Categorias") },
                    selected = selectedScreen == "category",
                    onClick = { selectedScreen = "category" },
                    icon = { Icon(Icons.Filled.Category, contentDescription = null) }
                )

                NavigationDrawerItem(
                    label = { Text("Usuarios") },
                    selected = selectedScreen == "users",
                    onClick = { selectedScreen = "users" },
                    icon = { Icon(Icons.Filled.Person, contentDescription = null) }
                )

                NavigationDrawerItem(
                    label = { Text("Plataformas") },
                    selected = selectedScreen == "platform",
                    onClick = { selectedScreen = "platform" },
                    icon = { Icon(Icons.Filled.SportsEsports, contentDescription = null) }
                )

                NavigationDrawerItem(
                    label = { Text("Reportes") },
                    selected = selectedScreen == "settings",
                    onClick = { selectedScreen = "settings" },
                    icon = { Icon(Icons.Filled.Report, contentDescription = null) }
                )

                NavigationDrawerItem(
                    label = { Text("Perfil") },
                    selected = selectedScreen == "profile",
                    onClick = { selectedScreen = "profile" },
                    icon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) }
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Divider()
                
                // Logout button
                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión") },
                    selected = false,
                    onClick = { 
                        authViewModel.logout()
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Filled.Logout, contentDescription = null) },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedTextColor = MaterialTheme.colorScheme.error,
                        unselectedIconColor = MaterialTheme.colorScheme.error
                    )
                )
                
                Spacer(Modifier.height(16.dp))
            }
        }
    ) {
        // Contenido central según la pantalla seleccionada
        when (selectedScreen) {
            "home" -> HomeScreen(onMenuClick = { scope.launch { drawerState.open() } })
            "orders" -> ProfileScreen(onMenuClick = { scope.launch { drawerState.open() } })
            "settings" -> SettingsScreen(onMenuClick = { scope.launch { drawerState.open() } })
            "category" -> CategoryScreen(onMenuClick = { scope.launch { drawerState.open() } })
            "users" -> UserScreen(onMenuClick = {scope.launch { drawerState.open() }})
            "platform" -> PlatformScreen( onMenuClick = {scope.launch { drawerState.open() }})
            "products" -> ProductScreen(onMenuClick = {scope.launch { drawerState.open() }})
            "profile" -> ProfileScreen(onMenuClick = { scope.launch { drawerState.open() } })
        }
    }
}

// Ejemplo de pantalla Configuración
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            MainTopBar("Configuracion", onMenuClick)
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Pantalla de configuración", modifier = Modifier.padding(16.dp))
        }
    }
}
