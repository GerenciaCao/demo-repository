package com.example.gerenciacao.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gerenciacao.ui.BotaoAcao
import com.example.gerenciacao.ui.Cores

/** Indicador de "carregando" centralizado. */
@Composable
fun EstadoCarregando(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = Cores.Laranja)
    }
}

/** Mensagem de erro com botão "Tentar novamente". */
@Composable
fun EstadoErro(mensagem: String, onTentarNovamente: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(mensagem, fontSize = 16.sp, color = Cores.Marrom, textAlign = TextAlign.Center)
        BotaoAcao("Tentar novamente", onTentarNovamente)
    }
}

/** Texto de erro de formulário (vermelho escuro, legível sobre fundo claro e azul). */
@Composable
fun MensagemErro(texto: String, modifier: Modifier = Modifier) {
    Text(texto, fontSize = 14.sp, color = Cores.VermelhoEscuro, modifier = modifier.padding(top = 8.dp))
}
