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
import com.example.levelup.ui.theme.carrito.CarritoScreen
import com.example.levelup.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer() {
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
                Text(
                    "Menú Principal",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )

                NavigationDrawerItem(
                    label = { Text("Dashboard") },
                    selected = selectedScreen == "home",
                    onClick = { selectedScreen = "home" },
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) }
                )

                NavigationDrawerItem(
                    label = { Text("Ventas") },
                    selected = selectedScreen == "carts",
                    onClick = { selectedScreen = "carts" },
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
                    selected = selectedScreen == "userProfile",
                    onClick = { selectedScreen = "userProfile" },
                    icon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                // User info and logout
                userEmail?.let { email ->
                    Text(
                        text = "Conectado como:",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión") },
                    selected = false,
                    onClick = { authViewModel.logout() },
                    icon = { Icon(Icons.Filled.Logout, contentDescription = null) }
                )
            }
        }
    ) {
        // Contenido central según la pantalla seleccionada
        when (selectedScreen) {
            "home" -> HomeScreen(onMenuClick = { scope.launch { drawerState.open() } })
            "profile" -> ProfileScreen(onMenuClick = { scope.launch { drawerState.open() } })
            "carts" -> CarritoScreen (onMenuClick = {scope.launch { drawerState.open() }})
            "userProfile" -> ProfileScreen(onMenuClick = { scope.launch { drawerState.open() } })
            "settings" -> SettingsScreenAuth(
                authViewModel = authViewModel,
                userEmail = userEmail,
                onMenuClick = { scope.launch { drawerState.open() } }
            )
            "category" -> CategoryScreen(onMenuClick = { scope.launch { drawerState.open() } })
            "users" -> UserScreen(onMenuClick = {scope.launch { drawerState.open() }})
            "platform" -> PlatformScreen( onMenuClick = {scope.launch { drawerState.open() }})
            "products" -> ProductScreen(onMenuClick = {scope.launch { drawerState.open() }})
        }
    }
}

// Pantalla de Reportes/Configuración
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenAuth(
    authViewModel: AuthViewModel,
    userEmail: String?,
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            MainTopBar("Reportes", onMenuClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Información de Usuario",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Email: ${userEmail ?: "No disponible"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Configuración del Backend",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "URL: http://levelup-back-env.eba-277ppcgy.us-east-1.elasticbeanstalk.com/",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Button(
                onClick = { authViewModel.logout() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }
}
