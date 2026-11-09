package com.example.gerenciacao.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gerenciacao.ui.*

/** Faixas de idade em anos completos. "6-10" vai até 9 anos e 11 meses; "10+" começa em 10 anos. */
enum class FaixaIdade(val rotulo: String, val meses: IntRange) {
    ATE_1("0-1 Ano", 0..23),
    DE_2_A_5("2-5 Anos", 24..71),
    DE_6_A_10("6-10 Anos", 72..119),
    MAIS_10("10+ Anos", 120..Int.MAX_VALUE),
}

data class CriteriosFiltro(
    val status: StatusAnimal? = null,
    val faixasIdade: Set<FaixaIdade> = emptySet(),
    val femea: Boolean? = null, // true = Fêmea, false = Macho, null = todos
) {
    fun aceita(a: Animal): Boolean =
        (status == null || a.status == status) &&
                (faixasIdade.isEmpty() || faixasIdade.any { a.idadeMeses in it.meses }) &&
                (femea == null || a.femea == femea)
}

@Composable
fun FiltroAnimais(
    inicial: CriteriosFiltro,
    onAplicar: (CriteriosFiltro) -> Unit,
    onFechar: () -> Unit,
) {
    var status by remember { mutableStateOf(inicial.status) }
    var faixas by remember { mutableStateOf(inicial.faixasIdade) }
    var femea by remember { mutableStateOf(inicial.femea) }

    Box(Modifier.fillMaxSize()) {
        // Fundo escurecido: tocar fecha o painel
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0x99000000))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onFechar,
                ),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.74f)
                .clip(RoundedCornerShape(topStart = 44.dp, topEnd = 44.dp))
                .background(Cores.Creme)
                .pointerInput(Unit) { detectTapGestures { } }, // não deixa o toque "vazar" para o fundo
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(40.dp).clip(CircleShape).background(Cores.Marrom),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.FilterList, contentDescription = null, tint = Cores.Laranja, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Filtrar", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Fechar",
                    tint = Cores.Marrom,
                    modifier = Modifier.size(32.dp).clickable(onClick = onFechar),
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
            ) {
                TituloSecaoFiltro("Status do Animal")
                StatusAnimal.values().forEach { s ->
                    OpcaoRadio(s.rotulo, status == s) { status = if (status == s) null else s }
                }

                TituloSecaoFiltro("Idade")
                FaixaIdade.values().forEach { f ->
                    OpcaoCheck(f.rotulo, f in faixas) { faixas = if (f in faixas) faixas - f else faixas + f }
                }

                TituloSecaoFiltro("Sexo")
                OpcaoRadio("Fêmea", femea == true) { femea = if (femea == true) null else true }
                OpcaoRadio("Macho", femea == false) { femea = if (femea == false) null else false }

                Spacer(Modifier.height(20.dp))
                BotaoLaranja("Aplicar", { onAplicar(CriteriosFiltro(status, faixas, femea)) })
                Text(
                    "Limpar filtros",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Cores.Marrom,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            status = null
                            faixas = emptySet()
                            femea = null
                        }
                        .padding(vertical = 14.dp),
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun TituloSecaoFiltro(texto: String) {
    Text(
        texto,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = Cores.Marrom,
        modifier = Modifier.padding(top = 18.dp, bottom = 6.dp),
    )
}

@Composable
private fun OpcaoRadio(texto: String, marcado: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(26.dp).border(1.5.dp, Cores.Laranja, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (marcado) Box(Modifier.size(18.dp).clip(CircleShape).background(Cores.Laranja))
        }
        Spacer(Modifier.width(10.dp))
        Text(texto, fontSize = 18.sp, color = Cores.Marrom)
    }
}

@Composable
private fun OpcaoCheck(texto: String, marcado: Boolean, onClick: () -> Unit) {
    val forma = RoundedCornerShape(6.dp)
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(forma)
                .background(if (marcado) Cores.Laranja else Color.Transparent)
                .border(1.5.dp, Cores.Laranja, forma),
        )
        Spacer(Modifier.width(10.dp))
        Text(texto, fontSize = 18.sp, color = Cores.Marrom)
    }
}
