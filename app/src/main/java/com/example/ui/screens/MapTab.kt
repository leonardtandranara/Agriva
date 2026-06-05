package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeatherData
import com.example.ui.theme.AgrivaGreen
import com.example.ui.theme.AgrivaDarkGreen
import com.example.ui.theme.AgrivaYellow
import com.example.ui.viewmodel.AgrivaViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MapTab(
    viewModel: AgrivaViewModel,
    modifier: Modifier = Modifier
) {
    val allWeather by viewModel.allWeather.collectAsState()
    val activeProducts by viewModel.activeMarketItems.collectAsState()
    val selectedRegion by viewModel.selectedMapRegion.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    val isAdmin = userRole == "ADMIN"
    val scrollState = rememberScrollState()

    // Find weather details for selected region
    val regionWeather = allWeather.find { it.region == selectedRegion } ?: WeatherData(
        region = selectedRegion,
        temperature = 25.0,
        rain = 0.0,
        wind = 10.0,
        humidity = 50.0,
        riskLevel = "VERT"
    )

    // Calculate product count in this region
    val productCount = activeProducts.count { it.region == selectedRegion }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
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
                        imageVector = Icons.Default.Map,
                        contentDescription = "Map icon",
                        tint = AgrivaGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = "Sarintany Iraisan-Faritra (Carte Interactive)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "South Madagascar Agrotech Forecast Center • Coords: -23.3516, 43.6792",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Layout wrapping Map Visual and Information Pane
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. THE VECTOR REGIONAL MAP BOX
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("interactive_map_canvas"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(16.dp)
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                ) {
                    val mapWidth = maxWidth
                    val mapHeight = maxHeight
                    val density = androidx.compose.ui.platform.LocalDensity.current
                    
                    val mapWidthPx = with(density) { mapWidth.toPx() }
                    val mapHeightPx = with(density) { mapHeight.toPx() }
                    val mapSizePx = minOf(mapWidthPx, mapHeightPx)
                    
                    val mapSizeDp = minOf(mapWidth, mapHeight)
                    val offsetX = (mapWidth - mapSizeDp) / 2f
                    val offsetY = (mapHeight - mapSizeDp) / 2f
                    
                    val offsetXPx = with(density) { offsetX.toPx() }
                    val offsetYPx = with(density) { offsetY.toPx() }

                    // Interlocking geometric boundaries of Southern Madagascar (0-100 coordinates scale)
                    val regionsCoords = mapOf(
                        "Atsimo-Andrefana" to listOf(
                            12f to 15f,
                            44f to 25f,
                            48f to 60f,
                            38f to 85f,
                            18f to 75f,
                            8f to 45f
                        ),
                        "Androy" to listOf(
                            48f to 60f,
                            38f to 85f,
                            45f to 98f,
                            55f to 92f,
                            58f to 68f
                        ),
                        "Anosy" to listOf(
                            58f to 68f,
                            55f to 92f,
                            78f to 65f,
                            85f to 38f,
                            70f to 35f
                        ),
                        "Ihorombe" to listOf(
                            44f to 25f,
                            48f to 60f,
                            58f to 68f,
                            70f to 35f,
                            68f to 18f,
                            53f to 15f
                        )
                    )

                    val regionCentroids = mapOf(
                        "Atsimo-Andrefana" to (23f to 50f),
                        "Androy" to (48f to 81f),
                        "Anosy" to (70f to 68f),
                        "Ihorombe" to (58f to 35f)
                    )

                    // Draw grid layout and Madagascar regions vector paths
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(allWeather) {
                                detectTapGestures { tapOffset ->
                                    val normX = (tapOffset.x - offsetXPx) * 100f / mapSizePx
                                    val normY = (tapOffset.y - offsetYPx) * 100f / mapSizePx
                                    
                                    var clickedRegion: String? = null
                                    for ((regionName, coords) in regionsCoords) {
                                        if (isPointInPolygon(normX, normY, coords)) {
                                            clickedRegion = regionName
                                            break
                                        }
                                    }
                                    if (clickedRegion != null) {
                                        viewModel.selectMapRegion(clickedRegion)
                                    }
                                }
                            }
                    ) {
                        // Custom Draw background gridlines for technical aesthetic
                        val strokeColor = Color.LightGray.copy(alpha = 0.2f)
                        val gridGap = 60.dp.toPx()
                        for (x in 0..size.width.toInt() step gridGap.toInt()) {
                            drawLine(
                                color = strokeColor,
                                start = Offset(x.toFloat(), 0f),
                                end = Offset(x.toFloat(), size.height),
                                strokeWidth = 1f
                            )
                        }
                        for (y in 0..size.height.toInt() step gridGap.toInt()) {
                            drawLine(
                                color = strokeColor,
                                start = Offset(0f, y.toFloat()),
                                end = Offset(size.width, y.toFloat()),
                                strokeWidth = 1f
                            )
                        }

                        // Draw interlocking vector shapes
                        regionsCoords.forEach { (name, points) ->
                            val path = Path().apply {
                                val first = points.first()
                                moveTo(offsetXPx + first.first * mapSizePx / 100f, offsetYPx + first.second * mapSizePx / 100f)
                                for (i in 1 until points.size) {
                                    val p = points[i]
                                    lineTo(offsetXPx + p.first * mapSizePx / 100f, offsetYPx + p.second * mapSizePx / 100f)
                                }
                                close()
                            }

                            val rLevel = getWeatherRiskForRegion(allWeather, name)
                            val rColor = getRiskColor(rLevel)
                            val isSel = selectedRegion == name
                            val fillAlpha = if (isSel) 0.65f else 0.35f
                            val strokeW = if (isSel) 4.dp.toPx() else 1.5.dp.toPx()
                            val strokeCol = if (isSel) Color(0xFFF9A825) else rColor.copy(alpha = 0.8f)

                            // Halo/Glow boundary for selected region
                            if (isSel) {
                                drawPath(
                                    path = path,
                                    color = strokeCol.copy(alpha = 0.25f),
                                    style = Stroke(width = strokeW * 2.5f)
                                )
                            }

                            // Radial crop suitability overlay gradient
                            val centroid = regionCentroids[name] ?: (50f to 50f)
                            drawPath(
                                path = path,
                                brush = Brush.radialGradient(
                                    colors = listOf(rColor.copy(alpha = fillAlpha), rColor.copy(alpha = fillAlpha * 0.4f)),
                                    center = Offset(offsetXPx + centroid.first * mapSizePx / 100f, offsetYPx + centroid.second * mapSizePx / 100f),
                                    radius = mapSizePx * 0.4f
                                )
                            )

                            // Vector Outline path
                            drawPath(
                                path = path,
                                color = strokeCol,
                                style = Stroke(width = strokeW)
                            )
                        }
                    }

                    // Interactive overlay metadata indicators in Madagascar geography
                    regionCentroids.forEach { (name, centroid) ->
                        val rLevel = getWeatherRiskForRegion(allWeather, name)
                        val rColor = getRiskColor(rLevel)
                        val isSel = selectedRegion == name
                        
                        val (emoji, riskText) = when (name) {
                            "Androy" -> "☀️" to "Haintany / Sec"
                            "Anosy" -> "🌧️" to "Rano / Saforano"
                            "Ihorombe" -> "💨" to "Tafio / Rivotra"
                            "Atsimo-Andrefana" -> "🍃" to "Salama / Optimal"
                            else -> "🌍" to "Milamina"
                        }

                        // Compute absolute Dp coordinates for overlay tags
                        val leftDp = offsetX + (mapSizeDp * (centroid.first / 100f))
                        val topDp = offsetY + (mapSizeDp * (centroid.second / 100f))

                        Box(
                            modifier = Modifier
                                .offset(
                                    x = leftDp - 60.dp,
                                    y = topDp - 26.dp
                                )
                                .width(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier
                                    .clickable { viewModel.selectMapRegion(name) }
                                    .testTag("map_region_$name"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSel) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                ),
                                border = BorderStroke(
                                    if (isSel) 2.dp else 1.dp,
                                    if (isSel) Color(0xFFF9A825) else rColor.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isSel) 4.dp else 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(text = emoji, fontSize = 11.sp)
                                        Text(
                                            text = name,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                    }
                                    Text(
                                        text = riskText,
                                        fontSize = 7.sp,
                                        color = rColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Legend Panel inside Map
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), shape = RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFE2E8F0), shape = RoundedCornerShape(8.dp))
                            .padding(6.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("FANAFALANA LOZA / ANALYSE:", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                LegendItem(color = Color(0xFF2E7D32), text = "🍃 Salama")
                                LegendItem(color = Color(0xFFF9A825), text = "🌧️ Orana")
                                LegendItem(color = Color(0xFFEF6C00), text = "💨 Rivotra")
                                LegendItem(color = Color(0xFFC62828), text = "☀️ Haintany")
                            }
                        }
                    }

                    // Compass Indicator on Map
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), shape = RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFFE2E8F0), shape = RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("AVARATRA (N)", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Icon(Icons.Default.Navigation, contentDescription = "compass", modifier = Modifier.size(16.dp), tint = AgrivaGreen)
                            Text("MADAGASCAR S.", fontSize = 7.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // 2. DETAILED INFO PANE FOR THE SELECTED REGION
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Faritra : $selectedRegion",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when(selectedRegion) {
                                        "Androy" -> "Faritra atsimo farany • Maindry sy mafana"
                                        "Anosy" -> "Antsinanana atsimo • Mandona ary manamorona ranomasina"
                                        "Ihorombe" -> "Imofampana • Lembalemba avo sy be tafio-drivotra"
                                        "Atsimo-Andrefana" -> "Andrefana • Velaran-tany tsara fanondrahana"
                                        else -> "Iraisan-karazany"
                                    },
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            RiskIndicatorBadge(riskLevel = regionWeather.riskLevel)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))

                        // Meteorological Indicators Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            WeatherIndicatorMini(
                                icon = Icons.Default.Thermostat,
                                label = "Hafanana",
                                value = "${regionWeather.temperature}°C",
                                color = Color(0xFFE64A19)
                            )
                            WeatherIndicatorMini(
                                icon = Icons.Default.WaterDrop,
                                label = "Mando",
                                value = "${regionWeather.humidity}%",
                                color = Color(0xFF1976D2)
                            )
                            WeatherIndicatorMini(
                                icon = Icons.Default.Air,
                                label = "Rivotra",
                                value = "${regionWeather.wind} km/h",
                                color = Color(0xFF00796B)
                            )
                            WeatherIndicatorMini(
                                icon = Icons.Default.Cloud,
                                label = "Orana",
                                value = "${regionWeather.rain} mm",
                                color = Color(0xFF512DA8)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Focus Crop Vulnerability Analysis Section
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = when (regionWeather.riskLevel) {
                                    "ROUGE" -> Color(0xFFC62828).copy(alpha = 0.05f)
                                    "ORANGE" -> Color(0xFFEF6C00).copy(alpha = 0.05f)
                                    "JAUNE" -> Color(0xFFF9A825).copy(alpha = 0.05f)
                                    else -> AgrivaGreen.copy(alpha = 0.05f)
                                }
                            ),
                            border = BorderStroke(
                                1.dp,
                                when (regionWeather.riskLevel) {
                                    "ROUGE" -> Color(0xFFC62828).copy(alpha = 0.2f)
                                    "ORANGE" -> Color(0xFFEF6C00).copy(alpha = 0.2f)
                                    "JAUNE" -> Color(0xFFF9A825).copy(alpha = 0.2f)
                                    else -> AgrivaGreen.copy(alpha = 0.2f)
                                }
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = when(regionWeather.riskLevel) {
                                            "ROUGE" -> Icons.Default.Warning
                                            "ORANGE" -> Icons.Default.Air
                                            "JAUNE" -> Icons.Default.WaterDrop
                                            else -> Icons.Default.CheckCircle
                                        },
                                        contentDescription = "Risk Indicator",
                                        tint = when(regionWeather.riskLevel) {
                                            "ROUGE" -> Color(0xFFC62828)
                                            "ORANGE" -> Color(0xFFEF6C00)
                                            "JAUNE" -> Color(0xFFF9A825)
                                            else -> Color(0xFF2E7D32)
                                        },
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Vulnerability diagnosis / Diagnostika Agronomika",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                
                                Text(
                                    text = when (selectedRegion) {
                                        "Androy" -> "RISIQUE DE SECHERESSE EXTRÊME : Le déficit hydrique sévère (0mm de pluie) menace les parcelles de Maïs et Sorgho. Risque de flétrissement permanent. Recommandation : Activer l'irrigation d'appoint et pailler les sols."
                                        "Anosy" -> "RISQUE D'EROSION & INONDATION : Les pluies intenses de mousson (${regionWeather.rain}mm) provoquent le lessivage des nutriments dans les cultures maraîchères. Recommandation : Aménager des canaux d'évacuation."
                                        "Ihorombe" -> "RISQUE DE DEGATS STRUCTURELS : Des rafales de vent soutenues (${regionWeather.wind}km/h) risquent de casser les tiges de haricots et de dessécher prématurément la litière forestière. Recommandation : Brise-vents végétaux."
                                        "Atsimo-Andrefana" -> "SITUATION HYDROLOGIQUE OPTIMALE : Des conditions idéales combinant ensoleillement et précipitations favorisent la riziculture (Riz Gasy) ainsi que l'arachide. Rentabilité maximale estimée."
                                        else -> "Diagnostic général de résilience."
                                    },
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Active market products count
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AgrivaGreen.copy(alpha = 0.08f), shape = RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = "store",
                                tint = AgrivaGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "$productCount vokatra mavitrika hita ato amin'ity faritra ity",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AgrivaDarkGreen
                            )
                        }

                        // Specific warning advice
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = getRiskAdvice(selectedRegion, regionWeather.riskLevel),
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        )
                    }
                }

                // 3. ADMIN WEATHER & RISK CONTROL PANEL
                if (isAdmin) {
                    AdminWeatherControls(
                        region = selectedRegion,
                        currentWeather = regionWeather,
                        onUpdate = { risk, t, r, w, h ->
                            viewModel.updateRegionRisk(selectedRegion, risk, t, r, w, h)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(8.dp).background(color, shape = RoundedCornerShape(2.dp)))
        Text(text = text, fontSize = 8.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
    }
}

// Ray casting algorithm for pixel-precise region SVG hit testing
fun isPointInPolygon(px: Float, py: Float, polygon: List<Pair<Float, Float>>): Boolean {
    var intersectCount = 0
    val count = polygon.size
    for (i in 0 until count) {
        val p1 = polygon[i]
        val p2 = polygon[(i + 1) % count]
        if (((p1.second > py) != (p2.second > py)) &&
            (px < (p2.first - p1.first) * (py - p1.second) / (p2.second - p1.second) + p1.first)
        ) {
            intersectCount++
        }
    }
    return intersectCount % 2 != 0
}

@Composable
fun DummyRegionNodeToAvoidCompileErrors() {}

@Composable
fun WeatherIndicatorMini(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun RiskIndicatorBadge(riskLevel: String) {
    val color = getRiskColor(riskLevel)
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), shape = RoundedCornerShape(50.dp))
            .border(1.dp, color.copy(alpha = 0.4f), shape = RoundedCornerShape(50.dp))
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        Text(
            text = "RISIKY: $riskLevel",
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun AdminWeatherControls(
    region: String,
    currentWeather: WeatherData,
    onUpdate: (risk: String, temp: Double, rain: Double, wind: Double, hum: Double) -> Unit
) {
    var risk by remember(currentWeather) { mutableStateOf(currentWeather.riskLevel) }
    var temp by remember(currentWeather) { mutableStateOf(currentWeather.temperature) }
    var rain by remember(currentWeather) { mutableStateOf(currentWeather.rain) }
    var wind by remember(currentWeather) { mutableStateOf(currentWeather.wind) }
    var hum by remember(currentWeather) { mutableStateOf(currentWeather.humidity) }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("admin_weather_override_form"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, AgrivaYellow.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Security, contentDescription = "admin lock", tint = AgrivaYellow, modifier = Modifier.size(22.dp))
                Text(
                    text = "Mpitondra (ADMIN) - Override Risiky & Météo [$region]",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Divider()

            // Risk Selection Button Row
            Text("Ambaratonga loza (Alerte Risque) :", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val levels = listOf("VERT", "JAUNE", "ORANGE", "ROUGE")
                levels.forEach { level ->
                    val isSelected = risk == level
                    val col = getRiskColor(level)
                    val bg = if (isSelected) col else col.copy(alpha = 0.1f)
                    val border = if (isSelected) BorderStroke(2.dp, Color.Black) else BorderStroke(1.dp, col.copy(alpha = 0.4f))
                    val textCol = if (isSelected) Color.White else col

                    Button(
                        onClick = { risk = level },
                        modifier = Modifier.weight(1f).height(36.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = textCol),
                        shape = RoundedCornerShape(8.dp),
                        border = border,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(level, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            // Met Sliders
            Column {
                Text("Hafanana (Température): ${temp.toInt()}°C", fontSize = 11.sp)
                Slider(
                    value = temp.toFloat(),
                    onValueChange = { temp = it.toDouble() },
                    valueRange = 10f..45f,
                    colors = SliderDefaults.colors(thumbColor = AgrivaGreen, activeTrackColor = AgrivaGreen)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Orana (Pluie): ${rain.toInt()}mm", fontSize = 11.sp)
                    Slider(
                        value = rain.toFloat(),
                        onValueChange = { rain = it.toDouble() },
                        valueRange = 0f..200f,
                        colors = SliderDefaults.colors(thumbColor = AgrivaGreen)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Rivotra (Vent): ${wind.toInt()}km/h", fontSize = 11.sp)
                    Slider(
                        value = wind.toFloat(),
                        onValueChange = { wind = it.toDouble() },
                        valueRange = 0f..120f,
                        colors = SliderDefaults.colors(thumbColor = AgrivaGreen)
                    )
                }
            }

            Button(
                onClick = { onUpdate(risk, temp, rain, wind, hum) },
                modifier = Modifier.fillMaxWidth().height(42.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgrivaGreen)
            ) {
                Icon(Icons.Default.Save, contentDescription = "save", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tehirizo ny Fanovana", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Helpers
fun getRiskColor(risk: String): Color {
    return when (risk.uppercase()) {
        "VERT" -> Color(0xFF2E7D32)
        "JAUNE" -> Color(0xFFF9A825)
        "ORANGE" -> Color(0xFFEF6C00)
        "ROUGE" -> Color(0xFFC62828)
        else -> Color.Gray
    }
}

fun getWeatherRiskForRegion(records: List<WeatherData>, regionName: String): String {
    return records.find { it.region == regionName }?.riskLevel ?: "VERT"
}

fun getRiskAdvice(region: String, risk: String): String {
    return when (risk) {
        "VERT" -> "Tsara ny toe-javatra ho an'ny fambolena ao $region. Tsy misy loza manambana amin'izao fotoana izao. Tetikasa fambolena ara-dalàna."
        "JAUNE" -> "Tandremo: Misy fiovana kely amin'ny orana sy ny toetr'andro ao $region. Amporisihina ny fanaraha-maso ny fitarihan-drano sy ny tatatra."
        "ORANGE" -> "Alerte Orange: Rivotra be na haintany antonony ao $region. Arovy ny rafi-pambolena ary tehirizo ny rano. Manomboka miomana amin'ny fiarovana."
        "ROUGE" -> "ALERTE ROUGE (LOZA BE): Haintany henjana sy loza mitatao ho an'ny vokatra ao $region. Fadio ny mandany rano, arovy ny tahirim-bola simulated wallet ary araho ny toromarika Agriva."
        else -> "Tsy misy toromarika manokana ho an'ny faritra $region."
    }
}
