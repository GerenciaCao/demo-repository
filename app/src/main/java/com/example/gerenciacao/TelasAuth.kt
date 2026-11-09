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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gerenciacao.R
import com.example.gerenciacao.ui.*

/**
 * Estrutura comum de Login/Cadastro: topo creme com "Bem-vindo ao" + logo + foto dos bichinhos
 * e painel azul arredondado por cima.
 * TODO: padrão de patinhas (vector drawable repetido) no fundo do painel azul.
 */
@Composable
private fun FundoAuth(
    alturaTopo: androidx.compose.ui.unit.Dp,
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
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 30.dp, vertical = 32.dp),
            content = conteudo,
        )
    }
}

@Composable
fun TelaLogin(
    onAcessar: () -> Unit,
    onCadastrar: () -> Unit,
    onEsqueceuSenha: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    FundoAuth(alturaTopo = 250.dp, foto = R.drawable.pets_login) {
        Text("Login", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Cores.Marrom)
        Spacer(Modifier.height(24.dp))
        CampoCreme(email, { email = it }, "E-mail")
        Spacer(Modifier.height(24.dp))
        CampoCreme(senha, { senha = it }, "Senha", senha = true)
        Text(
            "Esqueceu a senha?",
            color = Cores.Marrom,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 6.dp)
                .clickable(onClick = onEsqueceuSenha),
        )
        Spacer(Modifier.height(24.dp))
        BotaoLaranja("Acessar", onAcessar)
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f).height(1.dp).background(Cores.Marrom))
            Text("  Ou faça login pelo  ", fontSize = 12.sp, color = Cores.Marrom)
            Box(Modifier.weight(1f).height(1.dp).background(Cores.Marrom))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            BotaoSocial(R.drawable.ic_google, "Google") { /* TODO: login Google */ }
            BotaoSocial(R.drawable.ic_microsoft, "Microsoft") { /* TODO: login Microsoft */ }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.align(Alignment.CenterHorizontally)) {
            Text("Não tem uma conta?  ", fontSize = 12.sp, color = Cores.Marrom)
            Text(
                "Cadastre-se",
                fontSize = 12.sp,
                color = Color(0xFFFFEB3B),
                modifier = Modifier.clickable(onClick = onCadastrar),
            )
        }
    }
}

@Composable
fun TelaCadastro(
    onConfirmar: () -> Unit,
    onIrParaLogin: () -> Unit,
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
        CampoCreme(email, { email = it }, "E-mail")
        Spacer(Modifier.height(24.dp))
        CampoCreme(telefone, { telefone = it }, "Número de telefone")
        Spacer(Modifier.height(24.dp))
        CampoCreme(senha, { senha = it }, "Senha", senha = true)
        Spacer(Modifier.height(24.dp))
        BotaoLaranja("Confirmar", onConfirmar)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.align(Alignment.CenterHorizontally)) {
            Text("Já possui uma conta?  ", fontSize = 12.sp, color = Cores.Marrom)
            Text(
                "Clique aqui e faça o login",
                fontSize = 12.sp,
                color = Color(0xFFFFEB3B),
                modifier = Modifier.clickable(onClick = onIrParaLogin),
            )
        }
    }
}

@Composable
private fun BotaoSocial(@DrawableRes icone: Int, descricao: String, onClick: () -> Unit) {
    Image(
        painter = painterResource(icone),
        contentDescription = descricao,
        modifier = Modifier.size(56.dp).clip(CircleShape).clickable(onClick = onClick),
    )
}
