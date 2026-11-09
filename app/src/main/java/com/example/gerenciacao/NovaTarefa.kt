package com.example.gerenciacao.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gerenciacao.ui.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

/** Cores que uma tarefa pode ter na Agenda. */
val coresTarefa = listOf(Color(0xFF93CBF5), Cores.Verde, Cores.Rosa)

data class NovaTarefa(
    val titulo: String,
    val data: LocalDate,
    val hora: LocalTime,
    val animal: String?,
    val responsavel: String?,
    val cor: Color,
    val descricao: String,
    val lembrete: String,
)

private val opcoesLembrete = listOf("Sem lembrete", "No horário", "15 minutos antes", "1 hora antes", "1 dia antes")

/**
 * Painel "Nova Tarefa" (sobe por cima da Agenda).
 * [dataInicial] vem do dia tocado no calendário. A data/hora são escolhidas em 4 "roletas":
 * arraste para cima/baixo ou toque no número de cima/baixo.
 */
@Composable
fun NovaTarefaSheet(
    dataInicial: LocalDate,
    animais: List<String>,
    responsaveis: List<String>,
    onSalvar: (NovaTarefa) -> Unit,
    onFechar: () -> Unit,
) {
    val agora = remember { LocalTime.now() }
    var titulo by remember { mutableStateOf("") }
    var dia by remember { mutableStateOf(dataInicial.dayOfMonth) }
    var mes by remember { mutableStateOf(dataInicial.monthValue) }
    var hora by remember { mutableStateOf(agora.hour) }
    var minuto by remember { mutableStateOf(agora.minute) }
    var animal by remember { mutableStateOf<String?>(null) }
    var responsavel by remember { mutableStateOf<String?>(null) }
    var corEscolhida by remember { mutableStateOf(0) }
    var descricao by remember { mutableStateOf("") }
    var lembrete by remember { mutableStateOf<String?>(opcoesLembrete.first()) }
    var tentouSalvar by remember { mutableStateOf(false) }

    val ano = dataInicial.year
    val diasNoMes = YearMonth.of(ano, mes).lengthOfMonth()
    val diaAjustado = dia.coerceAtMost(diasNoMes)

    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0x99000000))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onFechar),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(topStart = 44.dp, topEnd = 44.dp))
                .background(Cores.Creme)
                .pointerInput(Unit) { detectTapGestures { } },
        ) {
            Box(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Cores.CinzaCampo),
            )
            Text(
                "Nova Tarefa",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
            )
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            ) {
                CampoFormulario(titulo, { titulo = it }, "Título", fundo = Cores.CinzaCampo)
                if (tentouSalvar && titulo.isBlank()) {
                    Text("Dê um título à tarefa.", fontSize = 13.sp, color = Cores.VermelhoBotao, modifier = Modifier.padding(top = 4.dp))
                }

                Text("Data e horário", fontSize = 15.sp, color = Color.Black, modifier = Modifier.padding(top = 18.dp, bottom = 6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Rolo("Dia", 1, diasNoMes, diaAjustado, { dia = it }, Modifier.weight(1f))
                    Rolo("Mês", 1, 12, mes, { mes = it }, Modifier.weight(1f))
                    Rolo("Horas", 0, 23, hora, { hora = it }, Modifier.weight(1f))
                    Rolo("Minutos", 0, 59, minuto, { minuto = it }, Modifier.weight(1f))
                }

                Spacer(Modifier.height(18.dp))
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Cores.CinzaCampo),
                ) {
                    SeletorSimples(animal ?: "Animal", animais) { animal = it }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(Color.Black))
                    SeletorSimples(responsavel ?: "Responsável", responsaveis) { responsavel = it }
                }

                Text("Cor", fontSize = 15.sp, color = Color.Black, modifier = Modifier.padding(top = 18.dp, bottom = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    coresTarefa.forEachIndexed { i, cor ->
                        Box(
                            Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(cor)
                                .then(if (i == corEscolhida) Modifier.border(2.dp, Cores.Marrom, CircleShape) else Modifier)
                                .clickable { corEscolhida = i },
                        )
                    }
                }

                Text("Descrição", fontSize = 15.sp, color = Color.Black, modifier = Modifier.padding(top = 18.dp, bottom = 6.dp))
                CampoFormulario(descricao, { descricao = it }, "", fundo = Cores.CinzaCampo, linhas = 5)

                Text("Lembrete", fontSize = 15.sp, color = Color.Black, modifier = Modifier.padding(top = 18.dp, bottom = 6.dp))
                SeletorLista(
                    opcoes = opcoesLembrete,
                    selecionado = lembrete,
                    rotulo = { it },
                    aoSelecionar = { lembrete = it },
                    dica = "Sem lembrete",
                    fundo = Cores.CinzaCampo,
                    sombra = false,
                )

                Spacer(Modifier.height(22.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    BotaoAcao("Cancelar", onFechar, Modifier.weight(1f))
                    BotaoAcao(
                        "Salvar",
                        onClick = {
                            tentouSalvar = true
                            if (titulo.isNotBlank()) {
                                onSalvar(
                                    NovaTarefa(
                                        titulo = titulo.trim(),
                                        data = LocalDate.of(ano, mes, diaAjustado),
                                        hora = LocalTime.of(hora, minuto),
                                        animal = animal,
                                        responsavel = responsavel,
                                        cor = coresTarefa[corEscolhida],
                                        descricao = descricao.trim(),
                                        lembrete = lembrete ?: opcoesLembrete.first(),
                                    ),
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.navigationBarsPadding().height(24.dp))
            }
        }
    }
}

/** Linha de seleção dentro do bloco cinza "Animal / Responsável". */
@Composable
private fun SeletorSimples(texto: String, opcoes: List<String>, aoSelecionar: (String) -> Unit) {
    var aberto by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth()) {
        Text(
            texto,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth().clickable { aberto = true }.padding(horizontal = 12.dp, vertical = 12.dp),
        )
        androidx.compose.material3.DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
            opcoes.forEach { opcao ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(opcao) },
                    onClick = {
                        aoSelecionar(opcao)
                        aberto = false
                    },
                )
            }
        }
    }
}

/**
 * Roleta de números: mostra o valor anterior, o atual (em destaque) e o próximo.
 * Arrastar para cima avança, para baixo volta; ao passar do limite, dá a volta.
 */
@Composable
private fun Rolo(
    rotulo: String,
    minimo: Int,
    maximo: Int,
    valor: Int,
    aoMudar: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val total = maximo - minimo + 1
    val envolver = { v: Int -> minimo + (((v - minimo) % total) + total) % total }
    val passoPx = with(LocalDensity.current) { 28.dp.toPx() }
    val valorAtual by rememberUpdatedState(valor)
    val aoMudarAtual by rememberUpdatedState(aoMudar)

    Column(
        modifier = modifier.pointerInput(minimo, maximo) {
            var acumulado = 0f
            detectVerticalDragGestures(onDragStart = { acumulado = 0f }) { change, dy ->
                change.consume()
                acumulado += dy
                var atual = valorAtual
                while (acumulado <= -passoPx) { atual = envolver(atual + 1); acumulado += passoPx }
                while (acumulado >= passoPx) { atual = envolver(atual - 1); acumulado -= passoPx }
                if (atual != valorAtual) aoMudarAtual(atual)
            }
        },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(rotulo, fontSize = 16.sp, color = Color.Black)
        Spacer(Modifier.height(4.dp))
        Text(
            "%02d".format(envolver(valor - 1)),
            fontSize = 13.sp,
            color = Cores.CinzaTexto,
            modifier = Modifier.clickable { aoMudar(envolver(valor - 1)) }.padding(vertical = 2.dp),
        )
        Text("%02d".format(valor), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(
            "%02d".format(envolver(valor + 1)),
            fontSize = 13.sp,
            color = Cores.CinzaTexto,
            modifier = Modifier.clickable { aoMudar(envolver(valor + 1)) }.padding(vertical = 2.dp),
        )
    }
}
