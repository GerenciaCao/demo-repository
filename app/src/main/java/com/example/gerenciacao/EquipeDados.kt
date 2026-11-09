package com.example.gerenciacao.ui.telas

import androidx.annotation.DrawableRes
import androidx.compose.runtime.mutableStateListOf
import com.example.gerenciacao.R

/** Cargos do abrigo (lista do protótipo). O rótulo muda conforme o gênero: Cuidador / Cuidadora. */
enum class Cargo(private val formaMasculina: String, private val formaFeminina: String) {
    ADMINISTRADOR("Administrador", "Administradora"),
    GESTOR("Gestor", "Gestora"),
    CUIDADOR("Cuidador", "Cuidadora"),
    VETERINARIO("Veterinário", "Veterinária"),
    ADESTRADOR("Adestrador", "Adestradora"),
    VOLUNTARIO("Voluntário", "Voluntária");

    fun rotulo(feminino: Boolean = false) = if (feminino) formaFeminina else formaMasculina
}

/** Permissões de acesso mostradas na tela de perfil/edição do membro. */
enum class Permissao(val rotulo: String) {
    VER_FICHAS("Ver Fichas dos Animais"),
    EDITAR_PRONTUARIOS("Editar Prontuários"),
    GERENCIAR_AGENDA("Gerenciar Agenda"),
    GERENCIAR_DADOS("Gerenciar Dados"),
    MODIFICAR_EQUIPE("Modificar Equipe"),
    GERENCIAR_ESTOQUE("Gerenciar Estoque"),
}

data class Membro(
    val id: Int,
    val nome: String,
    val sobrenome: String = "",
    val email: String,
    val telefone: String,
    val endereco: String,
    val cargo: Cargo,
    val feminino: Boolean = false, // só para escolher "Cuidador" x "Cuidadora" na tela
    val permissoes: Set<Permissao> = emptySet(),
    @DrawableRes val foto: Int? = null, // TODO: URL da foto vinda da API
) {
    val nomeCompleto: String get() = "$nome $sobrenome".trim()
    val cargoRotulo: String get() = cargo.rotulo(feminino)
}

/**
 * Dados de EXEMPLO da equipe, só para as telas funcionarem antes da API.
 * Quando o backend existir, isto vira um Repository + ViewModel que chama a API.
 */
object EquipeMock {
    const val usuarioAtualId = 1 // TODO: vir da conta logada

    val membros = mutableStateListOf(
        Membro(1, "Lucas", "Torino", "lucas@exemplo.com", "(11) 90000-0001", "—", Cargo.ADMINISTRADOR, false, Permissao.values().toSet(), foto = R.drawable.membro_padrao),
        Membro(2, "Nikolas", "de Paula", "nikolas@exemplo.com", "(11) 90000-0002", "—", Cargo.GESTOR, false, Permissao.values().toSet() - Permissao.MODIFICAR_EQUIPE, foto = R.drawable.membro_padrao),
        Membro(3, "Giovanna", "Fernandes", "giovanna@exemplo.com", "(11) 90000-0003", "—", Cargo.CUIDADOR, true, setOf(Permissao.VER_FICHAS, Permissao.EDITAR_PRONTUARIOS, Permissao.GERENCIAR_AGENDA), foto = R.drawable.membro_padrao),
        Membro(4, "Emily", "Keury", "emily@exemplo.com", "(11) 90000-0004", "—", Cargo.CUIDADOR, true, setOf(Permissao.VER_FICHAS, Permissao.EDITAR_PRONTUARIOS, Permissao.GERENCIAR_AGENDA), foto = R.drawable.membro_padrao),
        Membro(
            5, "Max", "Arthur", "maxArthuro.40@gmail.com", "(11) 98345-2134", "Rua Firmino Ladeira, 289", Cargo.VOLUNTARIO, false,
            setOf(Permissao.VER_FICHAS, Permissao.EDITAR_PRONTUARIOS, Permissao.GERENCIAR_AGENDA, Permissao.GERENCIAR_ESTOQUE),
            foto = R.drawable.membro_max,
        ),
        Membro(6, "Thales", "Carvalho", "thales@exemplo.com", "(11) 90000-0006", "—", Cargo.ADESTRADOR, false, setOf(Permissao.VER_FICHAS), foto = R.drawable.membro_padrao),
    )

    private var proximoId = 7

    fun buscar(id: Int): Membro? = membros.firstOrNull { it.id == id }

    fun adicionar(membro: Membro) {
        membros.add(membro.copy(id = proximoId++))
    }

    fun atualizar(membro: Membro) {
        val i = membros.indexOfFirst { it.id == membro.id }
        if (i >= 0) membros[i] = membro
    }

    fun remover(id: Int) {
        membros.removeAll { it.id == id }
    }
}
