// Arquivo de build da raiz do projeto. As versões ficam em gradle/libs.versions.toml.
// Com o AGP 9 o Kotlin já vem embutido: NÃO existe mais o plugin "kotlin-android".
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
