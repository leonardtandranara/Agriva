package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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
                    .height(340.dp)
                    .testTag("interactive_map_canvas"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Custom Draw Grid-Lines for Map tech aesthetic
                    Canvas(modifier = Modifier.fillMaxSize()) {
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
                    }

                    // Interactive Region Buttons placed representationally like Southern Madagascar Geography:
                    // Ihorombe is center-north inland. Atsimo-Andrefana is west coast.
                    // Androy is southernmost tip. Anosy is south-eastern coast.
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Region: Atsimo-Andrefana (West Coast)
                        RegionNode(
                            name = "Atsimo-Andrefana",
                            riskLevel = getWeatherRiskForRegion(allWeather, "Atsimo-Andrefana"),
                            productCount = activeProducts.count { it.region == "Atsimo-Andrefana" },
                            isSelected = selectedRegion == "Atsimo-Andrefana",
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .offset(x = 20.dp, y = (-20).dp),
                            onClick = { viewModel.selectMapRegion("Atsimo-Andrefana") }
                        )

                        // Region: Ihorombe (Inland Highlands)
                        RegionNode(
                            name = "Ihorombe",
                            riskLevel = getWeatherRiskForRegion(allWeather, "Ihorombe"),
                            productCount = activeProducts.count { it.region == "Ihorombe" },
                            isSelected = selectedRegion == "Ihorombe",
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(x = 40.dp, y = 30.dp),
                            onClick = { viewModel.selectMapRegion("Ihorombe") }
                        )

                        // Region: Anosy (Southeast Coast)
                        RegionNode(
                            name = "Anosy",
                            riskLevel = getWeatherRiskForRegion(allWeather, "Anosy"),
                            productCount = activeProducts.count { it.region == "Anosy" },
                            isSelected = selectedRegion == "Anosy",
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-30).dp, y = (-40).dp),
                            onClick = { viewModel.selectMapRegion("Anosy") }
                        )

                        // Region: Androy (Extreme Southern Tip)
                        RegionNode(
                            name = "Androy",
                            riskLevel = getWeatherRiskForRegion(allWeather, "Androy"),
                            productCount = activeProducts.count { it.region == "Androy" },
                            isSelected = selectedRegion == "Androy",
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(x = (-10).dp, y = (-10).dp),
                            onClick = { viewModel.selectMapRegion("Androy") }
                        )

                        // Compass Indicator on Map
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFFE2E8F0), shape = RoundedCornerShape(10.dp))
                                .padding(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("AVARATRA (N)", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Icon(Icons.Default.Navigation, contentDescription = "compass", modifier = Modifier.size(18.dp), tint = AgrivaGreen)
                                Text("TOLIARA SCALE", fontSize = 8.sp, color = Color.Gray)
                            }
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
                            Text(
                                text = "Faritra : $selectedRegion",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

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
fun RegionNode(
    name: String,
    riskLevel: String,
    productCount: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val riskColor = getRiskColor(riskLevel)
    val borderStroke = if (isSelected) BorderStroke(3.dp, AgrivaYellow) else BorderStroke(1.dp, riskColor.copy(alpha = 0.5f))
    val containerCol = if (isSelected) riskColor.copy(alpha = 0.35f) else riskColor.copy(alpha = 0.2f)

    Card(
        modifier = modifier
            .width(150.dp)
            .clickable { onClick() }
            .testTag("map_region_$name"),
        colors = CardDefaults.cardColors(containerColor = containerCol),
        border = borderStroke,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Little ping dot
                Canvas(modifier = Modifier.size(8.dp)) {
                    drawCircle(color = riskColor)
                }
                Text(
                    text = "Lojika: $riskLevel",
                    fontSize = 10.sp,
                    color = riskColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "$productCount vokatra",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

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
