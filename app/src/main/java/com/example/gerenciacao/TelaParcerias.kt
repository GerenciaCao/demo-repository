package com.example.gerenciacao.ui.telas

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gerenciacao.R
import com.example.gerenciacao.ui.*
import java.text.NumberFormat
import java.util.Locale

private enum class AbaParceria(val rotulo: String) { TODOS("Todos"), PRODUTOS("Produtos"), SERVICOS("Serviços") }

private data class ItemParceria(
    val titulo: String,
    val preco: Double,
    @DrawableRes val imagem: Int? = null, // TODO: fotos dos produtos/serviços (vão vir da API)
)

@Composable
fun TelaParcerias(onVoltar: () -> Unit) {
    var busca by remember { mutableStateOf("") }
    var aba by remember { mutableStateOf(AbaParceria.TODOS) }
    val moeda = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")) }

    // Dados de EXEMPLO (TODO: vir da API do módulo Parcerias, RF19 a RF22)
    val produtos = remember {
        listOf(
            ItemParceria("Ração Úmida Whiskas Sachê Gatos Adultos", 5.99, R.drawable.produto_whiskas),
            ItemParceria("Ração Úmida Pedigree Sachê Raças Pequenas", 4.99, R.drawable.produto_pedigree),
        )
    }
    val servicos = remember {
        listOf(
            ItemParceria("Banho e Tosa da Vila", 79.99, R.drawable.servico_banho),
            ItemParceria("Spa PetDog, onde seu cão relaxa", 79.99, R.drawable.servico_spa),
        )
    }
    val produtosFiltrados = produtos.filter { it.titulo.contains(busca, ignoreCase = true) }
    val servicosFiltrados = servicos.filter { it.titulo.contains(busca, ignoreCase = true) }

    BaseTelaAzul(onVoltar = onVoltar, titulo = "Parcerias") {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                BarraBusca(
                    valor = busca,
                    aoMudar = { busca = it },
                    dica = "Buscar por Produto, Serviço, etc",
                    onFiltro = { /* TODO: filtros por preço, marca e relevância (RF22) */ },
                    corBorda = Cores.Marrom,
                    larguraBorda = 2.dp,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
            item {
                Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    AbaParceria.values().forEach { a ->
                        Text(
                            a.rotulo,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            color = if (a == aba) Cores.Marrom else Color(0xFFC28A5A),
                            modifier = Modifier.clickable { aba = a },
                        )
                    }
                }
            }
            item { BannerDestaque(moeda.format(69.90)) }

            if (aba != AbaParceria.SERVICOS && produtosFiltrados.isNotEmpty()) {
                item { TituloSecaoParceria("Produtos Friday: Ofertas da semana") }
                item { LinhaCards(produtosFiltrados, altoImagem = 150.dp, moeda = moeda, ajuste = ContentScale.Fit, fundoImagem = Color.White) }
            }
            if (aba != AbaParceria.PRODUTOS && servicosFiltrados.isNotEmpty()) {
                item { TituloSecaoParceria("Serviços", espacoExtra = true) }
                item { LinhaCards(servicosFiltrados, altoImagem = 110.dp, moeda = moeda) }
            }
        }
    }
}

@Composable
private fun TituloSecaoParceria(texto: String, espacoExtra: Boolean = false) {
    Text(
        texto,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Cores.Marrom,
        modifier = Modifier.padding(start = 20.dp, top = if (espacoExtra) 10.dp else 0.dp),
    )
}

@Composable
private fun BannerDestaque(preco: String) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { /* TODO: abrir a oferta do parceiro */ },
    ) {
        Image(
            painter = painterResource(R.drawable.banner_premium),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            alignment = Alignment.CenterStart,
            modifier = Modifier.align(Alignment.CenterStart).fillMaxHeight().padding(start = 4.dp),
        )
        // Degradê laranja na borda direita, como no protótipo
        Box(
            Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(50.dp)
                .background(Brush.horizontalGradient(listOf(Color.Transparent, Cores.Laranja))),
        )
        Column(
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(preco.replace("\u00A0", ""), fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = Cores.Marrom)
            Text("Ração\nPremium", fontSize = 28.sp, lineHeight = 32.sp, textAlign = TextAlign.End, color = Cores.Marrom)
        }
    }
}

@Composable
private fun LinhaCards(
    itens: List<ItemParceria>,
    altoImagem: androidx.compose.ui.unit.Dp,
    moeda: NumberFormat,
    ajuste: ContentScale = ContentScale.Crop,
    fundoImagem: Color = Cores.AzulCampo,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(itens) { item ->
            Column(
                modifier = Modifier
                    .width(176.dp)
                    .shadow(6.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .clickable { /* TODO: abrir o link do parceiro (Intent ACTION_VIEW) */ },
            ) {
                Box(Modifier.fillMaxWidth().height(altoImagem).background(fundoImagem), contentAlignment = Alignment.Center) {
                    if (item.imagem != null) {
                        Image(painterResource(item.imagem), contentDescription = null, contentScale = ajuste, modifier = Modifier.fillMaxSize().padding(if (ajuste == ContentScale.Fit) 8.dp else 0.dp))
                    } else {
                        Icon(Icons.Filled.Pets, contentDescription = null, tint = Cores.Azul, modifier = Modifier.size(48.dp))
                    }
                }
                Column(Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                    Text(item.titulo, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.Black)
                    Text(moeda.format(item.preco), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}
