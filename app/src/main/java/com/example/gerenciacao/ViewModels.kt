package com.example.gerenciacao.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gerenciacao.GerenciaCaoApplication
import com.example.gerenciacao.di.AppContainer
import com.example.gerenciacao.domain.Resultado
import com.example.gerenciacao.domain.model.Abrigo
import com.example.gerenciacao.domain.model.AtualizacaoMembro
import com.example.gerenciacao.domain.model.EstadoSessao
import com.example.gerenciacao.domain.model.Membro
import com.example.gerenciacao.domain.model.NovoMembro
import com.example.gerenciacao.domain.repository.AbrigoRepository
import com.example.gerenciacao.domain.repository.AuthRepository
import com.example.gerenciacao.domain.repository.EquipeRepository
import com.example.gerenciacao.domain.validacao.Validadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Camada de APRESENTAÇÃO: cada ViewModel guarda o estado de uma tela e chama os repositórios (interfaces do domínio).
// As telas (Composables) só desenham o estado e avisam o ViewModel dos cliques.

/** Cria um ViewModel recebendo o [AppContainer] (injeção manual). */
@Composable
inline fun <reified VM : ViewModel> viewModelDoApp(key: String? = null, crossinline criar: (AppContainer) -> VM): VM {
    val app = LocalContext.current.applicationContext as GerenciaCaoApplication
    return viewModel(key = key, factory = viewModelFactory { initializer { criar(app.container) } })
}

// ------------------------------------------------------------------ Sessão

/** Estado da sessão para a navegação (quem está logado, ou ninguém). */
class SessaoViewModel(private val auth: AuthRepository) : ViewModel() {
    /** Estado atual da sessão. */
    val estado: StateFlow<EstadoSessao> = auth.estado

    init {
        viewModelScope.launch { auth.restaurarSessao() }
    }

    /** Sai da conta neste aparelho. */
    fun sair() = auth.sair()
}

// ------------------------------------------------------------------ Login / Cadastro / Convite

/** Estado das telas de acesso. */
data class UiAuth(val carregando: Boolean = false, val erro: String? = null)

/** Tela de Login. Quando dá certo a sessão muda e a navegação leva ao Início sozinha. */
class LoginViewModel(private val auth: AuthRepository) : ViewModel() {
    private val _estado = MutableStateFlow(UiAuth())
    val estado: StateFlow<UiAuth> = _estado.asStateFlow()

    /** Valida os campos e tenta entrar. */
    fun entrar(email: String, senha: String) {
        Validadores.validarLogin(email, senha)?.let { _estado.value = UiAuth(erro = it); return }
        _estado.value = UiAuth(carregando = true)
        viewModelScope.launch {
            _estado.value = when (val r = auth.login(email, senha)) {
                is Resultado.Sucesso -> UiAuth()
                is Resultado.Erro -> UiAuth(erro = r.erro.textoParaUsuario())
            }
        }
    }
}

/** Tela de Cadastro. */
class CadastroViewModel(private val auth: AuthRepository) : ViewModel() {
    private val _estado = MutableStateFlow(UiAuth())
    val estado: StateFlow<UiAuth> = _estado.asStateFlow()

    /** Valida os campos e cria a conta. */
    fun cadastrar(nome: String, email: String, telefone: String, senha: String) {
        Validadores.validarCadastro(nome, email, senha)?.let { _estado.value = UiAuth(erro = it); return }
        _estado.value = UiAuth(carregando = true)
        viewModelScope.launch {
            _estado.value = when (val r = auth.cadastrar(nome, email, telefone, senha)) {
                is Resultado.Sucesso -> UiAuth()
                is Resultado.Erro -> UiAuth(erro = r.erro.textoParaUsuario())
            }
        }
    }
}

/** Tela de aceitar convite (a pessoa convidada cria a senha com o código recebido). */
class AceitarConviteViewModel(private val auth: AuthRepository) : ViewModel() {
    private val _estado = MutableStateFlow(UiAuth())
    val estado: StateFlow<UiAuth> = _estado.asStateFlow()

    /** Valida e conclui o convite. */
    fun aceitar(codigo: String, senha: String) {
        val erroLocal = if (codigo.isBlank()) "Digite o código do convite." else Validadores.validarSenhaNova(senha)
        if (erroLocal != null) { _estado.value = UiAuth(erro = erroLocal); return }
        _estado.value = UiAuth(carregando = true)
        viewModelScope.launch {
            _estado.value = when (val r = auth.aceitarConvite(codigo, senha)) {
                is Resultado.Sucesso -> UiAuth()
                is Resultado.Erro -> UiAuth(erro = r.erro.textoParaUsuario())
            }
        }
    }
}

// ------------------------------------------------------------------ Equipe

/** Estado da lista da equipe. */
data class UiEquipe(val carregando: Boolean = false, val membros: List<Membro> = emptyList(), val erro: String? = null)

/** Lista de membros. */
class EquipeViewModel(private val repo: EquipeRepository) : ViewModel() {
    private val _estado = MutableStateFlow(UiEquipe())
    val estado: StateFlow<UiEquipe> = _estado.asStateFlow()

    /** (Re)carrega a equipe; mantém a lista antiga na tela enquanto atualiza. */
    fun carregar() {
        _estado.update { it.copy(carregando = true, erro = null) }
        viewModelScope.launch {
            when (val r = repo.listar()) {
                is Resultado.Sucesso -> _estado.value = UiEquipe(membros = r.dados.sortedBy { it.nome.lowercase() })
                is Resultado.Erro -> _estado.update { it.copy(carregando = false, erro = r.erro.textoParaUsuario()) }
            }
        }
    }
}

/** Estado de um membro (perfil e edição). */
data class UiMembro(
    val carregando: Boolean = false,
    val membro: Membro? = null,
    val erro: String? = null,
    val salvando: Boolean = false,
    val erroAcao: String? = null,
    val concluido: Boolean = false,
    val removido: Boolean = false,
)

/** Perfil, edição e remoção de um membro. */
class MembroViewModel(private val repo: EquipeRepository) : ViewModel() {
    private val _estado = MutableStateFlow(UiMembro())
    val estado: StateFlow<UiMembro> = _estado.asStateFlow()

    /** Carrega o membro. */
    fun carregar(id: String) {
        _estado.update { it.copy(carregando = true, erro = null) }
        viewModelScope.launch {
            when (val r = repo.buscar(id)) {
                is Resultado.Sucesso -> _estado.update { it.copy(carregando = false, membro = r.dados) }
                is Resultado.Erro -> _estado.update { it.copy(carregando = false, erro = r.erro.textoParaUsuario()) }
            }
        }
    }

    /** Salva as alterações; ao terminar marca [UiMembro.concluido]. */
    fun salvar(id: String, alteracao: AtualizacaoMembro) {
        _estado.update { it.copy(salvando = true, erroAcao = null) }
        viewModelScope.launch {
            when (val r = repo.atualizar(id, alteracao)) {
                is Resultado.Sucesso -> _estado.update { it.copy(salvando = false, membro = r.dados, concluido = true) }
                is Resultado.Erro -> _estado.update { it.copy(salvando = false, erroAcao = r.erro.textoParaUsuario()) }
            }
        }
    }

    /** Remove o membro; ao terminar marca [UiMembro.removido]. */
    fun remover(id: String) {
        _estado.update { it.copy(salvando = true, erroAcao = null) }
        viewModelScope.launch {
            when (val r = repo.remover(id)) {
                is Resultado.Sucesso -> _estado.update { it.copy(salvando = false, removido = true) }
                is Resultado.Erro -> _estado.update { it.copy(salvando = false, erroAcao = r.erro.textoParaUsuario()) }
            }
        }
    }
}

/** Estado da tela Adicionar Membro. [codigoConvite] aparece quando o convite foi criado. */
data class UiConvite(
    val salvando: Boolean = false,
    val erro: String? = null,
    val codigoConvite: String? = null,
    val emailConvidado: String = "",
    val concluido: Boolean = false,
)

/** Convida um novo membro. */
class ConviteViewModel(private val repo: EquipeRepository) : ViewModel() {
    private val _estado = MutableStateFlow(UiConvite())
    val estado: StateFlow<UiConvite> = _estado.asStateFlow()

    /** Valida e cria o convite. O código é mostrado a quem convidou (ainda não há envio por e-mail). */
    fun convidar(novo: NovoMembro?, nome: String, email: String) {
        Validadores.validarNovoMembro(nome, email, novo != null)?.let { _estado.value = UiConvite(erro = it); return }
        _estado.value = UiConvite(salvando = true)
        viewModelScope.launch {
            _estado.value = when (val r = repo.convidar(requireNotNull(novo))) {
                is Resultado.Sucesso -> UiConvite(codigoConvite = r.dados.codigo, emailConvidado = r.dados.membro.email)
                is Resultado.Erro -> UiConvite(erro = r.erro.textoParaUsuario())
            }
        }
    }

    /** Fecha o aviso do código e volta à lista. */
    fun fecharConvite() = _estado.update { it.copy(codigoConvite = null, concluido = true) }
}

// ------------------------------------------------------------------ Abrigo

/** Estado das informações do abrigo. */
data class UiAbrigo(val carregando: Boolean = false, val abrigo: Abrigo? = null, val erro: String? = null)

/** Informações do abrigo (tela Informações do abrigo e cartão de ocupação do Início). */
class AbrigoViewModel(private val repo: AbrigoRepository) : ViewModel() {
    private val _estado = MutableStateFlow(UiAbrigo())
    val estado: StateFlow<UiAbrigo> = _estado.asStateFlow()

    /** Carrega o abrigo. */
    fun carregar() {
        _estado.update { it.copy(carregando = true, erro = null) }
        viewModelScope.launch {
            when (val r = repo.buscar()) {
                is Resultado.Sucesso -> _estado.value = UiAbrigo(abrigo = r.dados)
                is Resultado.Erro -> _estado.update { it.copy(carregando = false, erro = r.erro.textoParaUsuario()) }
            }
        }
    }
}
