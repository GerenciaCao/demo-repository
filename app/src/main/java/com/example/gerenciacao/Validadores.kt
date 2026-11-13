package com.example.gerenciacao.domain.validacao

/** Regras de validação dos formulários (puras, sem Android). A API repete as mesmas regras: o servidor é quem manda. */
object Validadores {
    private val EMAIL = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

    /** Confere e-mail e senha preenchidos. Devolve a mensagem de erro ou null se estiver ok. */
    fun validarLogin(email: String, senha: String): String? = when {
        email.isBlank() || senha.isBlank() -> "Preencha o e-mail e a senha."
        !EMAIL.matches(email.trim()) -> "Digite um e-mail válido."
        else -> null
    }

    /** Confere os campos do cadastro. */
    fun validarCadastro(nome: String, email: String, senha: String): String? = when {
        nome.isBlank() -> "Informe o seu nome."
        !EMAIL.matches(email.trim()) -> "Digite um e-mail válido."
        else -> validarSenhaNova(senha)
    }

    /** Senha nova: de 8 a 72 caracteres, com letras e números. */
    fun validarSenhaNova(senha: String): String? = when {
        senha.length < 8 || senha.length > 72 -> "A senha deve ter de 8 a 72 caracteres."
        senha.none { it.isLetter() } || senha.none { it.isDigit() } -> "A senha deve ter letras e números."
        else -> null
    }

    /** Confere os dados de um novo membro. */
    fun validarNovoMembro(nome: String, email: String, temCargo: Boolean): String? = when {
        nome.isBlank() -> "Informe o nome."
        !EMAIL.matches(email.trim()) -> "Digite um e-mail válido."
        !temCargo -> "Escolha um cargo."
        else -> null
    }
}
