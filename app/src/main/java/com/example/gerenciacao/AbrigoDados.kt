package com.example.gerenciacao.ui.telas

/**
 * Dados de EXEMPLO do abrigo, usados pela tela "Informações do abrigo" e pelo card de capacidade do Início.
 * Quando a API existir, isto vira uma chamada ao backend (coleção Abrigo do MongoDB).
 */
object AbrigoMock {
    val nome = "Refúgio dos Peludos"
    val endereco = "Rua Doutor Rodrigo Alves, 721"
    val enderecoCompleto = "Rua Doutor Rodrigo Alves, 721, SP, 07450-1237"
    val distancia = "12 Km" // TODO: calcular a partir da localização do usuário (latitude/longitude do abrigo)
    val descricao =
        "O Abrigo Refúgio dos Peludos nasceu da vontade inabalável de resgatar animais vulneráveis, " +
                "curar as suas feridas físicas e emocionais e prepará-los para o momento mais esperado: " +
                "o dia em que encontram uma família para a vida toda."
    val email = "refugio.peludo@gmail.com"
    val telefone = "(11) 98123-1236"

    val animais = 20
    val capacidade = 60
    val vagas: Int get() = capacidade - animais
}
