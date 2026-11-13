package com.example.gerenciacao

import android.app.Application
import com.example.gerenciacao.di.AppContainer

/** Classe Application: cria o [AppContainer] uma única vez quando o app abre. (Declarada no AndroidManifest.) */
class GerenciaCaoApplication : Application() {
    lateinit var container: AppContainer
        private set

    /** Inicializa as dependências do app. */
    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
