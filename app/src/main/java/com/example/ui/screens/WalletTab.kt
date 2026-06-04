package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WalletTransaction
import com.example.ui.theme.AgrivaGreen
import com.example.ui.theme.AgrivaDarkGreen
import com.example.ui.theme.AgrivaYellow
import com.example.ui.viewmodel.AgrivaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WalletTab(
    viewModel: AgrivaViewModel,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.activeTransactions.collectAsState()
    val balance by viewModel.walletBalance.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    val isAdmin = userRole == "ADMIN"
    var showSimulatorDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab Header
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
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Wallet Icon",
                        tint = AgrivaGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = "Paingam-bola simulated (Wallet)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Microfiancement agricole simulation • Sandbox mode",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 1. BRANDED PHYSICAL CREDIT CARD VISUAL
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(AgrivaGreen, AgrivaDarkGreen)
                        )
                    )
                    .padding(20.dp)
            ) {
                // Background shield watermark accent
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    modifier = Modifier
                        .size(140.dp)
                        .align(Alignment.CenterEnd)
                        .offset(x = 30.dp, y = 20.dp),
                    tint = Color.White.copy(alpha = 0.08f)
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    val formattedEmail = viewModel.currentUserEmail.collectAsState().value ?: "agriva_user@mada.mg"
                    val formattedRole = viewModel.userRole.collectAsState().value ?: "USER"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AGRIVA MADA ACCREDITED",
                            color = AgrivaYellow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )

                        Text(
                            text = "SIMULATOR v1.0",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Balance Display Section
                    Column {
                        Text(
                            text = "Sora-bola misy (Solde disponible) :",
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${formatAriary(balance)} Ar",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.testTag("wallet_balance_text")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Mpihazona (Titulaire)",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 8.sp
                            )
                            Text(
                                text = formattedEmail,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Badge representation of USER level or ADMIN
                        Box(
                            modifier = Modifier
                                .background(AgrivaYellow, shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = formattedRole,
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // Action Trigger Button for transaction builder
        Button(
            onClick = { showSimulatorDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("open_simulation_dialog"),
            colors = ButtonDefaults.buttonColors(containerColor = AgrivaGreen)
        ) {
            Icon(Icons.Default.SyncAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simuler Transaction (Gérer fonds)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        // 2. TRANSACTION HISTORY LOG
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Tantaran'ny Varotra sy Fandrotsahana (Transactions)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                Divider()
                Spacer(modifier = Modifier.height(10.dp))

                if (transactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tsy misy tantara voamarika aloha.",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("wallet_transactions_list"),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(transactions, key = { it.id }) { tx ->
                            TransactionRow(
                                transaction = tx,
                                isAdmin = isAdmin,
                                onSoftDelete = { viewModel.softDeleteTransaction(tx) }
                            )
                        }
                    }
                }
            }
        }

        // Simulating modal logic
        if (showSimulatorDialog) {
            WalletSimulatorDialog(
                onDismiss = { showSimulatorDialog = false },
                onSimulate = { type, amt, desc ->
                    viewModel.simulateWalletTransaction(type, amt, desc)
                    showSimulatorDialog = false
                }
            )
        }
    }
}

@Composable
fun TransactionRow(
    transaction: WalletTransaction,
    isAdmin: Boolean,
    onSoftDelete: () -> Unit
) {
    val isCredit = transaction.type == "credit"
    val accentColor = if (isCredit) Color(0xFF2E7D32) else Color(0xFFD32F2F)
    val bgMarker = if (isCredit) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("transaction_item_${transaction.id}")
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = RoundedCornerShape(10.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Circle type indicator
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(bgMarker, shape = RoundedCornerShape(50.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCredit) Icons.Default.AddCircle else Icons.Default.RemoveCircle,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatShortDate(transaction.createdAt),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        // Amount and Admin deletes
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isCredit) "+" else "-"}${formatAriary(transaction.amount)} Ar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = accentColor
                )
                Text(
                    text = "Bal: ${formatAriary(transaction.balanceAfter)}",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            if (isAdmin) {
                IconButton(
                    onClick = onSoftDelete,
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("soft_delete_tx_${transaction.id}"),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "soft delete", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun WalletSimulatorDialog(
    onDismiss: () -> Unit,
    onSimulate: (type: String, amount: Double, desc: String) -> Unit
) {
    var type by remember { mutableStateOf("credit") } // "credit" or "debit"
    var amountStr by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = AgrivaGreen)
                Text("Simuler Enregistrement", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                // Type Select Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10.dp))
                        .padding(2.dp)
                ) {
                    Button(
                        onClick = { type = "credit" },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "credit") Color(0xFF2E7D32) else Color.Transparent,
                            contentColor = if (type == "credit") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Dépôt (+ Credit)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { type = "debit" },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "debit") Color(0xFFD32F2F) else Color.Transparent,
                            contentColor = if (type == "debit") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Retrait (- Débit)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Isan'ny vola - Montant (Ar)") },
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("simulation_amount_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Torolalana - Motif / Description") },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("simulation_desc_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    if (amt > 0.0 && description.isNotBlank()) {
                        onSimulate(type, amt, description)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AgrivaGreen),
                modifier = Modifier.testTag("confirm_simulation_button")
            ) {
                Text("Ampidiro simulation", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hanafoana (Annuler)", color = Color.Gray, fontSize = 12.sp)
            }
        }
    )
}

fun formatShortDate(timestamp: Long): String {
    val date = Date(timestamp)
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(date)
}
