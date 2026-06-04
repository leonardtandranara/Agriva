package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketItem
import com.example.data.model.WalletTransaction
import com.example.ui.theme.AgrivaGreen
import com.example.ui.theme.AgrivaDarkGreen
import com.example.ui.theme.AgrivaYellow
import com.example.ui.viewmodel.AgrivaViewModel
import kotlin.math.max

@Composable
fun TrashTab(
    viewModel: AgrivaViewModel,
    modifier: Modifier = Modifier
) {
    val deletedProducts by viewModel.deletedMarketItems.collectAsState()
    val deletedTransactions by viewModel.deletedTransactions.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    val isAdmin = userRole == "ADMIN"
    val totalTrashCount = deletedProducts.size + deletedTransactions.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("trash_tab_scroller"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Trash Icon",
                            tint = Color(0xFFC62828),
                            modifier = Modifier.size(32.dp)
                        )
                        Column {
                            Text(
                                text = "Dapoaka / Corbeille (Poubelle d'Agra)",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Soft delete repository • Auto-famafana 30 andro (Auto-clean 30j)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // CONTROL ACCELERATION PANEL (Admin simulation tool)
        if (isAdmin) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("admin_trash_controls"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = AgrivaYellow)
                            Text("Simulateur d'âge & Garbage Collector (GC)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = "Ny didy dia milaza fa raha dila 30 andro ny dapoaka dia vafana tanteraka. Ampiasao ireto bokotra ambany ireto mba hanaovana andrana haingana :",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.simulateAgingOlderThan30Days() },
                                colors = ButtonDefaults.buttonColors(containerColor = AgrivaYellow, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.1f).height(38.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Simuler +35 Andro", fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }

                            Button(
                                onClick = { viewModel.checkAndSweepOldTrash() },
                                colors = ButtonDefaults.buttonColors(containerColor = AgrivaGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(38.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Fafao dila 30j (GC)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.forceImmediateTrashClean() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.1f).height(38.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Madio tanteraka instantly", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Empty State indicator
        if (totalTrashCount == 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Inbox, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                            Text("Foana ny harona. Tsy misy zavatra voatahiry voafafa.", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        // DELETED COMMODITIES CATEGORY
        if (deletedProducts.isNotEmpty()) {
            item {
                Text(
                    text = "Vokatra voafafa (Marché Items Soft Deleted) :",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            items(deletedProducts, key = { "prod_${it.id}" }) { item ->
                DeletedItemRow(
                    title = item.name,
                    subtext = "Price: ${formatAriary(item.price)} Ar • Faritra: ${item.region}",
                    deletedAt = item.deletedAt ?: System.currentTimeMillis(),
                    isAdmin = isAdmin,
                    onRestore = { viewModel.restoreMarketItem(item.id) },
                    onPermanentDelete = { viewModel.deleteMarketItemPermanent(item.id) }
                )
            }
        }

        // DELETED TRANSACTION LOGS CATEGORY
        if (deletedTransactions.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Sora-bola voafafa (Logs Transaction Soft Deleted) :",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            items(deletedTransactions, key = { "tx_${it.id}" }) { tx ->
                DeletedItemRow(
                    title = tx.description,
                    subtext = "Amount: ${formatAriary(tx.amount)} Ar • Type: ${tx.type.uppercase()}",
                    deletedAt = tx.deletedAt ?: System.currentTimeMillis(),
                    isAdmin = isAdmin,
                    onRestore = { viewModel.restoreTransaction(tx) },
                    onPermanentDelete = { viewModel.deleteTransactionPermanent(tx.id) }
                )
            }
        }
    }
}

@Composable
fun DeletedItemRow(
    title: String,
    subtext: String,
    deletedAt: Long,
    isAdmin: Boolean,
    onRestore: () -> Unit,
    onPermanentDelete: () -> Unit
) {
    // Calculate countdown remaining
    val ageMs = max(0L, System.currentTimeMillis() - deletedAt)
    val ageDays = (ageMs / (1000L * 60 * 60 * 24)).toInt()
    val daysRemaining = max(0, 30 - ageDays)
    val percentRemaining = daysRemaining / 30f

    val progressColor = when {
        daysRemaining > 20 -> AgrivaGreen
        daysRemaining > 10 -> AgrivaYellow
        else -> Color(0xFFC62828)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtext,
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Beautiful Linear progress countdown visual representing remaining time before swipe
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LinearProgressIndicator(
                        progress = percentRemaining,
                        color = progressColor,
                        trackColor = progressColor.copy(alpha = 0.15f),
                        modifier = Modifier
                            .width(100.dp)
                            .height(6.dp)
                    )
                    Text(
                        text = "$daysRemaining andro sisa (dila GC)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = progressColor
                    )
                }
            }

            // Restore / Permanent delete commands
            if (isAdmin) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = onRestore,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = AgrivaGreen),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Restore, contentDescription = "Restore item")
                    }

                    IconButton(
                        onClick = onPermanentDelete,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color(0xFFC62828)),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "Permanent deletion")
                    }
                }
            } else {
                // Read-only indicator
                IconButton(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Locked", tint = Color.LightGray)
                }
            }
        }
    }
}
