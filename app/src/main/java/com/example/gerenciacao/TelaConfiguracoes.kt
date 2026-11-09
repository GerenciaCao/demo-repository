package com.example.gerenciacao.ui.telas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gerenciacao.R
import com.example.gerenciacao.ui.*

private data class ItemConfig(
    val titulo: String,
    val icone: ImageVector?, // null = usa o ícone da casa com coração (drawable)
    val aoClicar: () -> Unit = {},
)

@Composable
fun TelaConfiguracoes(
    onVoltar: () -> Unit,
    onEquipe: () -> Unit = {},
    onParcerias: () -> Unit = {},
    onMinhaConta: () -> Unit = {},
    onAbrigo: () -> Unit = {},
) {
    val contas = listOf(
        ItemConfig("Minha Conta", Icons.Filled.Person, onMinhaConta),
        ItemConfig("Notificações", Icons.Filled.Notifications),
        ItemConfig("Segurança e Privacidade", Icons.Filled.Security),
        ItemConfig("Informações do abrigo", null, onAbrigo),
    )
    val outros = listOf(
        ItemConfig("Aparência e Acessibilidade", Icons.Filled.Palette),
        ItemConfig("Sobre", Icons.Filled.Info),
        ItemConfig("Ajuda", Icons.AutoMirrored.Filled.Help),
    )

    Column(Modifier.fillMaxSize().background(Cores.Laranja)) {
        Column(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 16.dp, top = 8.dp, bottom = 16.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Voltar",
                tint = Cores.Marrom,
                modifier = Modifier.size(32.dp).clickable(onClick = onVoltar),
            )
            Text(
                "Configurações",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = Cores.Marrom,
                modifier = Modifier.padding(start = 44.dp),
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 44.dp, topEnd = 44.dp))
                .background(Cores.Creme),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        Box(
                            Modifier
                                .weight(1f)
                                .height(110.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Cores.AzulClaro)
                                .clickable(onClick = onParcerias),
                        ) {
                            Text(
                                "Serviços e\nProdutos",
                                fontSize = 17.sp,
                                lineHeight = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Cores.Marrom,
                                modifier = Modifier.padding(start = 10.dp, top = 12.dp),
                            )
                            // TODO: trocar pela imagem do saco de ração (ainda não enviada)
                            Icon(
                                imageVector = Icons.Filled.ShoppingBag,
                                contentDescription = null,
                                tint = Cores.Marrom.copy(alpha = 0.5f),
                                modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp).size(52.dp),
                            )
                        }
                        Box(
                            Modifier
                                .weight(1f)
                                .height(110.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Cores.AzulClaro)
                                .clickable(onClick = onEquipe),
                        ) {
                            Text(
                                "Equipe",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Cores.Marrom,
                                modifier = Modifier.padding(start = 10.dp, top = 12.dp),
                            )
                        }
                    }
                }
                item { RotuloSecao("CONTAS E PREFERÊNCIAS") }
                items(contas.size) { i -> LinhaConfig(contas[i]) }
                item { RotuloSecao("OUTROS") }
                items(outros.size) { i -> LinhaConfig(outros[i]) }
            }
        }
    }
}

@Composable
private fun RotuloSecao(texto: String) {
    Text(
        texto,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Cores.Marrom,
        modifier = Modifier.padding(top = 16.dp, bottom = 2.dp),
    )
}

@Composable
private fun LinhaConfig(item: ItemConfig) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Cores.CremeItem)
            .clickable(onClick = item.aoClicar)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (item.icone != null) {
            Icon(imageVector = item.icone, contentDescription = null, tint = Cores.MarromClaro, modifier = Modifier.size(26.dp))
        } else {
            Image(
                painter = painterResource(R.drawable.nav_inicio),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Cores.MarromClaro),
                modifier = Modifier.size(26.dp),
            )
        }
        Spacer(Modifier.width(14.dp))
        Text(item.titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom, modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Cores.MarromClaro,
        )
    }
}
