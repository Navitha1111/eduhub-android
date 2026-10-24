package com.eduhub.android.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduhub.android.ui.navigation.Screen
import com.eduhub.android.ui.theme.EduCyan400
import com.eduhub.android.ui.theme.EduSlate400
import com.eduhub.android.ui.theme.EduSlate900

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun CommonBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem(Screen.Home.route, "Home", Icons.Default.Home),
        BottomNavItem(Screen.Courses.route, "Courses", Icons.Default.MenuBook),
        BottomNavItem(Screen.Playground.route, "Playground", Icons.Default.Code),
        BottomNavItem(Screen.CheatSheet.route, "CheatSheet", Icons.Default.LibraryBooks),
        BottomNavItem(Screen.Profile.route, "Profile", Icons.Default.Person)
    )

    NavigationBar(
        containerColor = EduSlate900,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EduCyan400,
                    selectedTextColor = EduCyan400,
                    unselectedIconColor = EduSlate400,
                    unselectedTextColor = EduSlate400,
                    indicatorColor = EduSlate900
                )
            )
        }
    }
}
