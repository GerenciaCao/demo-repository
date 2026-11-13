package com.example.gerenciacao.ui.telas

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gerenciacao.R
import com.example.gerenciacao.ui.*
import com.example.gerenciacao.ui.componentes.MensagemErro
import com.example.gerenciacao.ui.viewmodel.UiAuth

/**
 * Estrutura comum de Login/Cadastro/Convite: topo creme com "Bem-vindo ao" + logo + foto dos bichinhos
 * e painel azul arredondado por cima (rola e sobe junto com o teclado).
 * TODO: padrão de patinhas (vector drawable repetido) no fundo do painel azul.
 */
@Composable
private fun FundoAuth(
    alturaTopo: Dp,
    @DrawableRes foto: Int,
    conteudo: @Composable ColumnScope.() -> Unit,
) {
    Box(Modifier.fillMaxSize().background(Cores.Creme)) {
        Image(
            painter = painterResource(foto),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomEnd,
            modifier = Modifier.align(Alignment.TopEnd).height(alturaTopo + 12.dp),
        )
        Column(Modifier.statusBarsPadding().padding(start = 20.dp, top = 24.dp)) {
            Text("Bem-vindo ao", color = Cores.Laranja, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Image(
                painter = painterResource(R.drawable.logo_gerenciacao),
                contentDescription = "GerenciaCão",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(200.dp),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = alturaTopo)
                .background(Cores.Azul, RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp))
                .imePadding()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 30.dp, vertical = 32.dp),
            content = conteudo,
        )
    }
}

/** Tela de Login. Os estados (carregando/erro) vêm do ViewModel; a tela só desenha e avisa os cliques. */
@Composable
fun TelaLogin(
    estado: UiAuth,
    onEntrar: (email: String, senha: String) -> Unit,
    onCadastrar: () -> Unit,
    onEsqueceuSenha: () -> Unit,
    onTenhoConvite: () -> Unit,
    onSocial: (String) -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    FundoAuth(alturaTopo = 250.dp, foto = R.drawable.pets_login) {
        Text("Login", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)
        Spacer(Modifier.height(24.dp))
        CampoCreme(email, { email = it }, "E-mail", teclado = KeyboardType.Email)
        Spacer(Modifier.height(24.dp))
        CampoCreme(senha, { senha = it }, "Senha", senha = true)
        Text(
            "Esqueceu a senha?",
            color = Cores.Marrom,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.End).padding(top = 6.dp).clickable(onClick = onEsqueceuSenha),
        )
        estado.erro?.let { MensagemErro(it) }
        Spacer(Modifier.height(20.dp))
        BotaoLaranja(if (estado.carregando) "Entrando..." else "Acessar", { onEntrar(email, senha) }, enabled = !estado.carregando)
        Spacer(Modifier.height(16.dp))
        BotoesSociais("Ou faça login pelo", onSocial)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.align(Alignment.CenterHorizontally)) {
            Text("Não tem uma conta?  ", fontSize = 12.sp, color = Cores.Marrom)
            Text("Cadastre-se", fontSize = 12.sp, color = Color(0xFFFFEB3B), modifier = Modifier.clickable(onClick = onCadastrar))
        }
        Text(
            "Tenho um código de convite",
            fontSize = 12.sp,
            color = Cores.Marrom,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp).clickable(onClick = onTenhoConvite),
        )
    }
}

/** Tela de Cadastro (nome, e-mail, telefone e senha) com os botões de Google e Microsoft. */
@Composable
fun TelaCadastro(
    estado: UiAuth,
    onCadastrar: (nome: String, email: String, telefone: String, senha: String) -> Unit,
    onIrParaLogin: () -> Unit,
    onSocial: (String) -> Unit,
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    FundoAuth(alturaTopo = 200.dp, foto = R.drawable.pet_cadastro) {
        Text("Cadastre-se", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)
        Spacer(Modifier.height(24.dp))
        CampoCreme(nome, { nome = it }, "Nome")
        Spacer(Modifier.height(24.dp))
        CampoCreme(email, { email = it }, "E-mail", teclado = KeyboardType.Email)
        Spacer(Modifier.height(24.dp))
        CampoCreme(telefone, { telefone = it }, "Número de telefone", teclado = KeyboardType.Phone)
        Spacer(Modifier.height(24.dp))
        CampoCreme(senha, { senha = it }, "Senha", senha = true)
        Text("Mínimo de 8 caracteres, com letras e números.", fontSize = 12.sp, color = Cores.Marrom, modifier = Modifier.padding(top = 6.dp))
        estado.erro?.let { MensagemErro(it) }
        Spacer(Modifier.height(20.dp))
        BotaoLaranja(if (estado.carregando) "Criando conta..." else "Confirmar", { onCadastrar(nome, email, telefone, senha) }, enabled = !estado.carregando)
        Spacer(Modifier.height(16.dp))
        BotoesSociais("Ou cadastre-se pelo", onSocial)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.align(Alignment.CenterHorizontally)) {
            Text("Já possui uma conta?  ", fontSize = 12.sp, color = Cores.Marrom)
            Text("Clique aqui e faça o login", fontSize = 12.sp, color = Color(0xFFFFEB3B), modifier = Modifier.clickable(onClick = onIrParaLogin))
        }
    }
}

/** Tela provisória para a pessoa convidada criar a senha usando o código do convite. (Aguardando o design.) */
@Composable
fun TelaAceitarConvite(estado: UiAuth, onAceitar: (codigo: String, senha: String) -> Unit, onVoltar: () -> Unit) {
    var codigo by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    FundoAuth(alturaTopo = 200.dp, foto = R.drawable.pet_cadastro) {
        Text("Convite", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)
        Text("Digite o código que você recebeu e escolha uma senha.", fontSize = 13.sp, color = Cores.Marrom, modifier = Modifier.padding(top = 6.dp))
        Spacer(Modifier.height(20.dp))
        CampoCreme(codigo, { codigo = it }, "Código do convite")
        Spacer(Modifier.height(24.dp))
        CampoCreme(senha, { senha = it }, "Nova senha", senha = true)
        estado.erro?.let { MensagemErro(it) }
        Spacer(Modifier.height(20.dp))
        BotaoLaranja(if (estado.carregando) "Entrando..." else "Confirmar", { onAceitar(codigo, senha) }, enabled = !estado.carregando)
        Text(
            "Voltar ao login",
            fontSize = 12.sp,
            color = Color(0xFFFFEB3B),
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 14.dp).clickable(onClick = onVoltar),
        )
    }
}

/** Linha "Ou ... pelo" com os botões do Google e da Microsoft (a autenticação por eles ainda não existe). */
@Composable
private fun ColumnScope.BotoesSociais(titulo: String, onSocial: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f).height(1.dp).background(Cores.Marrom))
        Text("  $titulo  ", fontSize = 12.sp, color = Cores.Marrom)
        Box(Modifier.weight(1f).height(1.dp).background(Cores.Marrom))
    }
    Spacer(Modifier.height(12.dp))
    Row(Modifier.align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
        BotaoSocial(R.drawable.ic_google, "Google") { onSocial("Google") }
        BotaoSocial(R.drawable.ic_microsoft, "Microsoft") { onSocial("Microsoft") }
    }
}

/** Botão redondo com o ícone de um provedor. */
@Composable
private fun BotaoSocial(@DrawableRes icone: Int, descricao: String, onClick: () -> Unit) {
    Image(
        painter = painterResource(icone),
        contentDescription = "Entrar com $descricao",
        modifier = Modifier.size(56.dp).clip(CircleShape).clickable(onClick = onClick),
    )
}
