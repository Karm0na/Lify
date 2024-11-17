package com.example.lify.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lify.R

@Composable
fun EstadisticasScreen() {
    var selectedPeriod by remember { mutableStateOf("7 días") } // Período seleccionado
    val periods = listOf("7 días", "30 días", "6 meses", "1 año", "Todo")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Fondo oscuro
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título
        Text(
            text = "Estadísticas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Días entrenados
        DiasEntrenadosRow()

        // Mapa de músculos trabajados
        MusculosEntrenadosCard()

        // Selector de períodos desplazable
        PeriodSelectorHorizontal(
            periods = periods,
            selectedPeriod = selectedPeriod,
            onPeriodSelected = { selectedPeriod = it }
        )

        // Lista de estadísticas con contenido expandible
        EstadisticasList(selectedPeriod = selectedPeriod)
    }
}

// Componente: Días entrenados
@Composable
fun DiasEntrenadosRow() {
    val dias = listOf("L", "M", "X", "J", "V", "S", "D")
    val entrenados = listOf(true, true, false, false, true, true, false) // Datos ficticios

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        dias.forEachIndexed { index, dia ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (entrenados[index]) Color(0xFFFFA000) else Color(0xFF2C2C2C),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dia,
                    color = if (entrenados[index]) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Componente: Mapa de músculos entrenados
@Composable
fun MusculosEntrenadosCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Aquí puedes añadir una imagen representando los músculos entrenados
            Text(
                text = "Músculos entrenados",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

// Componente: Barra horizontal desplazable para selector de períodos
@Composable
fun PeriodSelectorHorizontal(periods: List<String>, selectedPeriod: String, onPeriodSelected: (String) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(periods.size) { index ->
            val period = periods[index]
            Button(
                onClick = { onPeriodSelected(period) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedPeriod == period) Color(0xFFFFA000) else Color(0xFF2C2C2C),
                    contentColor = if (selectedPeriod == period) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(text = period, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Componente: Lista de estadísticas con funcionalidad expandible
@Composable
fun EstadisticasList(selectedPeriod: String) {
    // Estado para expandir o colapsar
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Distribución de músculos",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded } // Expandir o colapsar
                    .padding(16.dp)
            ) {
                Text(
                    text = "Gráfica de distribución de músculos",
                    color = Color.White,
                    fontSize = 14.sp
                )
                // Animación para mostrar o colapsar el contenido
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(animationSpec = tween(300)),
                    exit = shrinkVertically(animationSpec = tween(300))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(Color(0xFFFFA000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aquí iría la gráfica detallada",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
