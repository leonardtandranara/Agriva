package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AgrivaLogo
import com.example.ui.theme.AgrivaGreen
import com.example.ui.theme.AgrivaDarkGreen
import com.example.ui.theme.AgrivaYellow
import com.example.ui.viewmodel.AgrivaViewModel
import com.example.ui.viewmodel.DashboardTab

@Composable
fun SummaryTab(
    viewModel: AgrivaViewModel,
    modifier: Modifier = Modifier
) {
    val balance by viewModel.walletBalance.collectAsState()
    val activeProducts by viewModel.activeMarketItems.collectAsState()
    val allWeather by viewModel.allWeather.collectAsState()
    val deletedProducts by viewModel.deletedMarketItems.collectAsState()
    val deletedTransactions by viewModel.deletedTransactions.collectAsState()

    val totalTrash = deletedProducts.size + deletedTransactions.size
    val activeProductCount = activeProducts.size
    // Count alerts (ROUGE or ORANGE)
    val climaticAlertCount = allWeather.count { it.riskLevel == "ROUGE" || it.riskLevel == "ORANGE" }

    val userEmail by viewModel.currentUserEmail.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Personal Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("welcome_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Tonga soa tompoko,",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = userEmail ?: "Mpiompy & Mpiambina",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(AgrivaYellow, shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = userRole ?: "USER",
                                color = Color.Black,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Agriva Mada Intelligence Hub",
                            fontSize = 11.sp,
                            color = AgrivaGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Mini animated spinning logo or static beautiful insignia
                AgrivaLogo(showText = false, size = 60f)
            }
        }

        // QUICK STATUS METRICS (GRID LAYOUT EFFECT TYPE)
        Text(
            text = "Tondro-tombana ankapobeny (Indicateurs clés) :",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // WALLET METRIC CARD (Clickable to switch tab)
            MetricBigCard(
                title = "Solde simulation",
                value = "${formatAriary(balance)} Ar",
                description = "Volanao ao anatin'ny wallet",
                icon = Icons.Default.AccountBalanceWallet,
                accentColor = AgrivaGreen,
                modifier = Modifier.weight(1f),
                isDarkStyle = true,
                onClick = { viewModel.selectTab(DashboardTab.WALLET) }
            )

            // COMMODITIES METRIC CARD
            MetricBigCard(
                title = "Vokatra mavitrika",
                value = "$activeProductCount vokatra",
                description = "Sora-bary voamarika amin'izao",
                icon = Icons.Default.Storefront,
                accentColor = AgrivaGreen,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.selectTab(DashboardTab.MARKET) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // WEATHER ALERT METRIC
            MetricBigCard(
                title = "Alerte Climatique",
                value = "$climaticAlertCount alertes",
                description = "Loza mitatao Orange / Rouge",
                icon = Icons.Default.Warning,
                accentColor = if (climaticAlertCount > 0) Color(0xFFC62828) else Color.Gray,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.selectTab(DashboardTab.WEATHER) }
            )

            // TRASH CORBEILLE METRIC
            MetricBigCard(
                title = "Dapoaka / Trash",
                value = "$totalTrash items",
                description = "Zavatra soft delete sisa",
                icon = Icons.Default.Delete,
                accentColor = if (totalTrash > 0) Color(0xFFEF6C00) else Color.Gray,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.selectTab(DashboardTab.TRASH) }
            )
        }

        // MADAGASCAR CLIMATIC MONITOR SUMMARY (Aesthetic weather status table)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Fanaraha-maso ny loza isam-paritra (Météo Status)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Divider()

                allWeather.forEach { weather ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectMapRegion(weather.region)
                                viewModel.selectTab(DashboardTab.MAP)
                            }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Regional compass spot
                            Icon(Icons.Default.Place, contentDescription = null, tint = AgrivaGreen, modifier = Modifier.size(16.dp))
                            Text(
                                text = weather.region,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "${weather.temperature}°C • Hum: ${weather.humidity.toInt()}%",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )

                            // Little circular badge for risk
                            val riskColor = getRiskColor(weather.riskLevel)
                            Box(
                                modifier = Modifier
                                    .background(riskColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                    .border(1.dp, riskColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = weather.riskLevel,
                                    color = riskColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                }
            }
        }

        // QUICK NOTCH/FOOTER FOR AGRIVA MADA PLATFORM INFO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(AgrivaGreen.copy(alpha = 0.1f), AgrivaDarkGreen.copy(alpha = 0.15f))
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Antsipirian'ny Agriva Platform",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgrivaDarkGreen
                    )
                    Text(
                        text = "Tsy mampiasa services payants na fampandrenesana SMS.",
                        fontSize = 9.sp,
                        color = Color.DarkGray
                    )
                }

                Icon(
                    imageVector = Icons.Default.CloudQueue,
                    contentDescription = null,
                    tint = AgrivaGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun MetricBigCard(
    title: String,
    value: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    isDarkStyle: Boolean = false,
    onClick: () -> Unit
) {
    val cardBg = if (isDarkStyle) AgrivaGreen else MaterialTheme.colorScheme.surface
    val cardBorder = if (isDarkStyle) null else BorderStroke(1.dp, Color(0xFFE2E8F0))
    val titleColor = if (isDarkStyle) Color.White.copy(alpha = 0.8f) else Color.Gray
    val valueColor = if (isDarkStyle) Color.White else MaterialTheme.colorScheme.onSurface
    val descColor = if (isDarkStyle) Color.White.copy(alpha = 0.7f) else Color.Gray

    Card(
        modifier = modifier
            .height(115.dp)
            .clickable { onClick() }
            .testTag("metric_${title.lowercase().replace(" ", "_")}"),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = cardBorder,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = titleColor,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .background(if (isDarkStyle) Color.White.copy(alpha = 0.15f) else accentColor.copy(alpha = 0.1f), shape = RoundedCornerShape(6.dp))
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isDarkStyle) AgrivaYellow else accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = valueColor
                )
                Text(
                    text = description,
                    fontSize = 9.sp,
                    color = descColor,
                    lineHeight = 11.sp
                )
            }
        }
    }
}
