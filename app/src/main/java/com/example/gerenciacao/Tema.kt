package com.example.gerenciacao.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gerenciacao.R

// Cores estimadas a partir dos protótipos. Ajuste com o hex exato do Figma.
object Cores {
    val Laranja = Color(0xFFFFAD31)
    val LaranjaClaro = Color(0xFFFFB454)
    val Marrom = Color(0xFF432818)
    val MarromClaro = Color(0xFF8A5A33)
    val Creme = Color(0xFFFFF8EE)
    val CremeItem = Color(0xFFFFF0DC)
    val CremeCard = Color(0xFFFFF1CC)
    val Azul = Color(0xFF8AC8FF)
    val AzulClaro = Color(0xFFB8E3FF)
    val AzulTitulo = Color(0xFF1F5A93)
    val AzulEscuro = Color(0xFF2B5282)
    val Verde = Color(0xFF98DC7D)
    val VerdeEscuro = Color(0xFF5FB06A)
    val VerdeClaro = Color(0xFFAAD17E)
    val Amarelo = Color(0xFFFFE8B5)
    val AmareloChip = Color(0xFFFFF59A)
    val AmareloBorda = Color(0xFFF2E394)
    val AmareloStatus = Color(0xFFF5E27A)
    val Vermelho = Color(0xFFFF6B6B)
    val Rosa = Color(0xFFE88F8F)
    // Telas de equipe / formulários
    val AzulCinza = Color(0xFFDDE8F0)
    val AzulCampo = Color(0xFFE6F3FF)
    val AzulMarinho = Color(0xFF1F4E8C)
    val AzulDialogo = Color(0xFFD5E8F0)
    val CinzaCampo = Color(0xFFD9D9D9)
    val CinzaTexto = Color(0xFF7A7A7A)
    val VermelhoBotao = Color(0xFFD96B6B)
    val VermelhoEscuro = Color(0xFF9B1C1C)
    val CinzaAvatar = Color(0xFFB7C9D3)
}

@Composable
fun GerenciaCaoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Cores.Laranja,
            onPrimary = Cores.Marrom,
            background = Cores.Creme,
            surface = Cores.Creme,
            onBackground = Cores.Marrom,
            onSurface = Cores.Marrom,
        ),
        content = content,
    )
}

/**
 * Base das telas internas (Início, Animais, Dados): topo laranja com avatar e engrenagem
 * e painel creme com cantos arredondados por cima, onde fica o conteúdo.
 */
@Composable
fun BaseTela(
    saudacao: (@Composable () -> Unit)? = null,
    onAvatar: () -> Unit = {},
    onConfig: () -> Unit = {},
    configSemFundo: Boolean = false,
    extra: (@Composable ColumnScope.() -> Unit)? = null,
    conteudo: @Composable BoxScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize().background(Cores.Laranja)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(onClick = onAvatar, tamanho = 48.dp)
            if (saudacao != null) {
                Spacer(Modifier.width(12.dp))
                Box(Modifier.weight(1f)) { saudacao() }
            } else {
                Spacer(Modifier.weight(1f))
            }
            IconeConfig(onClick = onConfig, comFundo = !configSemFundo)
        }
        if (extra != null) {
            Column(Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 22.dp), content = extra)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 44.dp, topEnd = 44.dp))
                .background(Cores.Creme),
            content = conteudo,
        )
    }
}

/** Faixa laranja alta (usada na Agenda): título dentro da faixa e conteúdo sobreposto por cima dela. */
@Composable
fun CabecalhoLaranja(
    titulo: String,
    altura: Dp,
    onAvatar: () -> Unit = {},
    onConfig: () -> Unit = {},
    conteudo: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(altura)
            .background(Cores.Laranja, RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp))
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(onClick = onAvatar)
            IconeConfig(onClick = onConfig)
        }
        Spacer(Modifier.weight(1f))
        Text(titulo, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)
        conteudo()
    }
}

@Composable
fun Avatar(onClick: () -> Unit = {}, tamanho: Dp = 48.dp) {
    // TODO: trocar pela foto real do usuário (Coil). Por enquanto usa a foto do protótipo.
    Image(
        painter = painterResource(R.drawable.perfil),
        contentDescription = "Perfil",
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(tamanho).clip(CircleShape).clickable(onClick = onClick),
    )
}

@Composable
fun IconeConfig(onClick: () -> Unit = {}, comFundo: Boolean = true) {
    val forma = RoundedCornerShape(16.dp)
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .then(
                if (comFundo) {
                    Modifier
                        .clip(forma)
                        .background(Color.White.copy(alpha = 0.3f))
                        .border(1.dp, Color.White.copy(alpha = 0.5f), forma)
                } else {
                    Modifier
                },
            ),
    ) {
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Configurações",
            tint = Cores.Marrom,
            modifier = Modifier.size(28.dp),
        )
    }
}

@Composable
fun BotaoLaranja(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Cores.Laranja, contentColor = Cores.Marrom),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
    ) {
        Text(texto, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CampoCreme(
    valor: String,
    aoMudar: (String) -> Unit,
    rotulo: String,
    modifier: Modifier = Modifier,
    senha: Boolean = false,
    teclado: KeyboardType = KeyboardType.Text,
) {
    TextField(
        value = valor,
        onValueChange = aoMudar,
        placeholder = { Text(rotulo, color = Cores.Marrom) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = if (senha) KeyboardType.Password else teclado),
        visualTransformation = if (senha) PasswordVisualTransformation() else VisualTransformation.None,
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Cores.Creme,
            unfocusedContainerColor = Cores.Creme,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Cores.Marrom,
            unfocusedTextColor = Cores.Marrom,
        ),
        modifier = modifier.fillMaxWidth(),
    )
}

/**
 * Barra de busca com botão de filtro. Os parâmetros permitem as variações do protótipo:
 * borda amarela (Animais/Dados), borda marrom (Parcerias) ou botão azul com sombra (Equipe).
 */
@Composable
fun BarraBusca(
    valor: String,
    aoMudar: (String) -> Unit,
    dica: String,
    onFiltro: () -> Unit,
    modifier: Modifier = Modifier,
    corBorda: Color? = Cores.AmareloBorda,
    larguraBorda: Dp = 1.5.dp,
    corBotao: Color = Cores.Marrom,
    corIconeBotao: Color = Cores.Laranja,
    sombra: Boolean = false,
) {
    val forma = RoundedCornerShape(50)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .then(if (sombra) Modifier.shadow(6.dp, forma) else Modifier)
            .clip(forma)
            .background(Cores.Creme)
            .then(if (corBorda != null) Modifier.border(larguraBorda, corBorda, forma) else Modifier)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = Cores.Marrom,
            modifier = Modifier.size(28.dp),
        )
        BasicTextField(
            value = valor,
            onValueChange = aoMudar,
            singleLine = true,
            textStyle = TextStyle(color = Cores.Marrom, fontSize = 16.sp),
            modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
            decorationBox = { campo ->
                Box {
                    if (valor.isEmpty()) Text(dica, color = Cores.Marrom, fontSize = 16.sp, maxLines = 1)
                    campo()
                }
            },
        )
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(corBotao).clickable(onClick = onFiltro),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = "Filtrar",
                tint = corIconeBotao,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/**
 * Base das telas de Equipe e Parcerias: topo AZUL com seta de voltar (e título opcional)
 * e painel creme arredondado por cima.
 */
@Composable
fun BaseTelaAzul(
    onVoltar: () -> Unit,
    titulo: String? = null,
    conteudo: @Composable BoxScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize().background(Cores.Azul)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Voltar",
                tint = Cores.Marrom,
                modifier = Modifier.size(36.dp).clickable(onClick = onVoltar),
            )
            if (titulo != null) {
                Spacer(Modifier.weight(1f))
                Text(titulo, fontSize = 36.sp, fontWeight = FontWeight.Black, color = Cores.Marrom)
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.width(36.dp)) // equilibra a seta para centralizar o título
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 44.dp, topEnd = 44.dp))
                .background(Cores.Creme),
            content = conteudo,
        )
    }
}
