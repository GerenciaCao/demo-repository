package com.example.gerenciacao.ui.telas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gerenciacao.R
import com.example.gerenciacao.ui.*

// ================================================================ EQUIPE (lista)

@Composable
fun TelaEquipe(
    onVoltar: () -> Unit,
    onAbrirMembro: (Int) -> Unit,
    onAdicionar: () -> Unit,
) {
    var busca by remember { mutableStateOf("") }
    val lista = EquipeMock.membros.filter { it.nomeCompleto.contains(busca, ignoreCase = true) }

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
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
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
                    AvatarMembro(membro.foto, tamanho = 60.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(membro.nomeCompleto, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)
                        Text(membro.cargoRotulo, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Cores.CinzaTexto)
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
    foto: Int? = null,
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
            foto = foto,
            tamanho = 116.dp,
            modifier = Modifier.align(Alignment.TopCenter).border(3.dp, Color.White, CircleShape),
        )
        canto()
    }
}

@Composable
private fun TopoVoltar(onVoltar: () -> Unit) {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
        contentDescription = "Voltar",
        tint = Cores.Marrom,
        modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp).size(36.dp).clickable(onClick = onVoltar),
    )
}

@Composable
private fun TituloBloco(texto: String) {
    Text(texto, fontSize = 24.sp, color = Cores.Marrom, modifier = Modifier.padding(top = 18.dp, bottom = 6.dp))
}

@Composable
fun TelaPerfilMembro(id: Int, onVoltar: () -> Unit, onEditar: () -> Unit) {
    val membro = EquipeMock.buscar(id)
    if (membro == null) {
        LaunchedEffect(Unit) { onVoltar() }
        return
    }

    Column(Modifier.fillMaxSize().background(Cores.Creme).statusBarsPadding()) {
        TopoVoltar(onVoltar)
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 14.dp),
        ) {
            CartaoMembro(foto = membro.foto) {
                Text(membro.nomeCompleto, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)
                Text(membro.cargoRotulo, fontSize = 20.sp, color = Cores.CinzaTexto)
            }
            TituloBloco("Informações Pessoais")
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(horizontal = 10.dp)) {
                CampoLeitura("E-mail", membro.email)
                CampoLeitura("Telefone", membro.telefone)
                CampoLeitura("Endereço", membro.endereco)
            }
            TituloBloco("Permissões de Acesso")
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(horizontal = 10.dp)) {
                if (membro.permissoes.isEmpty()) {
                    Text("Nenhuma permissão concedida.", fontSize = 16.sp, color = Cores.CinzaTexto)
                }
                Permissao.values().filter { it in membro.permissoes }.forEach {
                    Text(
                        it.rotulo,
                        fontSize = 18.sp,
                        color = Cores.Marrom,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Cores.AzulCinza, RoundedCornerShape(4.dp))
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        BotaoAcao(
            texto = "Editar Perfil",
            onClick = onEditar,
            icone = Icons.Filled.Edit,
            modifier = Modifier.align(Alignment.CenterHorizontally).width(270.dp).padding(bottom = 0.dp),
        )
        Spacer(Modifier.navigationBarsPadding().height(16.dp))
    }
}

// ================================================================ EDITAR MEMBRO

@Composable
fun TelaEditarMembro(id: Int, onVoltar: () -> Unit, onRemovido: () -> Unit) {
    val original = EquipeMock.buscar(id)
    if (original == null) {
        LaunchedEffect(Unit) { onVoltar() }
        return
    }

    var cargo by remember(id) { mutableStateOf<Cargo?>(original.cargo) }
    var email by remember(id) { mutableStateOf(original.email) }
    var telefone by remember(id) { mutableStateOf(original.telefone) }
    var endereco by remember(id) { mutableStateOf(original.endereco) }
    var permissoes by remember(id) { mutableStateOf(original.permissoes) }
    var confirmarExclusao by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(Cores.Creme).statusBarsPadding().imePadding()) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 14.dp),
        ) {
            Spacer(Modifier.height(30.dp))
            CartaoMembro(
                foto = original.foto,
                canto = {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar nome",
                        tint = Cores.Marrom,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 74.dp, end = 18.dp)
                            .size(22.dp)
                            .clickable { /* TODO: editar nome do membro */ },
                    )
                },
            ) {
                Text(
                    "Editar Foto",
                    fontSize = 12.sp,
                    color = Cores.Marrom,
                    modifier = Modifier.clickable { /* TODO: escolher foto (galeria/câmera) */ },
                )
                Text(original.nomeCompleto, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)
            }

            TituloBloco("Cargo")
            Box(Modifier.padding(horizontal = 10.dp)) {
                SeletorLista(
                    opcoes = Cargo.values().toList(),
                    selecionado = cargo,
                    rotulo = { it.rotulo(original.feminino) },
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
                    LinhaPermissao(p.rotulo, p in permissoes) {
                        permissoes = if (p in permissoes) permissoes - p else permissoes + p
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(Modifier.padding(horizontal = 10.dp), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                BotaoAcao("Cancelar", onVoltar, Modifier.weight(1f))
                BotaoAcao(
                    "Salvar",
                    onClick = {
                        EquipeMock.atualizar(
                            original.copy(
                                cargo = cargo ?: original.cargo,
                                email = email.trim(),
                                telefone = telefone.trim(),
                                endereco = endereco.trim(),
                                permissoes = permissoes,
                            ),
                        )
                        onVoltar()
                    },
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(12.dp))
            BotaoAcao(
                "Remover Conta",
                onClick = { confirmarExclusao = true },
                modifier = Modifier.align(Alignment.CenterHorizontally).width(270.dp),
            )
            Spacer(Modifier.navigationBarsPadding().height(20.dp))
        }
    }

    if (confirmarExclusao) {
        DialogoExcluirConta(
            onCancelar = { confirmarExclusao = false },
            onConfirmar = {
                confirmarExclusao = false
                EquipeMock.remover(id) // TODO: chamar a API de remoção (RF07)
                onRemovido()
            },
        )
    }
}

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

@Composable
fun TelaAdicionarMembro(onVoltar: () -> Unit) {
    var nome by remember { mutableStateOf("") }
    var sobrenome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var endereco by remember { mutableStateOf("") }
    var cargo by remember { mutableStateOf<Cargo?>(null) }
    var tentouSalvar by remember { mutableStateOf(false) }

    val fundo = Cores.AzulCampo
    val borda = Cores.AmareloBorda
    val faltaAlgo = nome.isBlank() || !email.contains("@") || cargo == null

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
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = x.dp, y = y.dp)
                        .size(22.dp)
                        .rotate(giro)
                        .alpha(0.25f),
                )
            }
            Image(
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
            rotulo = { it.rotulo() },
            aoSelecionar = { cargo = it },
            dica = "Selecione um Cargo",
            fundo = fundo,
            borda = borda,
        )

        if (tentouSalvar && faltaAlgo) {
            Text(
                "Preencha o nome, um e-mail válido e o cargo.",
                fontSize = 14.sp,
                color = Cores.VermelhoBotao,
                modifier = Modifier.padding(top = 10.dp),
            )
        }

        Spacer(Modifier.height(30.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            BotaoAcao("Cancelar", onVoltar, Modifier.weight(1f))
            BotaoAcao(
                "Salvar",
                onClick = {
                    tentouSalvar = true
                    if (!faltaAlgo) {
                        EquipeMock.adicionar(
                            Membro(
                                id = 0, // o id real é definido ao adicionar
                                nome = nome.trim(),
                                sobrenome = sobrenome.trim(),
                                email = email.trim(),
                                telefone = telefone.trim(),
                                endereco = endereco.trim(),
                                cargo = cargo!!,
                                permissoes = setOf(Permissao.VER_FICHAS),
                            ),
                        ) // TODO: chamar a API / convite por token (RF06)
                        onVoltar()
                    }
                },
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.navigationBarsPadding().height(24.dp))
    }
}
