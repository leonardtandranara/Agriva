package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.WeatherData
import com.example.ui.theme.AgrivaGreen
import com.example.ui.theme.AgrivaDarkGreen
import com.example.ui.theme.AgrivaYellow
import com.example.ui.viewmodel.AgrivaViewModel

@Composable
fun WeatherTab(
    viewModel: AgrivaViewModel,
    modifier: Modifier = Modifier
) {
    val allWeather by viewModel.allWeather.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    val isAdmin = userRole == "ADMIN"
    val scrollState = rememberScrollState()

    // Playground Interactive Calculator State 
    var simRegion by remember { mutableStateOf("Androy") }
    var simTemp by remember { mutableStateOf(30.0) }
    var simRain by remember { mutableStateOf(10.0) }
    var simWind by remember { mutableStateOf(20.0) }
    var simHum by remember { mutableStateOf(45.0) }

    // Run calcul logic
    val calculatedRisk = calculateDynamicRisk(simTemp, simRain, simWind, simHum)

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
                        imageVector = Icons.Default.CloudQueue,
                        contentDescription = "Meteo Icon",
                        tint = AgrivaGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = "Météo Agricole Prédictive (Risk Engine)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Drafitra fikajiana sy fisorohana any Atsimon'i Madagasikara",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 1. REGIONAL WEATHER CARDS
        Text(
            text = "Toetrandro isam-paritra amin'izao fotoana izao :",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        allWeather.forEach { weather ->
            RegionalWeatherPanelCard(weather = weather)
        }

        // 2. RISK ENGINE CALCULATORY PLAYGROUND (Dynamic sandbox testing)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("weather_calculator_box"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = AgrivaYellow)
                    Text(
                        text = "Fikajiana Risky (Agriva Risk Engine API Simulator)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Divider()

                Text(
                    text = "Ampiasao ireto famantarana ireto mba hanombanana ny risiky ny fambolena amin'ny alalan'ny fizahantany dynamique :",
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Input controls for calculator
                Column {
                    Text(text = "Hafanana (Température) : ${simTemp.toInt()}°C", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Slider(
                        value = simTemp.toFloat(),
                        onValueChange = { simTemp = it.toDouble() },
                        valueRange = 10f..45f,
                        colors = SliderDefaults.colors(thumbColor = AgrivaGreen, activeTrackColor = AgrivaGreen)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Orana (Pluie) : ${simRain.toInt()} mm", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Slider(value = simRain.toFloat(), onValueChange = { simRain = it.toDouble() }, valueRange = 0f..200f)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Mando (Humidité) : ${simHum.toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Slider(value = simHum.toFloat(), onValueChange = { simHum = it.toDouble() }, valueRange = 10f..100f)
                    }
                }

                Column {
                    Text(text = "Rivotra (Vent) : ${simWind.toInt()} km/h", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Slider(
                        value = simWind.toFloat(),
                        onValueChange = { simWind = it.toDouble() },
                        valueRange = 0f..120f,
                        colors = SliderDefaults.colors(thumbColor = AgrivaGreen, activeTrackColor = AgrivaGreen)
                    )
                }

                // Dynamic calculation result panel
                val computedColor = getRiskColor(calculatedRisk)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(computedColor.copy(alpha = 0.15f), shape = RoundedCornerShape(10.dp))
                        .border(1.dp, computedColor.copy(alpha = 0.4f), shape = RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "RISIKY SY ALERTE VOAKAJY (RESULTAT)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = computedColor
                        )

                        Text(
                            text = calculatedRisk,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = computedColor
                        )

                        Text(
                            text = getDynamicRiskInterpretation(calculatedRisk),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )

                        // Save current prediction is Admin
                        if (isAdmin) {
                            Spacer(modifier = Modifier.height(6.dp))
                            TextButton(
                                onClick = {
                                    viewModel.updateRegionRisk(simRegion, calculatedRisk, simTemp, simRain, simWind, simHum)
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = AgrivaDarkGreen)
                            ) {
                                Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Aparitaho ho an'ny Faritra (Publish to $simRegion)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Region choose row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Select region targets: ", fontSize = 9.sp, color = Color.Gray)
                                val regions = listOf("Atsimo-Andrefana", "Androy", "Anosy", "Ihorombe")
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    regions.forEach { r ->
                                        val isS = simRegion == r
                                        Box(
                                            modifier = Modifier
                                                .background(if (isS) AgrivaGreen else Color.LightGray, shape = RoundedCornerShape(4.dp))
                                                .clickable { simRegion = r }
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(r.split("-").first(), fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RegionalWeatherPanelCard(weather: WeatherData) {
    val riskColor = getRiskColor(weather.riskLevel)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = AgrivaGreen)
                    Text(
                        text = weather.region,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Alert label with risk color
                Box(
                    modifier = Modifier
                        .background(riskColor.copy(alpha = 0.15f), shape = RoundedCornerShape(50.dp))
                        .border(1.dp, riskColor.copy(alpha = 0.4f), shape = RoundedCornerShape(50.dp))
                        .padding(horizontal = 12.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = weather.riskLevel,
                        color = riskColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(12.dp))

            // Meteorological Params grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherIndicatorMini(
                    icon = Icons.Default.Thermostat,
                    label = "Hafanana",
                    value = "${weather.temperature}°C",
                    color = Color(0xFFE64A19)
                )
                WeatherIndicatorMini(
                    icon = Icons.Default.WaterDrop,
                    label = "Mando",
                    value = "${weather.humidity.toInt()}%",
                    color = Color(0xFF1976D2)
                )
                WeatherIndicatorMini(
                    icon = Icons.Default.Air,
                    label = "Rivotra",
                    value = "${weather.wind} km/h",
                    color = Color(0xFF00796B)
                )
                WeatherIndicatorMini(
                    icon = Icons.Default.Cloud,
                    label = "Orana",
                    value = "${weather.rain} mm",
                    color = Color(0xFF512DA8)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// Risk calculation logic (Weather risks based on agricultural indicators)
fun calculateDynamicRisk(temp: Double, rain: Double, wind: Double, hum: Double): String {
    return when {
        // Severe heatwave combined with absolute drought => RED warning (Critical Drought)
        temp >= 33.0 && rain == 0.0 && hum <= 25.0 -> "ROUGE"

        // Severe cyclone condition: ultra high wind or ultra heavy rains => RED warning (Cyclone/Flooding)
        wind >= 55.0 || rain >= 90.0 -> "ROUGE"

        // Strong heat drying up plants combined with low rains => ORANGE warning (High climatic stress)
        temp >= 31.0 && rain <= 4.0 && hum <= 35.0 -> "ORANGE"

        // Very high winds threat => ORANGE warning (Strong storms)
        wind >= 38.0 -> "ORANGE"

        // Light drought danger or warning sign -> JAUNE (Moderate stress alert)
        temp >= 29.0 && rain <= 8.0 && hum <= 45.0 -> "JAUNE"

        // Damp warnings, minor heavy weather -> JAUNE
        wind >= 25.0 || rain >= 35.0 -> "JAUNE"

        // Safe green parameters
        else -> "VERT"
    }
}

fun getDynamicRiskInterpretation(risk: String): String {
    return when (risk) {
        "VERT" -> "Toe-tany tonga lafatra (Sécurisé). Ny orana sy ny hafanana dia tsara dia tsara ho an'ny fambolena."
        "JAUNE" -> "Fiambenana (Tandremo). Manomboka mikorontana kely ny toetrandro. Araho maso ny famantarana."
        "ORANGE" -> "Fampitandremana (Loza). Tondra-drano na haintany antonony. Alao sary an-tsaina ny fanalefahana loza."
        "ROUGE" -> "ALERTE SOS (LOZA BE). Atahorana ny mosary sy ny haintany na fahasimbana be. Tehirizo ny wallet!"
        else -> ""
    }
}
