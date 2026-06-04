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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketItem
import com.example.ui.theme.AgrivaGreen
import com.example.ui.theme.AgrivaDarkGreen
import com.example.ui.theme.AgrivaYellow
import com.example.ui.viewmodel.AgrivaViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MarketTab(
    viewModel: AgrivaViewModel,
    modifier: Modifier = Modifier
) {
    val activeProducts by viewModel.activeMarketItems.collectAsState()
    val searchQuery by viewModel.marketSearchQuery.collectAsState()
    val regionFilter by viewModel.marketRegionFilter.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    val isAdmin = userRole == "ADMIN"
    var showAddDialog by remember { mutableStateOf(false) }

    // Computed filtered list
    val filteredProducts = activeProducts.filter { item ->
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true)
        val matchesRegion = regionFilter == "TOUT" || item.region == regionFilter
        matchesSearch && matchesRegion
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Market Icon",
                        tint = AgrivaGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = "Tsaram-bary sy Vokatra (Prix du Marché)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Météo-indexé • Synchro locale offline",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isAdmin) {
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AgrivaGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("open_add_product_dialog")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "AddIcon", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Vokatra Vaovao", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Filters Panel
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setMarketFilters(it, regionFilter) },
                    placeholder = { Text("Hikaroka vokatra (ex: Riz)...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("market_search_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AgrivaGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                // Region Chips Row
                Text(text = "Sivana Faritra (Filtrer par région) :", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val regions = listOf("TOUT", "Atsimo-Andrefana", "Androy", "Anosy", "Ihorombe")
                    regions.forEach { r ->
                        val isSelected = regionFilter == r
                        val chipBg = if (isSelected) AgrivaGreen else MaterialTheme.colorScheme.surfaceVariant
                        val chipTextCol = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                        Button(
                            onClick = { viewModel.setMarketFilters(searchQuery, r) },
                            colors = ButtonDefaults.buttonColors(containerColor = chipBg, contentColor = chipTextCol),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("filter_chip_$r"),
                            elevation = null
                        ) {
                            Text(text = if (r == "TOUT") "Rehetra (Tout)" else r, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Products Table/List
        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Inventory, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Text("Tsy misy vokatra hita mifanaraka amin'ny sivana.", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("market_products_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    ProductRowCard(
                        product = product,
                        isAdmin = isAdmin,
                        onToggleStatus = { viewModel.toggleMarketItemStatus(product) },
                        onSoftDelete = { viewModel.softDeleteMarketItem(product.id) }
                    )
                }
            }
        }

        // Add Product Dialog (ADMIN Only)
        if (showAddDialog && isAdmin) {
            AddProductDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, price, region, variation ->
                    viewModel.addMarketItem(name, price, region, variation)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun ProductRowCard(
    product: MarketItem,
    isAdmin: Boolean,
    onToggleStatus: () -> Unit,
    onSoftDelete: () -> Unit
) {
    val showInactiveCol = product.status == "inactive"
    val cardColor = if (showInactiveCol) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
    val borderCol = if (showInactiveCol) Color.LightGray.copy(alpha = 0.5f) else Color(0xFFE2E8F0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.dp, borderCol),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = product.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showInactiveCol) Color.Gray else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Status badge
                        Box(
                            modifier = Modifier
                                .background(
                                    if (showInactiveCol) Color.Gray.copy(alpha = 0.2f) else AgrivaGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (showInactiveCol) "Tsy mavitrika" else "Mavitrika",
                                fontSize = 8.sp,
                                color = if (showInactiveCol) Color.DarkGray else AgrivaDarkGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "Faritra : ${product.region}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                // Variation indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val isPositive = product.variation >= 0
                    val varColor = if (isPositive) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                    Icon(
                        imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = varColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${if (isPositive) "+" else ""}${product.variation}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = varColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = borderCol.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price Tag in MGA
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Payments, contentDescription = null, tint = AgrivaGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${formatAriary(product.price)} Ariary (Ar)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = if (showInactiveCol) Color.Gray else AgrivaDarkGreen
                    )
                }

                // Admin controls: toggle active, delete
                if (isAdmin) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Toggle Status Active/Inactive Button
                        IconButton(
                            onClick = onToggleStatus,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("toggle_status_${product.id}")
                        ) {
                            Icon(
                                imageVector = if (showInactiveCol) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "toggle status",
                                tint = if (showInactiveCol) AgrivaGreen else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Soft Delete Button
                        IconButton(
                            onClick = onSoftDelete,
                            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("soft_delete_${product.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "soft delete",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, price: Double, region: String, variation: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("Atsimo-Andrefana") }
    var variationStr by remember { mutableStateOf("") }

    val regions = listOf("Atsimo-Andrefana", "Androy", "Anosy", "Ihorombe")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.AddBusiness, contentDescription = null, tint = AgrivaGreen)
                Text("Hanampy Vokatra Vaovao", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Anaran'ny Vokatra (ex: Riz Rouge)") },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_product_name_input")
                )

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Tsarany - Prix (Ar/kg)") },
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_product_price_input")
                )

                OutlinedTextField(
                    value = variationStr,
                    onValueChange = { variationStr = it },
                    label = { Text("Fiovana (%, ex: +2.5 na -1.2)") },
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_product_variation_input")
                )

                Text("Faritra misy ny vokatra :", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    regions.forEach { r ->
                        val isSel = region == r
                        val bg = if (isSel) AgrivaGreen else MaterialTheme.colorScheme.surfaceVariant
                        val tx = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                        Button(
                            onClick = { region = r },
                            colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = tx),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp),
                            elevation = null
                        ) {
                            Text(r.split("-").first(), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 0.0
                    val variation = variationStr.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && price > 0.0) {
                        onAdd(name, price, region, variation)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AgrivaGreen),
                modifier = Modifier.testTag("confirm_add_product_button")
            ) {
                Text("Insert Vokatra", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hanafoana (Annuler)", color = Color.Gray, fontSize = 12.sp)
            }
        }
    )
}

fun formatAriary(amount: Double): String {
    val formatter = NumberFormat.getInstance(Locale.FRANCE)
    return formatter.format(amount)
}
