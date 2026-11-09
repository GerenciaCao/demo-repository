package com.example.gerenciacao.ui.telas

import com.example.gerenciacao.R

/**
 * Dados de EXEMPLO dos animais, usados pela lista de Animais e pela Ficha Individual.
 * Quando a API existir, isto vira uma chamada ao backend (coleção Animal do MongoDB).
 */
object AnimaisMock {
    val animais = listOf(
        Animal(
            id = 1, nome = "Gabi", femea = true, raca = "Poodle", idade = "4-6 Meses", idadeMeses = 5,
            porte = "Pequeno", status = StatusAnimal.DISPONIVEL, especie = "Cães", foto = R.drawable.pet_gabi,
            pesoKg = 3.2, castrado = true,
            caracteristicas = "Dócil, ativa, dentes em crescimento",
            descricao = "A Gabi chegou ao abrigo ainda filhote, muito carinhosa e curiosa. Adora brincar com bolinhas " +
                    "e se dá bem com outros bichinhos. Está com as vacinas em dia e procura um lar com bastante atenção.",
        ),
        Animal(
            id = 2, nome = "Bobby", femea = false, raca = "Dachshund", idade = "3 Meses", idadeMeses = 3,
            porte = "Pequeno", status = StatusAnimal.EM_ADOCAO, especie = "Cães", foto = R.drawable.pet_bobby,
            pesoKg = 2.1, castrado = false,
            caracteristicas = "Curioso, brincalhão, aprendendo a socializar",
            descricao = "O Bobby é um filhote cheio de energia. Já tem interessados em adotá-lo e está em fase de " +
                    "entrevistas e visitas com as famílias.",
        ),
        Animal(
            id = 3, nome = "Oreo", femea = true, raca = "SRD", idade = "1 Ano", idadeMeses = 12,
            porte = "Pequeno", status = StatusAnimal.EM_TRATAMENTO, especie = "Gatos", foto = R.drawable.pet_oreo,
            pesoKg = 3.8, castrado = true,
            caracteristicas = "Tranquila, gosta de colo",
            descricao = "A Oreo está em tratamento veterinário e se recupera bem. É muito calma e adora um colo " +
                    "quentinho. Assim que estiver liberada, vai procurar um novo lar.",
        ),
    )

    fun buscar(id: Int): Animal? = animais.firstOrNull { it.id == id }
}
