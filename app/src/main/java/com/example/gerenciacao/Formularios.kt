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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Rótulo pequeno acima de um campo de formulário. */
@Composable
fun RotuloCampo(texto: String, tamanho: Int = 16) {
    Text(
        texto,
        fontSize = tamanho.sp,
        color = Cores.Marrom,
        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
    )
}

/** Campo de texto de formulário (fundo colorido, borda e sombra opcionais). */
@Composable
fun CampoFormulario(
    valor: String,
    aoMudar: (String) -> Unit,
    dica: String = "",
    modifier: Modifier = Modifier,
    fundo: Color = Cores.AzulCinza,
    borda: Color? = null,
    sombra: Boolean = false,
    teclado: KeyboardType = KeyboardType.Text,
    linhas: Int = 1,
) {
    val forma = RoundedCornerShape(10.dp)
    BasicTextField(
        value = valor,
        onValueChange = aoMudar,
        singleLine = linhas == 1,
        minLines = linhas,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        textStyle = TextStyle(color = Cores.Marrom, fontSize = 16.sp),
        modifier = modifier
            .fillMaxWidth()
            .then(if (sombra) Modifier.shadow(4.dp, forma) else Modifier)
            .background(fundo, forma)
            .then(if (borda != null) Modifier.border(1.dp, borda, forma) else Modifier),
        decorationBox = { campo ->
            Box(
                modifier = Modifier.heightIn(min = if (linhas == 1) 48.dp else 100.dp).padding(horizontal = 14.dp, vertical = 12.dp),
                contentAlignment = if (linhas == 1) Alignment.CenterStart else Alignment.TopStart,
            ) {
                if (valor.isEmpty()) Text(dica, color = Cores.CinzaTexto, fontSize = 14.sp)
                campo()
            }
        },
    )
}

/** Campo somente leitura (tela de perfil do membro). */
@Composable
fun CampoLeitura(rotulo: String, valor: String) {
    val forma = RoundedCornerShape(10.dp)
    Column {
        Text(rotulo, fontSize = 14.sp, color = Cores.Marrom, modifier = Modifier.padding(bottom = 4.dp))
        Text(
            valor.ifBlank { "—" },
            fontSize = 16.sp,
            color = Cores.Marrom,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, forma)
                .background(Cores.AzulCinza, forma)
                .padding(horizontal = 14.dp, vertical = 14.dp),
        )
    }
}

/** Lista suspensa (Cargo, Animal, Responsável, Lembrete...). */
@Composable
fun <T> SeletorLista(
    opcoes: List<T>,
    selecionado: T?,
    rotulo: (T) -> String,
    aoSelecionar: (T) -> Unit,
    dica: String,
    modifier: Modifier = Modifier,
    fundo: Color = Cores.AzulCinza,
    borda: Color? = null,
    sombra: Boolean = true,
) {
    var aberto by remember { mutableStateOf(false) }
    val forma = RoundedCornerShape(10.dp)
    Box(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (sombra) Modifier.shadow(4.dp, forma) else Modifier)
                .background(fundo, forma)
                .then(if (borda != null) Modifier.border(1.dp, borda, forma) else Modifier)
                .clip(forma)
                .clickable { aberto = true }
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selecionado?.let(rotulo) ?: dica,
                fontSize = 14.sp,
                fontWeight = if (selecionado != null) FontWeight.Bold else FontWeight.Normal,
                color = if (selecionado != null) Cores.Marrom else Cores.CinzaTexto,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = Cores.Marrom)
        }
        DropdownMenu(
            expanded = aberto,
            onDismissRequest = { aberto = false },
            modifier = Modifier.background(fundo),
        ) {
            opcoes.forEach { opcao ->
                DropdownMenuItem(
                    text = { Text(rotulo(opcao), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom) },
                    onClick = {
                        aoSelecionar(opcao)
                        aberto = false
                    },
                )
            }
        }
    }
}

/** Linha de permissão com caixa de seleção azul-marinho. */
@Composable
fun LinhaPermissao(texto: String, marcado: Boolean, aoAlternar: () -> Unit) {
    val forma = RoundedCornerShape(6.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Cores.AzulCinza)
            .clickable(onClick = aoAlternar)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .border(1.5.dp, Cores.AzulMarinho, forma)
                .padding(3.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(if (marcado) Cores.AzulMarinho else Color.Transparent),
        )
        Spacer(Modifier.width(14.dp))
        Text(texto, fontSize = 18.sp, color = Cores.Marrom)
    }
}

/** Botão de ação laranja com texto creme (Cancelar, Salvar, Editar Perfil...). */
@Composable
fun BotaoAcao(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icone: ImageVector? = null,
    corFundo: Color = Cores.Laranja,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = corFundo, contentColor = Cores.Creme),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
    ) {
        Text(texto, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        if (icone != null) {
            Spacer(Modifier.width(8.dp))
            Icon(icone, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}

/** Foto provisória de membro (círculo cinza-azulado com silhueta). TODO: foto real via Coil. */
@Composable
fun AvatarPlaceholder(tamanho: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(tamanho).clip(CircleShape).background(Cores.CinzaAvatar),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFF6F8FA0), modifier = Modifier.size(tamanho * 0.65f))
    }
}

/** Foto do membro (círculo). Sem foto, mostra a silhueta provisória. */
@Composable
fun AvatarMembro(foto: Int?, tamanho: Dp, modifier: Modifier = Modifier) {
    if (foto != null) {
        Image(
            painter = painterResource(foto),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.size(tamanho).clip(CircleShape),
        )
    } else {
        AvatarPlaceholder(tamanho, modifier)
    }
}
