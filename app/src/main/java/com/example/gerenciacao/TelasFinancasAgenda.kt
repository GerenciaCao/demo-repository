package com.example.gerenciacao.ui.telas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gerenciacao.R
import com.example.gerenciacao.ui.*
import java.text.NumberFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val localeBR = Locale.forLanguageTag("pt-BR")

// ================================================================ DADOS

private enum class TipoEstoque { GATO, CACHORRO, MEDICAMENTO }

private data class ItemEstoque(val titulo: String, val detalhe: String, val tipo: TipoEstoque)

@Composable
fun TelaFinancas(onConfig: () -> Unit = {}, onAvatar: () -> Unit = {}) {
    val meses = remember {
        listOf("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro")
    }
    var mesSelecionado by remember { mutableStateOf("Outubro") }
    var menuAberto by remember { mutableStateOf(false) }
    var busca by remember { mutableStateOf("") }

    // Dados de exemplo (TODO: vir do ViewModel, RF12 a RF14)
    val doacoes = 109.90
    val despesas = 261.80
    val saldo = 2359.98
    val estoque = remember {
        listOf(
            ItemEstoque("Ração para gatos", "03 Sacos disponíveis", TipoEstoque.GATO),
            ItemEstoque("Ração para cachorros", "03 Sacos disponíveis", TipoEstoque.CACHORRO),
            ItemEstoque("Medicamentos", "02 Unidades disponíveis", TipoEstoque.MEDICAMENTO),
        )
    }
    val moeda = remember { NumberFormat.getCurrencyInstance(localeBR) }

    BaseTela(onConfig = onConfig, onAvatar = onAvatar) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { Text("Dados", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom) }
            item {
                BarraBusca(
                    valor = busca,
                    aoMudar = { busca = it },
                    dica = "Buscar por Despesa",
                    onFiltro = { /* TODO: filtro de despesas */ },
                )
            }
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Row(
                        modifier = Modifier.clickable { menuAberto = true },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(mesSelecionado, fontSize = 20.sp, color = Cores.Marrom)
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Escolher o mês", tint = Cores.Marrom)
                    }
                    DropdownMenu(expanded = menuAberto, onDismissRequest = { menuAberto = false }) {
                        meses.forEach { mes ->
                            DropdownMenuItem(
                                text = { Text(mes) },
                                onClick = {
                                    mesSelecionado = mes
                                    menuAberto = false
                                },
                            )
                        }
                    }
                }
            }
            item {
                Row(Modifier.padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ResumoCard(moeda.format(doacoes), "Doações", Modifier.weight(1f))
                    ResumoCard(moeda.format(despesas), "Despesas", Modifier.weight(1f))
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 18.dp)
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(24.dp))
                        .background(Cores.LaranjaClaro, RoundedCornerShape(24.dp))
                        .padding(vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(moeda.format(saldo), fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Cores.Creme)
                    Text("Saldo mensal", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Cores.Creme)
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 18.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Cores.CremeCard)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text("Estoque do abrigo", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)
                    estoque.forEach { LinhaEstoque(it) }
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable { /* TODO: tela de estoque */ },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Acessar estoque", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Cores.Marrom, modifier = Modifier.size(20.dp))
                    }
                }
            }
            item {
                Text("Relatórios", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom, modifier = Modifier.padding(top = 8.dp))
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
                    BotaoRelatorio("Semanal", Modifier.weight(1f)) { /* TODO: relatório semanal (RF14) */ }
                    BotaoRelatorio("Mensal", Modifier.weight(1f)) { /* TODO: relatório mensal (RF14) */ }
                }
            }
        }
    }
}

@Composable
private fun ResumoCard(valor: String, rotulo: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Cores.LaranjaClaro)
            .clickable { /* TODO: abrir lista de ${rotulo.lowercase()} */ }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(valor, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Cores.Creme)
        Text(rotulo, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Cores.Creme)
    }
}

@Composable
private fun LinhaEstoque(item: ItemEstoque) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        when (item.tipo) {
            // TODO: trocar pelos ícones de gato e de cachorro (ainda não enviados)
            TipoEstoque.GATO, TipoEstoque.CACHORRO -> Icon(
                imageVector = Icons.Filled.Pets,
                contentDescription = null,
                tint = Cores.Marrom,
                modifier = Modifier.size(36.dp),
            )
            TipoEstoque.MEDICAMENTO -> Image(
                painter = painterResource(R.drawable.cat_medicamentos),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Cores.Marrom),
                modifier = Modifier.size(36.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(item.titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)
            Text(item.detalhe, fontSize = 16.sp, color = Cores.Marrom.copy(alpha = 0.75f))
        }
    }
}

@Composable
private fun BotaoRelatorio(texto: String, modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Cores.Marrom)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(texto, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Cores.Amarelo)
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.White)
    }
}

// ================================================================ AGENDA

/** Tarefa/evento da agenda. [cor] é a cor escolhida em "Nova Tarefa". */
data class Evento(
    val data: LocalDate,
    val titulo: String,
    val animal: String,
    val horario: String,
    val cor: Color = Color(0xFF93CBF5),
)

@Composable
fun TelaAgenda(onConfig: () -> Unit = {}, onAvatar: () -> Unit = {}) {
    var mes by remember { mutableStateOf(YearMonth.of(2026, 10)) } // TODO: YearMonth.now()
    var eventos by remember {
        mutableStateOf(
            listOf(
                Evento(LocalDate.of(2026, 10, 5), "Consulta Veterinária", "Mel", "10h30"),
                Evento(LocalDate.of(2026, 10, 21), "Visita de Adoção", "Scooby", "14h30"),
                Evento(LocalDate.of(2026, 10, 23), "Consulta Veterinária", "Bobby", "14h30"),
            ),
        )
    }
    var dataNovaTarefa by remember { mutableStateOf<LocalDate?>(null) }

    // Dados de exemplo para os seletores (TODO: vir da API)
    val animais = remember { listOf("Gabi", "Bobby", "Oreo", "Mel", "Scooby") }
    val responsaveis = EquipeMock.membros.map { it.nomeCompleto }

    val eventosDoMes = eventos
        .filter { YearMonth.from(it.data) == mes }
        .sortedBy { it.data }

    Box(Modifier.fillMaxSize()) {
        // O espaço no fim da faixa laranja deixa o título "Agenda" acima do calendário, que invade a faixa
        CabecalhoLaranja(titulo = "Agenda", altura = 300.dp, onAvatar = onAvatar, onConfig = onConfig) {
            Spacer(Modifier.height(64.dp))
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 236.dp, start = 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                CardCalendario(
                    mes = mes,
                    onAnterior = { mes = mes.minusMonths(1) },
                    onProximo = { mes = mes.plusMonths(1) },
                    aoTocarDia = { dia -> dataNovaTarefa = mes.atDay(dia) }, // abre "Nova Tarefa" (RF15)
                )
            }
            item {
                Text(
                    "Eventos do mês",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Cores.Marrom,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                )
            }
            items(eventosDoMes) { evento ->
                val diaSemana = evento.data.dayOfWeek.getDisplayName(TextStyle.FULL, localeBR)
                    .replaceFirstChar { it.uppercase() }
                    .replace("-feira", "-Feira")
                CardEvento(evento, diaSemana)
            }
        }

        dataNovaTarefa?.let { data ->
            NovaTarefaSheet(
                dataInicial = data,
                animais = animais,
                responsaveis = responsaveis,
                onSalvar = { t ->
                    // TODO: chamar a API para salvar o evento (RF15) e agendar o lembrete
                    eventos = eventos + Evento(
                        data = t.data,
                        titulo = t.titulo,
                        animal = t.animal ?: "—",
                        horario = "%dh%02d".format(t.hora.hour, t.hora.minute),
                        cor = t.cor,
                    )
                    mes = YearMonth.from(t.data)
                    dataNovaTarefa = null
                },
                onFechar = { dataNovaTarefa = null },
            )
        }
    }
}

@Composable
private fun CardCalendario(mes: YearMonth, onAnterior: () -> Unit, onProximo: () -> Unit, aoTocarDia: (Int) -> Unit) {
    val nomeMes = mes.month.getDisplayName(TextStyle.FULL, localeBR).replaceFirstChar { it.uppercase() }
    val deslocamento = mes.atDay(1).dayOfWeek.let { if (it == DayOfWeek.SUNDAY) 0 else it.value } // domingo = coluna 0
    val celulas: List<Int?> = List(deslocamento) { null } + (1..mes.lengthOfMonth()).toList()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Cores.AzulClaro, RoundedCornerShape(24.dp))
            .padding(16.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = "Mês anterior", tint = Cores.Marrom, modifier = Modifier.clickable(onClick = onAnterior).size(32.dp))
            Text("$nomeMes, ${mes.year}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)
            Icon(Icons.Filled.ChevronRight, contentDescription = "Próximo mês", tint = Cores.Marrom, modifier = Modifier.clickable(onClick = onProximo).size(32.dp))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth()) {
            listOf("D", "S", "T", "Q", "Q", "S", "S").forEach {
                Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)
            }
        }
        Spacer(Modifier.height(8.dp))
        celulas.chunked(7).forEach { semana ->
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                for (i in 0 until 7) {
                    val dia = semana.getOrNull(i)
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        if (dia != null) {
                            Box(
                                Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Cores.Azul)
                                    .clickable { aoTocarDia(dia) },
                                contentAlignment = Alignment.Center,
                            ) { Text("%02d".format(dia), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Cores.Marrom) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardEvento(e: Evento, diaSemana: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Cores.Amarelo)
            .clickable { /* TODO: detalhes do evento */ },
    ) {
        Column(
            Modifier
                .padding(8.dp)
                .width(108.dp)
                .fillMaxHeight()
                .background(e.cor, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text("%02d".format(e.data.dayOfMonth), fontSize = 24.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)
            Text(diaSemana, fontSize = 12.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, color = Cores.Marrom)
        }
        Column(Modifier.padding(start = 132.dp, top = 12.dp)) {
            Text(e.titulo, fontSize = 14.sp, lineHeight = 17.sp, color = Cores.Marrom)
            Text("Animal: ${e.animal}", fontSize = 14.sp, lineHeight = 17.sp, color = Cores.Marrom)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Cores.Marrom,
            modifier = Modifier.align(Alignment.TopEnd).padding(10.dp),
        )
        Text(
            "Horário: ${e.horario}",
            fontSize = 11.sp,
            color = Cores.Marrom,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
                .background(Cores.Verde, RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 3.dp),
        )
    }
}
