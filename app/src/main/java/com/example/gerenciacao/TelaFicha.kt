package com.example.gerenciacao.ui.telas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gerenciacao.ui.*

private val AzulTexto = Color(0xFF1B3F6B)

/** "4-6 Meses" -> "4-6 m"; "1 Ano" -> "1 a". */
private fun idadeCurta(idade: String) =
    idade.replace(" Meses", " m").replace(" Mes", " m").replace(" Anos", " a").replace(" Ano", " a")

/** Ficha Individual: foto, identificação, dados clínicos e descrição do bichinho. */
@Composable
fun TelaFicha(id: Int, onVoltar: () -> Unit) {
    val animal = AnimaisMock.buscar(id)
    if (animal == null) {
        LaunchedEffect(Unit) { onVoltar() }
        return
    }
    val especie = if (animal.especie == "Gatos") "Gato" else "Cão"
    val castrado = when {
        animal.castrado && animal.femea -> "Castrada"
        animal.castrado -> "Castrado"
        animal.femea -> "Não castrada"
        else -> "Não castrado"
    }

    Column(Modifier.fillMaxSize().background(Cores.Creme).verticalScroll(rememberScrollState())) {
        // Faixa laranja com a seta e o título
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                .background(Cores.Laranja)
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 22.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = Color.White,
                modifier = Modifier.size(36.dp).clickable(onClick = onVoltar),
            )
            Spacer(Modifier.height(28.dp))
            Text("Ficha Individual", fontSize = 34.sp, fontWeight = FontWeight.Black, color = Cores.Marrom)
        }

        Box(Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 6.dp)
                    .size(292.dp)
                    .clip(CircleShape)
                    .background(Cores.AzulClaro),
            )
            Image(
                painter = painterResource(animal.foto),
                contentDescription = animal.nome,
                contentScale = ContentScale.Fit,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp).size(170.dp),
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 150.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Cores.Azul)
                    .padding(horizontal = 18.dp, vertical = 18.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(animal.nome, fontSize = 44.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
                    Text(
                        if (animal.femea) "♀" else "♂",
                        fontSize = 38.sp,
                        color = if (animal.femea) Color(0xFFE0568F) else AzulTexto,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        animal.status.rotulo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Cores.Marrom,
                        modifier = Modifier
                            .background(animal.status.cor, RoundedCornerShape(50))
                            .padding(horizontal = 18.dp, vertical = 8.dp),
                    )
                }

                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, AzulTexto, RoundedCornerShape(16.dp))
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DadoFicha(especie, "Espécie", Modifier.weight(1f))
                    Box(Modifier.width(1.dp).height(44.dp).background(AzulTexto))
                    DadoFicha(animal.raca, "Raça", Modifier.weight(1f))
                    Box(Modifier.width(1.dp).height(44.dp).background(AzulTexto))
                    DadoFicha(idadeCurta(animal.idade), "Idade", Modifier.weight(1f), prefixo = "Aprox.")
                }

                Spacer(Modifier.height(16.dp))
                LinhaFicha("${animal.porte} • ${"%.1f".format(animal.pesoKg).replace('.', ',')}kg", "Porte/Peso atual")
                LinhaFicha(castrado, "Status reprodutivo")
                LinhaFicha(animal.caracteristicas, "Características")

                Text(
                    "Descrição do animal",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulTexto,
                    modifier = Modifier.padding(top = 14.dp, bottom = 10.dp),
                )
                Text(
                    animal.descricao,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Justify,
                    color = AzulTexto,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp)
                        .background(Color(0xFFDCEEFF), RoundedCornerShape(24.dp))
                        .padding(18.dp),
                )
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun DadoFicha(valor: String, rotulo: String, modifier: Modifier = Modifier, prefixo: String? = null) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (prefixo != null) Text(prefixo, fontSize = 12.sp, lineHeight = 12.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
        Text(valor, fontSize = 26.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold, color = AzulTexto, maxLines = 1)
        Text(rotulo, fontSize = 12.sp, color = AzulTexto)
    }
}

@Composable
private fun LinhaFicha(valor: String, rotulo: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(valor, fontSize = 21.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold, color = AzulTexto, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(12.dp))
        Text(rotulo, fontSize = 15.sp, color = AzulTexto.copy(alpha = 0.7f), textAlign = TextAlign.End, modifier = Modifier.width(120.dp))
    }
}
