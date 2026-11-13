package com.example.gerenciacao.navegacao

import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gerenciacao.R
import com.example.gerenciacao.domain.model.EstadoSessao
import com.example.gerenciacao.ui.Cores
import com.example.gerenciacao.ui.telas.*
import com.example.gerenciacao.ui.viewmodel.AbrigoViewModel
import com.example.gerenciacao.ui.viewmodel.AceitarConviteViewModel
import com.example.gerenciacao.ui.viewmodel.CadastroViewModel
import com.example.gerenciacao.ui.viewmodel.ConviteViewModel
import com.example.gerenciacao.ui.viewmodel.EquipeViewModel
import com.example.gerenciacao.ui.viewmodel.LoginViewModel
import com.example.gerenciacao.ui.viewmodel.MembroViewModel
import com.example.gerenciacao.ui.viewmodel.SessaoViewModel
import com.example.gerenciacao.ui.viewmodel.viewModelDoApp

/** Nomes das rotas (telas) do app. */
object Rotas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val CADASTRO = "cadastro"
    const val ACEITAR_CONVITE = "aceitar_convite"
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

    /** Rota da ficha de um animal. */
    fun ficha(id: Int) = "ficha/$id"

    /** Rota do perfil de um membro (o id vem da API e é codificado para ir na rota). */
    fun perfilMembro(id: String) = "perfil_membro/${Uri.encode(id)}"

    /** Rota da edição de um membro. */
    fun editarMembro(id: String) = "editar_membro/${Uri.encode(id)}"
}

private data class ItemBarra(val rota: String, val rotulo: String, @DrawableRes val icone: Int)

private val itensBarra = listOf(
    ItemBarra(Rotas.INICIO, "Início", R.drawable.nav_inicio),
    ItemBarra(Rotas.ANIMAIS, "Animais", R.drawable.nav_animais),
    ItemBarra(Rotas.FINANCAS, "Dados", R.drawable.nav_dados),
    ItemBarra(Rotas.AGENDA, "Agenda", R.drawable.nav_agenda),
)

/** Troca de aba mantendo o estado de cada uma. */
private fun NavController.irParaAba(rota: String) = navigate(rota) {
    popUpTo(Rotas.INICIO) { saveState = true }
    launchSingleTop = true
    restoreState = true
}

/** Vai para [rota] limpando toda a pilha (usado ao entrar e ao sair da conta). */
private fun NavController.limparEIrPara(rota: String) = navigate(rota) {
    popUpTo(graph.id) { inclusive = true }
    launchSingleTop = true
}

/**
 * Raiz do app: guarda o NavHost e reage à SESSÃO. Entrar/sair da conta muda a tela sozinho:
 * as telas e ViewModels não precisam saber para onde ir (separação de responsabilidades).
 */
@Composable
fun GerenciaCaoApp() {
    val nav = rememberNavController()
    val contexto = LocalContext.current
    val sessaoVm = viewModelDoApp { SessaoViewModel(it.authRepository) }
    val sessao by sessaoVm.estado.collectAsStateWithLifecycle()
    val entrada by nav.currentBackStackEntryAsState()
    val rotaAtual = entrada?.destination?.route
    var splashConcluida by remember { mutableStateOf(false) }
    val usuario = (sessao as? EstadoSessao.Logado)?.usuario

    // Navegação guiada pela sessão
    LaunchedEffect(sessao, rotaAtual, splashConcluida) {
        val s = sessao
        val rotasPublicas = setOf(Rotas.LOGIN, Rotas.CADASTRO, Rotas.ACEITAR_CONVITE)
        when {
            rotaAtual == null -> Unit
            rotaAtual == Rotas.SPLASH -> if (splashConcluida) {
                when (s) {
                    is EstadoSessao.Logado -> nav.limparEIrPara(Rotas.INICIO)
                    EstadoSessao.SemSessao -> nav.limparEIrPara(Rotas.LOGIN)
                    EstadoSessao.Verificando -> Unit
                }
            }
            s is EstadoSessao.Logado && rotaAtual in rotasPublicas -> nav.limparEIrPara(Rotas.INICIO)
            s is EstadoSessao.SemSessao && rotaAtual !in rotasPublicas -> nav.limparEIrPara(Rotas.LOGIN)
        }
    }

    val irConfig: () -> Unit = { nav.navigate(Rotas.CONFIG) { launchSingleTop = true } }
    val irPerfil: () -> Unit = { usuario?.let { nav.navigate(Rotas.perfilMembro(it.id)) { launchSingleTop = true } } }
    val irParcerias: () -> Unit = { nav.navigate(Rotas.PARCERIAS) { launchSingleTop = true } }
    val avisar: (String) -> Unit = { Toast.makeText(contexto, it, Toast.LENGTH_SHORT).show() }

    // Na Ficha Individual a barra continua visível, com a aba Animais marcada
    val abaAtual = if (rotaAtual == Rotas.FICHA) Rotas.ANIMAIS else rotaAtual

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
        NavHost(navController = nav, startDestination = Rotas.SPLASH, modifier = Modifier.padding(padding)) {

            composable(Rotas.SPLASH) { TelaSplash(onConcluir = { splashConcluida = true }) }

            composable(Rotas.LOGIN) {
                val vm = viewModelDoApp { LoginViewModel(it.authRepository) }
                val ui by vm.estado.collectAsStateWithLifecycle()
                TelaLogin(
                    estado = ui,
                    onEntrar = vm::entrar,
                    onCadastrar = { nav.navigate(Rotas.CADASTRO) },
                    onEsqueceuSenha = { avisar("Recuperar senha estará disponível em breve.") },
                    onTenhoConvite = { nav.navigate(Rotas.ACEITAR_CONVITE) },
                    onSocial = { avisar("Entrar com $it estará disponível em breve.") },
                )
            }
            composable(Rotas.CADASTRO) {
                val vm = viewModelDoApp { CadastroViewModel(it.authRepository) }
                val ui by vm.estado.collectAsStateWithLifecycle()
                TelaCadastro(
                    estado = ui,
                    onCadastrar = vm::cadastrar,
                    onIrParaLogin = { nav.popBackStack() },
                    onSocial = { avisar("Cadastro com $it estará disponível em breve.") },
                )
            }
            composable(Rotas.ACEITAR_CONVITE) {
                val vm = viewModelDoApp { AceitarConviteViewModel(it.authRepository) }
                val ui by vm.estado.collectAsStateWithLifecycle()
                TelaAceitarConvite(estado = ui, onAceitar = vm::aceitar, onVoltar = { nav.popBackStack() })
            }

            composable(Rotas.INICIO) {
                val abrigoVm = viewModelDoApp { AbrigoViewModel(it.abrigoRepository) }
                val abrigo by abrigoVm.estado.collectAsStateWithLifecycle()
                LaunchedEffect(Unit) { abrigoVm.carregar() }
                TelaInicio(
                    nomeUsuario = usuario?.nome.orEmpty(),
                    abrigo = abrigo.abrigo,
                    onVerAgenda = { nav.irParaAba(Rotas.AGENDA) },
                    onVerDados = { nav.irParaAba(Rotas.FINANCAS) },
                    onConfig = irConfig,
                    onAvatar = irPerfil,
                    onParcerias = irParcerias,
                    onConexoes = { nav.navigate(Rotas.CONEXOES) },
                )
            }
            composable(Rotas.ANIMAIS) {
                TelaAnimais(onConfig = irConfig, onAvatar = irPerfil, onAbrirFicha = { id -> nav.navigate(Rotas.ficha(id)) })
            }
            composable(Rotas.FICHA, arguments = listOf(navArgument("id") { type = NavType.IntType })) { be ->
                TelaFicha(id = be.arguments?.getInt("id") ?: 0, onVoltar = { nav.popBackStack() })
            }
            composable(Rotas.FINANCAS) { TelaFinancas(onConfig = irConfig, onAvatar = irPerfil) }
            composable(Rotas.AGENDA) {
                val equipeVm = viewModelDoApp { EquipeViewModel(it.equipeRepository) }
                val equipe by equipeVm.estado.collectAsStateWithLifecycle()
                LaunchedEffect(Unit) { equipeVm.carregar() }
                TelaAgenda(onConfig = irConfig, onAvatar = irPerfil, responsaveis = equipe.membros.map { it.nomeCompleto })
            }

            composable(Rotas.CONFIG) {
                TelaConfiguracoes(
                    onVoltar = { nav.popBackStack() },
                    onEquipe = { nav.navigate(Rotas.EQUIPE) },
                    onParcerias = irParcerias,
                    onMinhaConta = irPerfil,
                    onAbrigo = { nav.navigate(Rotas.ABRIGO) },
                    onSair = sessaoVm::sair,
                )
            }
            composable(Rotas.ABRIGO) {
                val abrigoVm = viewModelDoApp { AbrigoViewModel(it.abrigoRepository) }
                val equipeVm = viewModelDoApp { EquipeViewModel(it.equipeRepository) }
                val abrigo by abrigoVm.estado.collectAsStateWithLifecycle()
                val equipe by equipeVm.estado.collectAsStateWithLifecycle()
                LaunchedEffect(Unit) {
                    abrigoVm.carregar()
                    equipeVm.carregar()
                }
                TelaAbrigo(abrigo, equipe.membros, onVoltar = { nav.popBackStack() }, onTentarNovamente = abrigoVm::carregar)
            }
            composable(Rotas.PARCERIAS) { TelaParcerias(onVoltar = { nav.popBackStack() }) }
            composable(Rotas.CONEXOES) { TelaConexao(onVoltar = { nav.popBackStack() }) }

            composable(Rotas.EQUIPE) {
                val vm = viewModelDoApp { EquipeViewModel(it.equipeRepository) }
                val ui by vm.estado.collectAsStateWithLifecycle()
                LaunchedEffect(Unit) { vm.carregar() }
                TelaEquipe(
                    estado = ui,
                    onVoltar = { nav.popBackStack() },
                    onAbrirMembro = { id -> nav.navigate(Rotas.perfilMembro(id)) },
                    onAdicionar = { nav.navigate(Rotas.ADICIONAR_MEMBRO) },
                    onTentarNovamente = vm::carregar,
                )
            }
            composable(Rotas.ADICIONAR_MEMBRO) {
                val vm = viewModelDoApp { ConviteViewModel(it.equipeRepository) }
                val ui by vm.estado.collectAsStateWithLifecycle()
                TelaAdicionarMembro(estado = ui, onConvidar = vm::convidar, onVoltar = { nav.popBackStack() }, onFecharConvite = vm::fecharConvite)
            }
            composable(Rotas.PERFIL_MEMBRO, arguments = listOf(navArgument("id") { type = NavType.StringType })) { be ->
                val id = be.arguments?.getString("id").orEmpty()
                val vm = viewModelDoApp { MembroViewModel(it.equipeRepository) }
                val ui by vm.estado.collectAsStateWithLifecycle()
                LaunchedEffect(id) { vm.carregar(id) }
                TelaPerfilMembro(
                    estado = ui,
                    onVoltar = { nav.popBackStack() },
                    onEditar = { nav.navigate(Rotas.editarMembro(id)) },
                    onTentarNovamente = { vm.carregar(id) },
                )
            }
            composable(Rotas.EDITAR_MEMBRO, arguments = listOf(navArgument("id") { type = NavType.StringType })) { be ->
                val id = be.arguments?.getString("id").orEmpty()
                val vm = viewModelDoApp { MembroViewModel(it.equipeRepository) }
                val ui by vm.estado.collectAsStateWithLifecycle()
                LaunchedEffect(id) { vm.carregar(id) }
                TelaEditarMembro(
                    estado = ui,
                    onVoltar = { nav.popBackStack() },
                    onSalvar = { alteracao -> vm.salvar(id, alteracao) },
                    onRemover = { vm.remover(id) },
                    onSalvo = { nav.popBackStack() },
                    onRemovido = {
                        // Removeu a própria conta: sai. Removeu outra pessoa: volta para a lista da Equipe.
                        if (id == usuario?.id) sessaoVm.sair()
                        else if (!nav.popBackStack(Rotas.EQUIPE, inclusive = false)) nav.popBackStack(Rotas.PERFIL_MEMBRO, inclusive = true)
                    },
                    onTentarNovamente = { vm.carregar(id) },
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
                    if (selecionado) Text(item.rotulo, color = Cores.Marrom, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
