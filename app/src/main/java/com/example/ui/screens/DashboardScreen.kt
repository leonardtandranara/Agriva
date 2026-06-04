package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AgrivaLogo
import com.example.ui.theme.AgrivaGreen
import com.example.ui.theme.AgrivaDarkGreen
import com.example.ui.theme.AgrivaYellow
import com.example.ui.viewmodel.AgrivaViewModel
import com.example.ui.viewmodel.DashboardTab

@Composable
fun DashboardScreen(
    viewModel: AgrivaViewModel,
    modifier: Modifier = Modifier
) {
    val activeTab by viewModel.currentTab.collectAsState()
    val userEmail by viewModel.currentUserEmail.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    // Collapse Sidebar state
    var isSidebarCollapsed by remember { mutableStateOf(false) }
    val sidebarWidth = if (isSidebarCollapsed) 68.dp else 220.dp

    // Animation values for rotation toggler Chevron
    val rotationState by animateFloatAsState(targetValue = if (isSidebarCollapsed) 180f else 0f)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            // FIXED HEADER (Agriva brand and session status)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(AgrivaDarkGreen)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AgrivaLogo(showText = true, size = 44f, textSize = 16f, textColor = Color.White)
                    }

                    // User status pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // User Identity badge (shrunk path visual)
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.25f), shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Wifi, contentDescription = "Active synch", tint = Color.White, modifier = Modifier.size(10.dp))
                                Text(
                                    text = userEmail?.split("@")?.firstOrNull() ?: "Fermier",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Logout Icon button
                        IconButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("header_logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = AgrivaYellow
                            )
                        }
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        // Layout comprising Sidebar and content arena
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // RESPONSIVE COLLAPSIBLE SIDEBAR
            Column(
                modifier = Modifier
                    .width(sidebarWidth)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    .animateContentSize()
                    .testTag("collapsible_sidebar"),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Tab selector stack with header branding logo
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Sidebar Header logo display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AgrivaLogo(
                            showText = !isSidebarCollapsed,
                            size = 36f,
                            textSize = 13f,
                            textColor = AgrivaDarkGreen
                        )
                    }

                    Divider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, start = 8.dp, end = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Item tabs definitions
                        val tabs = listOf(
                            SidebarTabItem(DashboardTab.DASHBOARD_SUMMARY, Icons.Default.Dashboard, "Tondro (Index)"),
                            SidebarTabItem(DashboardTab.WEATHER, Icons.Default.Cloud, "Météo (Alerte)"),
                            SidebarTabItem(DashboardTab.MARKET, Icons.Default.TrendingUp, "Marché (Prix)"),
                            SidebarTabItem(DashboardTab.WALLET, Icons.Default.AccountBalanceWallet, "Wallet (Vola)"),
                            SidebarTabItem(DashboardTab.MAP, Icons.Default.Map, "Faritra (Map)"),
                            SidebarTabItem(DashboardTab.TRASH, Icons.Default.Delete, "Corbeille (Trash)")
                        )

                        tabs.forEach { tabItem ->
                            val isSelected = activeTab == tabItem.tab
                            val buttonColor = if (isSelected) AgrivaGreen else Color.Transparent
                            val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(buttonColor)
                                    .clickable { viewModel.selectTab(tabItem.tab) }
                                    .padding(horizontal = 12.dp)
                                    .testTag("sidebar_tab_${tabItem.tab.name.lowercase()}"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = tabItem.icon,
                                    contentDescription = tabItem.label,
                                    tint = contentColor,
                                    modifier = Modifier.size(20.dp)
                                )

                                if (!isSidebarCollapsed) {
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = tabItem.label,
                                        color = contentColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                // Collapser Chevron Switch Button situated at sidebar bottom
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Divider(modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp))

                    IconButton(
                        onClick = { isSidebarCollapsed = !isSidebarCollapsed },
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .testTag("sidebar_collapse_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Collapse triggers",
                            tint = AgrivaGreen,
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(rotationState)
                        )
                    }
                }
            }

            // CENTRAL DETAILED CONTENT CONTAINER (Tab Switcher with anim entry)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background)
                    .testTag("dashboard_content_canvas")
            ) {
                Crossfade(
                    targetState = activeTab,
                    label = "tabCrossfade"
                ) { tab ->
                    when (tab) {
                        DashboardTab.DASHBOARD_SUMMARY -> SummaryTab(viewModel = viewModel)
                        DashboardTab.WEATHER -> WeatherTab(viewModel = viewModel)
                        DashboardTab.MARKET -> MarketTab(viewModel = viewModel)
                        DashboardTab.WALLET -> WalletTab(viewModel = viewModel)
                        DashboardTab.MAP -> MapTab(viewModel = viewModel)
                        DashboardTab.TRASH -> TrashTab(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

data class SidebarTabItem(
    val tab: DashboardTab,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)
