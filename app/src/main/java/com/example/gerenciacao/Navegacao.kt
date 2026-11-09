package com.example.gerenciacao.navegacao

import android.R.attr.type
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gerenciacao.R
import com.example.gerenciacao.ui.Cores
import com.example.gerenciacao.ui.telas.*

object Rotas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val CADASTRO = "cadastro"
    const val INICIO = "inicio"
    const val ANIMAIS = "animais"
    const val FINANCAS = "financas"
    const val AGENDA = "agenda"
    const val CONFIG = "configuracoes"
    const val EQUIPE = "equipe"
    const val ABRIGO = "abrigo"
    const val CONEXOES = "conexoes"
    const val FICHA = "ficha/{id}"
    const val PARCERIAS = "parcerias"
    const val ADICIONAR_MEMBRO = "adicionar_membro"
    const val PERFIL_MEMBRO = "perfil_membro/{id}"
    const val EDITAR_MEMBRO = "editar_membro/{id}"

    fun ficha(id: Int) = "ficha/$id"
    fun perfilMembro(id: Int) = "perfil_membro/$id"
    fun editarMembro(id: Int) = "editar_membro/$id"
}

private data class ItemBarra(val rota: String, val rotulo: String, @DrawableRes val icone: Int)

private val itensBarra = listOf(
    ItemBarra(Rotas.INICIO, "Início", R.drawable.nav_inicio),
    ItemBarra(Rotas.ANIMAIS, "Animais", R.drawable.nav_animais),
    ItemBarra(Rotas.FINANCAS, "Dados", R.drawable.nav_dados),
    ItemBarra(Rotas.AGENDA, "Agenda", R.drawable.nav_agenda),
)

private fun NavController.irParaAba(rota: String) = navigate(rota) {
    popUpTo(Rotas.INICIO) { saveState = true }
    launchSingleTop = true
    restoreState = true
}

@Composable
fun GerenciaCaoApp() {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val rotaAtual = entrada?.destination?.route
    // Na Ficha Individual a barra continua visível, com a aba Animais marcada
    val abaAtual = if (rotaAtual == Rotas.FICHA) Rotas.ANIMAIS else rotaAtual
    val irConfig: () -> Unit = { nav.navigate(Rotas.CONFIG) { launchSingleTop = true } }
    val irPerfil: () -> Unit = { nav.navigate(Rotas.perfilMembro(EquipeMock.usuarioAtualId)) { launchSingleTop = true } }
    val irParcerias: () -> Unit = { nav.navigate(Rotas.PARCERIAS) { launchSingleTop = true } }

    Scaffold(
        containerColor = Cores.Creme,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (itensBarra.any { it.rota == abaAtual }) {
                BarraInferior(
                    rotaAtual = abaAtual,
                    onSelecionar = { rota ->
                        if (rotaAtual == Rotas.FICHA && rota == Rotas.ANIMAIS) nav.popBackStack() else nav.irParaAba(rota)
                    },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Rotas.SPLASH,
            modifier = Modifier.padding(padding),
        ) {
            composable(Rotas.SPLASH) {
                TelaSplash(
                    onConcluir = {
                        nav.navigate(Rotas.LOGIN) { popUpTo(Rotas.SPLASH) { inclusive = true } }
                    },
                )
            }
            composable(Rotas.LOGIN) {
                TelaLogin(
                    onAcessar = {
                        nav.navigate(Rotas.INICIO) { popUpTo(Rotas.LOGIN) { inclusive = true } }
                    },
                    onCadastrar = { nav.navigate(Rotas.CADASTRO) },
                    onEsqueceuSenha = { /* TODO: RF03 recuperação de senha */ },
                )
            }
            composable(Rotas.CADASTRO) {
                TelaCadastro(
                    onConfirmar = {
                        nav.navigate(Rotas.INICIO) { popUpTo(Rotas.LOGIN) { inclusive = true } }
                    },
                    onIrParaLogin = { nav.popBackStack() },
                )
            }
            composable(Rotas.INICIO) {
                TelaInicio(
                    onVerAgenda = { nav.irParaAba(Rotas.AGENDA) },
                    onVerDados = { nav.irParaAba(Rotas.FINANCAS) },
                    onConfig = irConfig,
                    onAvatar = irPerfil,
                    onParcerias = irParcerias,
                    onConexoes = { nav.navigate(Rotas.CONEXOES) },
                )
            }
            composable(Rotas.ANIMAIS) {
                TelaAnimais(
                    onConfig = irConfig,
                    onAvatar = irPerfil,
                    onAbrirFicha = { id -> nav.navigate(Rotas.ficha(id)) },
                )
            }
            composable(
                route = Rotas.FICHA,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { backEntry ->
                TelaFicha(id = backEntry.arguments?.getInt("id") ?: 0, onVoltar = { nav.popBackStack() })
            }
            composable(Rotas.CONEXOES) { TelaConexao(onVoltar = { nav.popBackStack() }) }
            composable(Rotas.FINANCAS) { TelaFinancas(onConfig = irConfig, onAvatar = irPerfil) }
            composable(Rotas.AGENDA) { TelaAgenda(onConfig = irConfig, onAvatar = irPerfil) }
            composable(Rotas.CONFIG) {
                TelaConfiguracoes(
                    onVoltar = { nav.popBackStack() },
                    onEquipe = { nav.navigate(Rotas.EQUIPE) },
                    onParcerias = irParcerias,
                    onMinhaConta = irPerfil,
                    onAbrigo = { nav.navigate(Rotas.ABRIGO) },
                )
            }
            composable(Rotas.ABRIGO) { TelaAbrigo(onVoltar = { nav.popBackStack() }) }
            composable(Rotas.PARCERIAS) { TelaParcerias(onVoltar = { nav.popBackStack() }) }
            composable(Rotas.EQUIPE) {
                TelaEquipe(
                    onVoltar = { nav.popBackStack() },
                    onAbrirMembro = { id -> nav.navigate(Rotas.perfilMembro(id)) },
                    onAdicionar = { nav.navigate(Rotas.ADICIONAR_MEMBRO) },
                )
            }
            composable(Rotas.ADICIONAR_MEMBRO) { TelaAdicionarMembro(onVoltar = { nav.popBackStack() }) }
            composable(
                route = Rotas.PERFIL_MEMBRO,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { backEntry ->
                val id = backEntry.arguments?.getInt("id") ?: 0
                TelaPerfilMembro(
                    id = id,
                    onVoltar = { nav.popBackStack() },
                    onEditar = { nav.navigate(Rotas.editarMembro(id)) },
                )
            }
            composable(
                route = Rotas.EDITAR_MEMBRO,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { backEntry ->
                val id = backEntry.arguments?.getInt("id") ?: 0
                TelaEditarMembro(
                    id = id,
                    onVoltar = { nav.popBackStack() },
                    onRemovido = {
                        // volta para a lista da Equipe se ela estiver na pilha; senão, para a tela anterior ao perfil
                        if (!nav.popBackStack(Rotas.EQUIPE, inclusive = false)) {
                            nav.popBackStack(Rotas.PERFIL_MEMBRO, inclusive = true)
                        }
                    },
                )
            }
        }
    }
}

/** Barra flutuante marrom com cantos arredondados. A aba ativa vira uma pílula laranja com o nome. */
@Composable
private fun BarraInferior(rotaAtual: String?, onSelecionar: (String) -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(Cores.Marrom)
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            itensBarra.forEach { item ->
                val selecionado = item.rota == rotaAtual
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .then(if (selecionado) Modifier.background(Cores.Laranja) else Modifier)
                        .clickable { onSelecionar(item.rota) }
                        .padding(horizontal = if (selecionado) 18.dp else 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Image(
                        painter = painterResource(item.icone),
                        contentDescription = item.rotulo,
                        modifier = Modifier.size(34.dp),
                        colorFilter = if (selecionado) ColorFilter.tint(Cores.Marrom) else null,
                    )
                    if (selecionado) {
                        Text(item.rotulo, color = Cores.Marrom, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
