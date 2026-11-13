package com.example.gerenciacao.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gerenciacao.R
import com.example.gerenciacao.domain.model.AtualizacaoMembro
import com.example.gerenciacao.domain.model.Cargo
import com.example.gerenciacao.domain.model.Membro
import com.example.gerenciacao.domain.model.NovoMembro
import com.example.gerenciacao.domain.model.Permissao
import com.example.gerenciacao.ui.*
import com.example.gerenciacao.ui.componentes.EstadoCarregando
import com.example.gerenciacao.ui.componentes.EstadoErro
import com.example.gerenciacao.ui.componentes.MensagemErro
import com.example.gerenciacao.ui.viewmodel.UiConvite
import com.example.gerenciacao.ui.viewmodel.UiEquipe
import com.example.gerenciacao.ui.viewmodel.UiMembro

// ================================================================ EQUIPE (lista)

/** Lista de membros do abrigo, com busca. Os dados vêm da API pelo ViewModel. */
@Composable
fun TelaEquipe(
    estado: UiEquipe,
    onVoltar: () -> Unit,
    onAbrirMembro: (String) -> Unit,
    onAdicionar: () -> Unit,
    onTentarNovamente: () -> Unit,
) {
    var busca by remember { mutableStateOf("") }
    val lista = estado.membros.filter { it.nomeCompleto.contains(busca, ignoreCase = true) }

    BaseTelaAzul(onVoltar = onVoltar) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Text("Equipe", fontSize = 36.sp, fontWeight = FontWeight.Black, color = Cores.Marrom, modifier = Modifier.padding(start = 18.dp))
            }
            item {
                BarraBusca(
                    valor = busca,
                    aoMudar = { busca = it },
                    dica = "Pesquisar membro",
                    onFiltro = { /* TODO: filtrar membros por cargo */ },
                    corBorda = null,
                    corBotao = Cores.Azul,
                    corIconeBotao = Cores.AmareloChip,
                    sombra = true,
                )
            }
            item {
                Text(
                    "Adicionar Membros",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Cores.Marrom,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(12.dp))
                        .background(Cores.Azul, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onAdicionar)
                        .padding(vertical = 14.dp),
                )
            }
            item { Text("Membros", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom, modifier = Modifier.padding(top = 6.dp)) }
            if (estado.carregando && estado.membros.isEmpty()) item { EstadoCarregando() }
            if (estado.erro != null && estado.membros.isEmpty()) item { EstadoErro(estado.erro, onTentarNovamente) }
            items(lista, key = { it.id }) { membro ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(20.dp))
                        .background(Cores.Creme, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onAbrirMembro(membro.id) }
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AvatarMembro(null, tamanho = 60.dp) // TODO: foto do membro (endpoint de fotos)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(membro.nomeCompleto, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)
                        Text(membro.cargo.rotulo, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Cores.CinzaTexto)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Cores.Marrom, modifier = Modifier.padding(end = 8.dp))
                }
            }
        }
    }
}

// ================================================================ PERFIL DO MEMBRO

/** Cartão azul-acinzentado com a foto sobrepondo o topo. [conteudo] é o que vem abaixo da foto. */
@Composable
private fun CartaoMembro(
    modifier: Modifier = Modifier,
    canto: @Composable BoxScope.() -> Unit = {},
    conteudo: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier.fillMaxWidth().padding(top = 8.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp)
                .background(Cores.AzulCinza, RoundedCornerShape(24.dp))
                .padding(top = 66.dp, bottom = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = conteudo,
        )
        AvatarMembro(
            foto = null, // TODO: foto do membro
            tamanho = 116.dp,
            modifier = Modifier.align(Alignment.TopCenter).border(3.dp, Color.White, CircleShape),
        )
        canto()
    }
}

/** Seta de voltar no topo das telas de perfil. */
@Composable
private fun TopoVoltar(onVoltar: () -> Unit) {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
        contentDescription = "Voltar",
        tint = Cores.Marrom,
        modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp).size(36.dp).clickable(onClick = onVoltar),
    )
}

/** Título de bloco ("Informações Pessoais", "Cargo"...). */
@Composable
private fun TituloBloco(texto: String) {
    Text(texto, fontSize = 24.sp, color = Cores.Marrom, modifier = Modifier.padding(top = 18.dp, bottom = 6.dp))
}

/** Perfil de um membro (somente leitura) com o botão Editar Perfil. */
@Composable
fun TelaPerfilMembro(estado: UiMembro, onVoltar: () -> Unit, onEditar: () -> Unit, onTentarNovamente: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Cores.Creme).statusBarsPadding()) {
        TopoVoltar(onVoltar)
        val membro = estado.membro
        when {
            membro != null -> {
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 14.dp)) {
                    CartaoMembro {
                        Text(membro.nomeCompleto, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)
                        Text(membro.cargo.rotulo, fontSize = 20.sp, color = Cores.CinzaTexto)
                    }
                    TituloBloco("Informações Pessoais")
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(horizontal = 10.dp)) {
                        CampoLeitura("E-mail", membro.email)
                        CampoLeitura("Telefone", membro.telefone)
                        CampoLeitura("Endereço", membro.endereco)
                    }
                    TituloBloco("Permissões de Acesso")
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(horizontal = 10.dp)) {
                        if (membro.permissoes.isEmpty()) Text("Nenhuma permissão concedida.", fontSize = 16.sp, color = Cores.CinzaTexto)
                        Permissao.values().filter { it in membro.permissoes }.forEach {
                            Text(
                                it.rotulo,
                                fontSize = 18.sp,
                                color = Cores.Marrom,
                                modifier = Modifier.fillMaxWidth().background(Cores.AzulCinza, RoundedCornerShape(4.dp)).padding(horizontal = 20.dp, vertical = 14.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
                BotaoAcao("Editar Perfil", onEditar, icone = Icons.Filled.Edit, modifier = Modifier.align(Alignment.CenterHorizontally).width(270.dp))
                Spacer(Modifier.navigationBarsPadding().height(16.dp))
            }
            estado.carregando -> EstadoCarregando()
            else -> EstadoErro(estado.erro ?: "Não foi possível carregar.", onTentarNovamente)
        }
    }
}

// ================================================================ EDITAR MEMBRO

/** Edição de um membro: cargo, dados pessoais e permissões. Salvar e Remover chamam a API pelo ViewModel. */
@Composable
fun TelaEditarMembro(
    estado: UiMembro,
    onVoltar: () -> Unit,
    onSalvar: (AtualizacaoMembro) -> Unit,
    onRemover: () -> Unit,
    onSalvo: () -> Unit,
    onRemovido: () -> Unit,
    onTentarNovamente: () -> Unit,
) {
    LaunchedEffect(estado.concluido) { if (estado.concluido) onSalvo() }
    LaunchedEffect(estado.removido) { if (estado.removido) onRemovido() }

    val original = estado.membro
    if (original == null) {
        Column(Modifier.fillMaxSize().background(Cores.Creme).statusBarsPadding()) {
            TopoVoltar(onVoltar)
            if (estado.carregando) EstadoCarregando() else EstadoErro(estado.erro ?: "Não foi possível carregar.", onTentarNovamente)
        }
        return
    }

    var cargo by remember(original.id) { mutableStateOf<Cargo?>(original.cargo) }
    var email by remember(original.id) { mutableStateOf(original.email) }
    var telefone by remember(original.id) { mutableStateOf(original.telefone) }
    var endereco by remember(original.id) { mutableStateOf(original.endereco) }
    var permissoes by remember(original.id) { mutableStateOf(original.permissoes) }
    var confirmarExclusao by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(Cores.Creme).statusBarsPadding().imePadding()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 14.dp)) {
            Spacer(Modifier.height(30.dp))
            CartaoMembro(
                canto = {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar nome",
                        tint = Cores.Marrom,
                        modifier = Modifier.align(Alignment.TopEnd).padding(top = 74.dp, end = 18.dp).size(22.dp).clickable { /* TODO: editar nome */ },
                    )
                },
            ) {
                Text("Editar Foto", fontSize = 12.sp, color = Cores.Marrom, modifier = Modifier.clickable { /* TODO: escolher foto */ })
                Text(original.nomeCompleto, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)
            }

            TituloBloco("Cargo")
            Box(Modifier.padding(horizontal = 10.dp)) {
                SeletorLista(
                    opcoes = Cargo.values().toList(),
                    selecionado = cargo,
                    rotulo = { it.rotulo },
                    aoSelecionar = { cargo = it },
                    dica = "Selecione um Cargo",
                )
            }

            TituloBloco("Informações Pessoais")
            Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                RotuloCampo("E-mail", 14)
                CampoFormulario(email, { email = it }, "E-mail", sombra = true, teclado = KeyboardType.Email)
                RotuloCampo("Telefone", 14)
                CampoFormulario(telefone, { telefone = it }, "(XX) XXXXX-XXXX", sombra = true, teclado = KeyboardType.Phone)
                RotuloCampo("Endereço", 14)
                CampoFormulario(endereco, { endereco = it }, "Endereço", sombra = true)
            }

            TituloBloco("Permissões de Acesso")
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(horizontal = 10.dp)) {
                Permissao.values().forEach { p ->
                    LinhaPermissao(p.rotulo, p in permissoes) { permissoes = if (p in permissoes) permissoes - p else permissoes + p }
                }
            }
            estado.erroAcao?.let { MensagemErro(it, Modifier.padding(horizontal = 10.dp)) }
            Spacer(Modifier.height(18.dp))
            Row(Modifier.padding(horizontal = 10.dp), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                BotaoAcao("Cancelar", onVoltar, Modifier.weight(1f))
                BotaoAcao(
                    texto = if (estado.salvando) "Salvando..." else "Salvar",
                    onClick = { onSalvar(AtualizacaoMembro(email = email, telefone = telefone, endereco = endereco, cargo = cargo, permissoes = permissoes)) },
                    modifier = Modifier.weight(1f),
                    enabled = !estado.salvando,
                )
            }
            Spacer(Modifier.height(12.dp))
            BotaoAcao("Remover Conta", { confirmarExclusao = true }, Modifier.align(Alignment.CenterHorizontally).width(270.dp), enabled = !estado.salvando)
            Spacer(Modifier.navigationBarsPadding().height(20.dp))
        }
    }

    if (confirmarExclusao) {
        DialogoExcluirConta(
            onCancelar = { confirmarExclusao = false },
            onConfirmar = {
                confirmarExclusao = false
                onRemover()
            },
        )
    }
}

/** Diálogo de confirmação "Excluir Conta". */
@Composable
private fun DialogoExcluirConta(onCancelar: () -> Unit, onConfirmar: () -> Unit) {
    Dialog(onDismissRequest = onCancelar, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(20.dp))
                .background(Cores.AzulDialogo, RoundedCornerShape(20.dp))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text("Excluir Conta", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(Modifier.height(16.dp))
            Text("Tem certeza que deseja deletar sua Conta?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(Modifier.height(6.dp))
            Text(
                "Ao confirmar, sua conta e todos os dados associados serão excluídos permanentemente. Essa ação não poderá ser desfeita.",
                fontSize = 18.sp,
                color = Color.Black,
            )
            Spacer(Modifier.height(26.dp))
            Text("Você realmente deseja continuar?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                BotaoAcao("Cancelar", onCancelar, Modifier.weight(1f))
                BotaoAcao("Deletar", onConfirmar, Modifier.weight(1f), corFundo = Cores.VermelhoBotao)
            }
        }
    }
}

// ================================================================ ADICIONAR MEMBRO

/** Formulário "Adicionar Membro". Ao salvar, a API cria o convite e o código aparece num aviso. */
@Composable
fun TelaAdicionarMembro(
    estado: UiConvite,
    onConvidar: (novo: NovoMembro?, nome: String, email: String) -> Unit,
    onVoltar: () -> Unit,
    onFecharConvite: () -> Unit,
) {
    LaunchedEffect(estado.concluido) { if (estado.concluido) onVoltar() }

    var nome by remember { mutableStateOf("") }
    var sobrenome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var endereco by remember { mutableStateOf("") }
    var cargo by remember { mutableStateOf<Cargo?>(null) }
    val fundo = Cores.AzulCampo
    val borda = Cores.AmareloBorda

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cores.Creme)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        Text("Adicionar Membro", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)

        // Ilustração: logo + patinhas decorativas. TODO: carinhas de gato/cachorro do protótipo.
        Box(Modifier.fillMaxWidth().height(170.dp), contentAlignment = Alignment.Center) {
            listOf(
                Triple(12, 20, -20f), Triple(70, 90, 15f), Triple(250, 10, 10f), Triple(300, 100, -15f),
                Triple(20, 120, 20f), Triple(310, 40, 25f), Triple(40, 10, -10f), Triple(280, 135, 5f),
            ).forEach { (x, y, giro) ->
                Icon(
                    imageVector = Icons.Filled.Pets,
                    contentDescription = null,
                    tint = Cores.Laranja,
                    modifier = Modifier.align(Alignment.TopStart).offset(x = x.dp, y = y.dp).size(22.dp).rotate(giro).alpha(0.25f),
                )
            }
            androidx.compose.foundation.Image(
                painter = painterResource(R.drawable.logo_laranja),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(130.dp),
            )
        }

        RotuloCampo("Nome")
        CampoFormulario(nome, { nome = it }, "Digite o Nome", fundo = fundo, borda = borda, sombra = true)
        RotuloCampo("Sobrenome")
        CampoFormulario(sobrenome, { sobrenome = it }, "Digite o Sobrenome", fundo = fundo, borda = borda, sombra = true)
        RotuloCampo("E-mail")
        CampoFormulario(email, { email = it }, "Digite o E-mail", fundo = fundo, borda = borda, sombra = true, teclado = KeyboardType.Email)
        RotuloCampo("Telefone/Celular")
        CampoFormulario(telefone, { telefone = it }, "(XX) XXXXX-XXXX", fundo = fundo, borda = borda, sombra = true, teclado = KeyboardType.Phone)
        RotuloCampo("Endereço")
        CampoFormulario(endereco, { endereco = it }, "Digite o Endereço", fundo = fundo, borda = borda, sombra = true)

        Spacer(Modifier.height(18.dp))
        Text("Cargo", fontSize = 16.sp, color = Cores.Marrom, modifier = Modifier.padding(bottom = 4.dp))
        SeletorLista(
            opcoes = Cargo.values().toList(),
            selecionado = cargo,
            rotulo = { it.rotulo },
            aoSelecionar = { cargo = it },
            dica = "Selecione um Cargo",
            fundo = fundo,
            borda = borda,
        )

        estado.erro?.let { MensagemErro(it) }

        Spacer(Modifier.height(30.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            BotaoAcao("Cancelar", onVoltar, Modifier.weight(1f))
            BotaoAcao(
                texto = if (estado.salvando) "Salvando..." else "Salvar",
                onClick = {
                    val novo = cargo?.let { NovoMembro(nome, sobrenome, email, telefone, endereco, it) }
                    onConvidar(novo, nome, email)
                },
                modifier = Modifier.weight(1f),
                enabled = !estado.salvando,
            )
        }
        Spacer(Modifier.navigationBarsPadding().height(24.dp))
    }

    estado.codigoConvite?.let { DialogoConvite(it, estado.emailConvidado, onFecharConvite) }
}

/** Aviso com o código do convite (válido por 7 dias). Enquanto não há envio por e-mail, quem convidou repassa o código. */
@Composable
private fun DialogoConvite(codigo: String, email: String, onFechar: () -> Unit) {
    Dialog(onDismissRequest = onFechar, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(20.dp))
                .background(Cores.AzulDialogo, RoundedCornerShape(20.dp))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text("Convite criado!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(Modifier.height(12.dp))
            Text("Envie este código para $email. Ele vale por 7 dias e aparece só agora.", fontSize = 16.sp, color = Color.Black)
            Spacer(Modifier.height(14.dp))
            SelectionContainer {
                Text(
                    codigo,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Cores.Marrom,
                    modifier = Modifier.fillMaxWidth().background(Cores.Creme, RoundedCornerShape(10.dp)).padding(12.dp),
                )
            }
            Text("Toque e segure o código para copiar.", fontSize = 12.sp, color = Color.Black, modifier = Modifier.padding(top = 6.dp))
            Spacer(Modifier.height(16.dp))
            BotaoAcao("Entendi", onFechar, Modifier.fillMaxWidth())
        }
    }
}
