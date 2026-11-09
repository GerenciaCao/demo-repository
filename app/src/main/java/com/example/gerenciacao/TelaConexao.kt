package com.example.gerenciacao.ui.telas

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gerenciacao.R
import com.example.gerenciacao.ui.*

private val CinzaCard = Color(0xFFECE8E0)
private val Dourado = Color(0xFFE6A03A)
private val AzulFiltro = Color(0xFF2B5282)

data class RedeAbrigo(
    val nome: String,
    val endereco: String,
    val descricao: String,
    @DrawableRes val foto: Int? = null,
    val suaConexao: Boolean = false,
)

/** Dados de EXEMPLO (TODO: vir da API, coleção de abrigos conectados). */
object ConexoesMock {
    val redes = listOf(
        RedeAbrigo(
            "ONG Patas Unidas", "Rua Castro Machado, 451, SP",
            "Por trás de cada olhar acolhido na ONG Patas Unidas, existe o sonho de uma nova oportunidade e de um lar cheio de carinho para recomeçar.",
            R.drawable.conexao_patas_unidas, suaConexao = true,
        ),
        RedeAbrigo(
            "Cantinho dos Focinhos", "Avenida Luz da Maré, 24, RJ",
            "No Cantinho dos Focinhos, cada resgate representa o início de uma nova história, com a esperança de encontrar uma família para a vida toda.",
            R.drawable.conexao_focinhos,
        ),
        RedeAbrigo(
            "Abrigo Lar Animal", "Avenida Maria do Carmo, 122, SP",
            "O Abrigo Lar Animal acredita que todo bichinho merece uma segunda chance. Por isso, trabalha diariamente para ajudar cada um a achar um lar.",
            R.drawable.conexao_lar_animal, suaConexao = true,
        ),
        RedeAbrigo(
            "Patas Felizes", "Rua Machado Deodoro, 145, SP",
            "O Abrigo Patas Felizes é um lugar onde histórias se encontram, vidas são transformadas e cada patinha recebe todo o cuidado que merece.",
            R.drawable.conexao_patas_felizes,
        ),
        RedeAbrigo(
            "Patinhas do Bem", "Rua Bonale da Silva, 15, SP",
            "Na ONG Patinhas do Bem, cada gesto de cuidado faz a diferença. Por trás de cada resgate, existe uma história de amor e recomeço.",
            null, // TODO: foto (ainda não enviada)
        ),
    )
}

@Composable
fun TelaConexao(onVoltar: () -> Unit) {
    var busca by remember { mutableStateOf("") }
    var soMinhas by remember { mutableStateOf(false) }

    val lista = ConexoesMock.redes.filter {
        it.nome.contains(busca, ignoreCase = true) && (!soMinhas || it.suaConexao)
    }

    BaseTelaAzul(onVoltar = onVoltar) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item { Text("Conexão", fontSize = 36.sp, fontWeight = FontWeight.Black, color = Cores.Marrom) }
            item {
                BarraBusca(
                    valor = busca,
                    aoMudar = { busca = it },
                    dica = "Buscar por Rede...",
                    onFiltro = { /* TODO: filtros (cidade, distância) */ },
                    corBorda = null,
                    sombra = true,
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    ChipRede("Todos", selecionado = !soMinhas) { soMinhas = false }
                    ChipRede("Suas Conexões", selecionado = soMinhas) { soMinhas = true }
                }
            }
            items(lista, key = { it.nome }) { rede -> CardRede(rede) }
            if (lista.isEmpty()) {
                item { Text("Nenhum abrigo encontrado.", fontSize = 16.sp, color = Cores.CinzaTexto) }
            }
        }
    }
}

@Composable
private fun ChipRede(texto: String, selecionado: Boolean, onClick: () -> Unit) {
    Text(
        texto,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selecionado) AzulFiltro else Cores.Marrom)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
    )
}

@Composable
private fun CardRede(rede: RedeAbrigo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CinzaCard)
            .clickable { /* TODO: abrir os detalhes do abrigo / pedir conexão */ }
            .padding(14.dp),
    ) {
        if (rede.foto != null) {
            Image(
                painter = painterResource(rede.foto),
                contentDescription = rede.nome,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(24.dp)),
            )
        } else {
            Box(
                Modifier.size(90.dp).clip(RoundedCornerShape(24.dp)).background(Cores.AzulClaro),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Filled.Pets, contentDescription = null, tint = Cores.Azul, modifier = Modifier.size(40.dp)) }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(rede.nome, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Dourado, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(rede.endereco, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Dourado, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(
                rede.descricao,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Justify,
                color = Cores.Marrom,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Cores.MarromClaro,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}
