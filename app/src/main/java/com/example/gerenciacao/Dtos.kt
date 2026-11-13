package com.example.gerenciacao.data.remote

import kotlinx.serialization.Serializable

// DTOs = o formato exato do JSON da API. Ficam só na camada de dados; as telas usam os modelos do domínio.

@Serializable data class CadastroRequestDto(val nome: String, val email: String, val telefone: String, val senha: String)
@Serializable data class LoginRequestDto(val email: String, val senha: String)
@Serializable data class AceitarConviteRequestDto(val token: String, val senha: String)

@Serializable
data class UsuarioDto(
    val id: String,
    val abrigoId: String,
    val nome: String,
    val sobrenome: String = "",
    val email: String,
    val telefone: String = "",
    val endereco: String = "",
    val fotoUrl: String? = null,
    val cargo: String,
    val permissoes: List<String> = emptyList(),
    val status: String,
    val passouPeloOnboard: Boolean = false,
)

@Serializable
data class AuthResponseDto(val token: String, val tipo: String = "Bearer", val expiraEm: String = "", val usuario: UsuarioDto)

@Serializable
data class NovoMembroRequestDto(
    val nome: String,
    val sobrenome: String,
    val email: String,
    val telefone: String,
    val endereco: String,
    val cargo: String,
    val permissoes: List<String>? = null,
)

@Serializable
data class ConviteResponseDto(val membro: UsuarioDto, val conviteToken: String, val conviteExpiraEm: String = "")

@Serializable
data class AtualizarMembroRequestDto(
    val nome: String? = null,
    val sobrenome: String? = null,
    val email: String? = null,
    val telefone: String? = null,
    val endereco: String? = null,
    val cargo: String? = null,
    val permissoes: List<String>? = null,
)

@Serializable
data class AbrigoDto(
    val id: String,
    val nome: String,
    val descricao: String = "",
    val telefone: String = "",
    val email: String = "",
    val endereco: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val redesSociais: String = "",
    val capacidade: Int = 0,
    val fotoUrl: String? = null,
    val animaisTotais: Int = 0,
)

/** Formato de erro da API: {"erro": "...", "campos": {...}}. */
@Serializable
data class ErroDto(val erro: String = "", val campos: Map<String, String>? = null)
