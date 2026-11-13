package com.example.gerenciacao.data.remote

import com.example.gerenciacao.domain.model.Abrigo
import com.example.gerenciacao.domain.model.Cargo
import com.example.gerenciacao.domain.model.Membro
import com.example.gerenciacao.domain.model.Permissao
import com.example.gerenciacao.domain.model.StatusConta

// Conversões DTO (formato da API) -> modelo de domínio (formato que as telas usam).

/** Converte o usuário da API em [Membro]. Valores desconhecidos recebem um padrão seguro (menos acesso). */
fun UsuarioDto.paraDominio() = Membro(
    id = id,
    abrigoId = abrigoId,
    nome = nome,
    sobrenome = sobrenome,
    email = email,
    telefone = telefone,
    endereco = endereco,
    cargo = runCatching { Cargo.valueOf(cargo) }.getOrDefault(Cargo.VOLUNTARIO),
    permissoes = permissoes.mapNotNull { runCatching { Permissao.valueOf(it) }.getOrNull() }.toSet(),
    status = runCatching { StatusConta.valueOf(status) }.getOrDefault(StatusConta.INATIVO),
)

/** Converte o abrigo da API em [Abrigo]. */
fun AbrigoDto.paraDominio() = Abrigo(
    id = id,
    nome = nome,
    descricao = descricao,
    telefone = telefone,
    email = email,
    endereco = endereco,
    redesSociais = redesSociais,
    capacidade = capacidade,
    animaisTotais = animaisTotais,
)
