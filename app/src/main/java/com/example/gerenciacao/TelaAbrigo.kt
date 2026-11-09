package com.example.gerenciacao.ui.telas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gerenciacao.R
import com.example.gerenciacao.ui.*

private val Dourado = Color(0xFFE6A03A)
private val Pessego = Color(0xFFFFC983)

/**
 * Informações do abrigo: foto no topo, painel creme com nome, localização, história,
 * números (animais, capacidade, vagas), contato e equipe.
 */
@Composable
fun TelaAbrigo(onVoltar: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Cores.Creme)) {
        Image(
            painter = painterResource(R.drawable.abrigo_foto),
            contentDescription = "Foto do abrigo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(310.dp),
        )

        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(284.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 700.dp)
                    .clip(RoundedCornerShape(topStart = 44.dp, topEnd = 44.dp))
                    .background(Cores.Creme)
                    .padding(horizontal = 20.dp)
                    .padding(top = 24.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp),
            ) {
                Text(AbrigoMock.nome, fontSize = 28.sp, fontWeight = FontWeight.Black, color = Cores.Marrom)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Cores.Marrom, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(AbrigoMock.endereco, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Dourado)
                    Spacer(Modifier.width(14.dp))
                    Box(Modifier.size(9.dp).clip(CircleShape).background(Cores.Marrom))
                    Spacer(Modifier.width(6.dp))
                    Text(AbrigoMock.distancia, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Dourado)
                }

                Text(
                    AbrigoMock.descricao,
                    fontSize = 15.sp,
                    lineHeight = 25.sp,
                    textAlign = TextAlign.Justify,
                    color = Cores.Marrom,
                    modifier = Modifier.padding(top = 18.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 22.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    CartaoNumero(AbrigoMock.animais, "Animais Totais", Modifier.weight(1f))
                    CartaoNumero(AbrigoMock.capacidade, "Capacidade", Modifier.weight(1f))
                    CartaoNumero(AbrigoMock.vagas, "Disponíveis", Modifier.weight(1f))
                }

                TituloBlocoAbrigo("Contato")
                LinhaContato(Icons.Filled.Email, AbrigoMock.email)
                LinhaContato(Icons.Filled.Phone, AbrigoMock.telefone)
                LinhaContato(Icons.Outlined.LocationOn, AbrigoMock.enderecoCompleto)

                TituloBlocoAbrigo("Equipe")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(EquipeMock.membros, key = { it.id }) { membro ->
                        Column(Modifier.width(84.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            AvatarMembro(membro.foto, tamanho = 76.dp)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                membro.nomeCompleto,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Cores.Marrom,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }

        // O protótipo não mostra seta de voltar sobre a foto; este botão é necessário para navegar.
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(12.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Cores.Creme.copy(alpha = 0.85f))
                .clickable(onClick = onVoltar),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Voltar", tint = Cores.Marrom, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun CartaoNumero(valor: Int, rotulo: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .background(Pessego, RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("$valor", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
        Text(rotulo, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom, maxLines = 1)
    }
}

@Composable
private fun TituloBlocoAbrigo(texto: String) {
    Text(
        texto,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Cores.Marrom,
        modifier = Modifier.padding(top = 26.dp, bottom = 12.dp),
    )
}

@Composable
private fun LinhaContato(icone: ImageVector, texto: String) {
    Row(Modifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icone, contentDescription = null, tint = Cores.Marrom, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(14.dp))
        Text(texto, fontSize = 16.sp, color = Cores.Marrom)
    }
}
